package com.example.quizzies

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType.Companion.StringType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizzies.data.DailyChallenge
import com.example.quizzies.data.SpellingWord
import com.example.quizzies.data.allAchievements
import com.example.quizzies.data.createDefaultDailyChallenges
import com.example.quizzies.data.numberAchievements
import com.example.quizzies.data.wordAchievements
import com.example.quizzies.data.wordsDatabase
import com.example.quizzies.ui.composables.DailyChallengesScreen
import com.example.quizzies.ui.composables.StickerBookScreen
import com.example.quizzies.ui.screens.AlphabetScreen
import com.example.quizzies.ui.screens.CalculationScreen
import com.example.quizzies.ui.screens.LoginScreen
import com.example.quizzies.ui.screens.MainScreen
import com.example.quizzies.ui.screens.NumbersScreen
import com.example.quizzies.ui.screens.SettingsScreen
import com.example.quizzies.ui.screens.SignUpScreen
import com.example.quizzies.ui.screens.SpellingBeeScreen
import com.example.quizzies.ui.screens.SpellingMenuScreen
import com.example.quizzies.ui.screens.SplashScreen
import com.example.quizzies.ui.screens.WordDetailScreen
import com.example.quizzies.ui.theme.LetsLearnTheme
import com.example.quizzies.utils.SoundManager
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


// --- App Navigation ---
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var navController: NavHostController
    private var userListener: ListenerRegistration? = null
    private lateinit var soundManager: SoundManager

    private var username by mutableStateOf("")
    private var stars by mutableIntStateOf(0)
    private var profileImageUri by mutableStateOf<Uri?>(null)
    private var streak by mutableIntStateOf(0)
    private var unlockedAchievements by mutableStateOf<List<String>>(emptyList())
    private var dailyChallengesState by mutableStateOf<List<DailyChallenge>>(emptyList())
    private var learnedWords by mutableStateOf<List<String>>(emptyList())
    private var isSigningUp by mutableStateOf(false)
    private var signUpError by mutableStateOf<String?>(null)
    private var isGoogleSigningIn by mutableStateOf(false)
    private var isLoggingIn by mutableStateOf(false)
    private var loginError by mutableStateOf<String?>(null)
    private var isInitialDataLoaded by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        soundManager = SoundManager(this)

        setContent {
            LetsLearnTheme {
                navController = rememberNavController()

                DisposableEffect(auth) {
                    val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        userListener?.remove()
                        val currentUser = firebaseAuth.currentUser
                        if (currentUser == null) {
                            isInitialDataLoaded = false
                            val currentRoute = navController.currentDestination?.route
                            if (currentRoute !in listOf("login", "signup", "splash")) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } else {
                            if (!isInitialDataLoaded) {
                                navController.navigate("main") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                            listenToUserData(currentUser)
                        }
                    }
                    auth.addAuthStateListener(authListener)
                    onDispose {
                        auth.removeAuthStateListener(authListener)
                        userListener?.remove()
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(navController = navController)
                    }
                    composable("login") {
                        LoginScreen(
                            onGoogleSignInClick = { handleGoogleSignIn() },
                            isLoading = isGoogleSigningIn || isLoggingIn,
                            onSignInClick = { email, password -> handleSignIn(email, password) },
                            onSignUpClick = { navController.navigate("signup") },
                            error = loginError
                        )
                    }
                    composable("signup") {
                        SignUpScreen(
                            isLoading = isSigningUp,
                            onSignUpClick = { email, username, password -> handleSignUp(email, username, password) },
                            onLoginClick = { navController.popBackStack() },
                            error = signUpError
                        )
                    }
                    composable("main") { 
                        MainScreen(navController = navController, username = username, streak = { streak })
                    }
                    composable("spelling_menu") {
                        SpellingMenuScreen(
                            navController = navController,
                            stars = stars,
                            profileImageUri = profileImageUri,
                            learnedWords = learnedWords,
                            onNavigateUp = { navController.navigateUp() })
                    }
                    composable("alphabet_menu") {
                        AlphabetScreen(onNavigateUp = { navController.navigateUp() })
                    }
                    composable("numbers_menu") {
                        NumbersScreen(onNavigateUp = { navController.navigateUp() })
                    }
                    composable("spelling_bee") {
                        SpellingBeeScreen(
                            stars = stars,
                            profileImageUri = profileImageUri,
                            onCorrect = ::onWordCorrect,
                            onWrong = ::onWrongAnswer,
                            onNavigateUp = { navController.navigateUp() })
                    }
                    composable(
                        "word_detail/{word}",
                        arguments = listOf(navArgument("word") { type = StringType })
                    ) { backStackEntry ->
                        val wordName = backStackEntry.arguments?.getString("word")
                        val word = wordsDatabase.find { it.name == wordName }
                        if (word != null) {
                            WordDetailScreen(
                                word = word,
                                username = username,
                                stars = stars,
                                profileImageUri = profileImageUri,
                                onCorrect = { onWordCorrect(word) },
                                onNavigateUp = { navController.navigateUp() },
                                onNextWord = {
                                    val unlearnedWords = wordsDatabase.filter { it.category == word.category && it.name !in learnedWords }
                                    val nextWord = if (unlearnedWords.isNotEmpty()) {
                                        unlearnedWords.random()
                                    } else {
                                        wordsDatabase.filter { it.category == word.category }.random()
                                    }
                                    navController.popBackStack()
                                    navController.navigate("word_detail/${nextWord.name}")
                                }
                            )
                        }
                    }
                    composable("calculation") {
                        CalculationScreen(
                            username = username,
                            stars = stars,
                            profileImageUri = profileImageUri,
                            onCorrect = ::onNumberCorrect,
                            onWrong = ::onWrongAnswer,
                            onNavigateUp = { navController.navigateUp() })
                    }
                    composable("sticker_book") {
                        StickerBookScreen(allAchievements = allAchievements, unlockedAchievementIds = unlockedAchievements, onNavigateUp = { navController.navigateUp() })
                    }
                    composable("daily_challenges") {
                        DailyChallengesScreen(
                            dailyChallenges = dailyChallengesState,
                            onChallengeCompleted = { onChallengeCompleted(it) },
                            onNavigateUp = { navController.navigateUp() })
                    }
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            username = username,
                            onLogout = { auth.signOut() },
                            onUsernameChanged = { newUsername ->
                                auth.currentUser?.uid?.let { uid ->
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(newUsername)
                                        .build()
                                    auth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            username = newUsername
                                            db.collection("users").document(uid).update("username", newUsername)
                                            navController.navigate("main") {
                                                popUpTo("main") { inclusive = true }
                                            }
                                        }
                                    }
                                }
                            },
                            onProfileImageChanged = { newImageUri ->
                                profileImageUri = newImageUri
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }

    @Suppress("UNCHECKED_CAST")
    private fun listenToUserData(user: FirebaseUser) {
        val userRef = db.collection("users").document(user.uid)

        userListener = userRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("MainActivity", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                this.username = snapshot.getString("username") ?: user.displayName ?: "Player"
                this.profileImageUri = (snapshot.getString("profileImageUri"))?.toUri()
                this.stars = (snapshot.getLong("stars") ?: 0L).toInt()

                val lastLoginTimestamp = snapshot.getTimestamp("lastLogin")?.toDate() ?: Date()
                val today = Date()

                if (!isSameDay(lastLoginTimestamp, today)) {
                    val lastLoginCal = Calendar.getInstance().apply { time = lastLoginTimestamp }
                    lastLoginCal.add(Calendar.DAY_OF_YEAR, 1)
                    val newStreak = if (isSameDay(lastLoginCal.time, today)) (snapshot.getLong("streak") ?: 0L) + 1 else 1
                    val defaultChallenges = createDefaultDailyChallenges()
                    userRef.update(
                        "streak", newStreak,
                        "lastLogin", Timestamp.now(),
                        "wordsLearnedToday", hashMapOf("date" to Timestamp.now(), "words" to emptyList<String>()),
                        "numbersSolvedToday", hashMapOf("date" to Timestamp.now(), "count" to 0L),
                        "dailyChallenges", defaultChallenges.map { challenge ->
                            mapOf(
                                "name" to challenge.name,
                                "description" to challenge.description,
                                "reward" to challenge.reward,
                                "isCompleted" to challenge.isCompleted
                            )
                        }
                    )
                    this.dailyChallengesState = defaultChallenges
                } else {
                    this.streak = (snapshot.getLong("streak") ?: 0L).toInt()
                    val dailyChallengesData = snapshot.get("dailyChallenges") as? List<Map<String, Any>>

                    this.dailyChallengesState = dailyChallengesData?.mapNotNull { challengeMap ->
                        try {
                            val name = challengeMap["name"] as? String
                            val description = challengeMap["description"] as? String
                            val reward = (challengeMap["reward"] as? Number)?.toInt()
                            val isCompleted = challengeMap["isCompleted"] as? Boolean

                            if (name != null && description != null && reward != null && isCompleted != null) {
                                DailyChallenge(name, description, reward, isCompleted)
                            } else {
                                null
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Failed to parse daily challenge", e)
                            null
                        }
                    } ?: createDefaultDailyChallenges()
                }

                this.unlockedAchievements = snapshot.get("unlockedAchievements") as? List<String> ?: emptyList()
                this.learnedWords = (snapshot.get("learnedWords") as? List<String>) ?: emptyList()
                isInitialDataLoaded = true

            } else {
                Log.d("MainActivity", "Current data: null. Creating new user...")
                createNewUserInFirestore(user, user.displayName ?: "Player")
            }
        }
    }

    private fun createNewUserInFirestore(user: FirebaseUser, username: String) {
        val userRef = db.collection("users").document(user.uid)
        val uri = user.photoUrl
        val defaultChallenges = createDefaultDailyChallenges()

        val newUser = hashMapOf(
            "username" to username,
            "stars" to 0,
            "streak" to 1,
            "lastLogin" to Timestamp.now(),
            "profileImageUri" to uri?.toString(),
            "unlockedAchievements" to emptyList<String>(),
            "learnedWords" to emptyList<String>(),
            "wordsLearnedToday" to hashMapOf("date" to Timestamp.now(), "words" to emptyList<String>()),
            "numbersSolvedToday" to hashMapOf("date" to Timestamp.now(), "count" to 0L),
            "dailyChallenges" to defaultChallenges.map { challenge -> mapOf("name" to challenge.name, "description" to challenge.description, "reward" to challenge.reward, "isCompleted" to challenge.isCompleted) }
        )

        userRef.set(newUser)
            .addOnSuccessListener { 
                Log.d("MainActivity", "New user created in Firestore with ID: ${user.uid}")
            }
            .addOnFailureListener { e ->
                Log.w("MainActivity", "Error creating new user", e)
            }
    }

    private fun handleGoogleSignIn() {
        isGoogleSigningIn = true
        val credentialManager = CredentialManager.create(this)
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(getString(R.string.default_web_client_id))
            .build()
        val getCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@MainActivity, getCredentialRequest)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val googleIdToken = googleIdTokenCredential.idToken
                        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                        auth.signInWithCredential(authCredential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = task.result?.user
                                    if (user != null && task.result?.additionalUserInfo?.isNewUser == true) {
                                        createNewUserInFirestore(user, googleIdTokenCredential.displayName ?: user.displayName ?: "Player")
                                    }
                                } else {
                                    loginError = task.exception?.message
                                }
                                isGoogleSigningIn = false
                            }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("MainActivity", "Google ID token parsing failed", e)
                        loginError = "Google sign-in failed."
                        isGoogleSigningIn = false
                    }
                } else {
                    loginError = "Sign-in failed. Unexpected credential type."
                    isGoogleSigningIn = false
                }
            } catch (e: GetCredentialException) {
                Log.e("MainActivity", "GetCredentialException", e)
                loginError = "Sign-in failed. Please try again."
                isGoogleSigningIn = false
            }
        }
    }

    private fun handleSignIn(email: String,password: String) {
        isLoggingIn = true
        loginError = null
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    loginError = task.exception?.message
                }
                isLoggingIn = false
            }
    }

    private fun handleSignUp(email: String, username: String,password: String) {
        isSigningUp = true
        signUpError = null
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.user?.let { user ->
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()
                        user.updateProfile(profileUpdates).addOnCompleteListener { _ ->
                            createNewUserInFirestore(user, username)
                         }
                    }
                } else {
                    signUpError = task.exception?.message
                }
                isSigningUp = false
            }
    }

    private fun onWrongAnswer() {
        soundManager.playWrongSound()
    }

    @Suppress("UNCHECKED_CAST")
    private fun onWordCorrect(word: SpellingWord) {
        soundManager.playCorrectSound()
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentStars = snapshot.getLong("stars") ?: 0L
            val newStars = currentStars + 1
            transaction.update(userRef, "stars", newStars)

            val learnedWords = (snapshot.get("learnedWords") as? MutableList<String>) ?: mutableListOf()
            if (!learnedWords.contains(word.name)) {
                learnedWords.add(word.name)
                transaction.update(userRef, "learnedWords", learnedWords)

                val wordsLearnedTodayData = snapshot.get("wordsLearnedToday") as? Map<String, Any>
                val wordsLearnedToday = if (wordsLearnedTodayData != null && isSameDay((wordsLearnedTodayData["date"] as Timestamp).toDate(), Date())) {
                    (wordsLearnedTodayData["words"] as? List<String> ?: emptyList()).toMutableList()
                } else {
                    mutableListOf()
                }
                wordsLearnedToday.add(word.name)
                transaction.set(userRef, hashMapOf(
                    "wordsLearnedToday" to hashMapOf("date" to Timestamp.now(), "words" to wordsLearnedToday)
                ), com.google.firebase.firestore.SetOptions.merge())

                val newUnlockedAchievements = mutableListOf<String>()
                wordAchievements.forEach { achievement ->
                    if (!unlockedAchievements.contains(achievement.id) && learnedWords.size >= achievement.requiredCount) {
                        newUnlockedAchievements.add(achievement.id)
                    }
                }
                if (newUnlockedAchievements.isNotEmpty()) {
                    transaction.update(userRef, "unlockedAchievements", FieldValue.arrayUnion(*newUnlockedAchievements.toTypedArray()))
                }
            }
            newStars
        }.addOnSuccessListener { newStars ->
            stars = newStars.toInt()
        }.addOnFailureListener { e ->
            Log.e("MainActivity", "Word correction transaction failed", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun onNumberCorrect() {
        soundManager.playCorrectSound()
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentStars = snapshot.getLong("stars") ?: 0L
            val newStars = currentStars + 1
            transaction.update(userRef, "stars", newStars)

            val numbersSolvedTodayData = snapshot.get("numbersSolvedToday") as? Map<String, Any>
            val numbersSolvedCount = if (numbersSolvedTodayData != null && isSameDay((numbersSolvedTodayData["date"] as Timestamp).toDate(), Date())) {
                (numbersSolvedTodayData["count"] as? Long ?: 0L)
            } else {
                0L
            }

            val newNumbersSolvedCount = numbersSolvedCount + 1
            transaction.set(userRef, hashMapOf(
                "numbersSolvedToday" to hashMapOf("date" to Timestamp.now(), "count" to newNumbersSolvedCount)
            ), com.google.firebase.firestore.SetOptions.merge())

            val newUnlockedAchievements = mutableListOf<String>()
            numberAchievements.forEach { achievement ->
                if (!unlockedAchievements.contains(achievement.id) && newNumbersSolvedCount.toInt() >= achievement.requiredCount) {
                    newUnlockedAchievements.add(achievement.id)
                }
            }

            if (newUnlockedAchievements.isNotEmpty()) {
                transaction.update(userRef, "unlockedAchievements", FieldValue.arrayUnion(*newUnlockedAchievements.toTypedArray()))
            }
            newStars
        }.addOnSuccessListener { newStars ->
            stars = newStars.toInt()
        }.addOnFailureListener { e ->
            Log.e("MainActivity", "Number correction transaction failed", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun onChallengeCompleted(challenge: DailyChallenge) {
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentStars = snapshot.getLong("stars") ?: 0L
            val newStars = currentStars + challenge.reward
            transaction.update(userRef, "stars", newStars)

            val dailyChallenges = (snapshot.get("dailyChallenges") as? List<Map<String, Any>>)?.mapNotNull { 
                try {
                    val name = it["name"] as? String
                    val description = it["description"] as? String
                    val reward = (it["reward"] as? Number)?.toInt()
                    val isCompleted = it["isCompleted"] as? Boolean

                    if (name != null && description != null && reward != null && isCompleted != null) {
                        DailyChallenge(name, description, reward, isCompleted)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }?.toMutableList() ?: mutableListOf()

            val challengeIndex = dailyChallenges.indexOfFirst { it.name == challenge.name }
            if (challengeIndex != -1) {
                dailyChallenges[challengeIndex] = challenge.copy(isCompleted = true)
                transaction.update(userRef, "dailyChallenges", dailyChallenges.map { c ->
                    mapOf("name" to c.name, "description" to c.description, "reward" to c.reward, "isCompleted" to c.isCompleted)
                 })
            }
            Pair(newStars, dailyChallenges)
        }.addOnSuccessListener { result ->
            stars = result.first.toInt()
            dailyChallengesState = result.second
        }.addOnFailureListener { _ ->
            Log.e("MainActivity", "Challenge completion transaction failed")
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val tz = TimeZone.getTimeZone("UTC")
        val cal1 = Calendar.getInstance(tz).apply { time = date1 }
        val cal2 = Calendar.getInstance(tz).apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
