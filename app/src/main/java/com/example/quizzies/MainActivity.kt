package com.example.quizzies

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizzies.data.allAchievements
import com.example.quizzies.data.wordsDatabase
import com.example.quizzies.ui.composables.DailyChallengesScreen
import com.example.quizzies.ui.composables.StickerBookScreen
import com.example.quizzies.ui.screens.AlphabetScreen
import com.example.quizzies.ui.screens.CalculationScreen
import com.example.quizzies.ui.screens.DrawingScreen
import com.example.quizzies.ui.screens.LoginScreen
import com.example.quizzies.ui.screens.MainScreen
import com.example.quizzies.ui.screens.MathLevelSelectionScreen
import com.example.quizzies.ui.screens.MemoryMatchScreen
import com.example.quizzies.ui.screens.NumbersScreen
import com.example.quizzies.ui.screens.SettingsScreen
import com.example.quizzies.ui.screens.SignUpScreen
import com.example.quizzies.ui.screens.SpellingBeeScreen
import com.example.quizzies.ui.screens.SpellingMenuScreen
import com.example.quizzies.ui.screens.SplashScreen
import com.example.quizzies.ui.screens.WordDetailScreen
import com.example.quizzies.ui.theme.LetsLearnTheme
import com.example.quizzies.viewModel.MainViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch


// --- App Navigation ---
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavHostController
    private val viewModel: MainViewModel by viewModels()

    private var isGoogleSigningIn by mutableStateOf(false)
    private var loginError by mutableStateOf<String?>(null)
    private var signUpError by mutableStateOf<String?>(null)
    private var isSigningUp by mutableStateOf(false)
    private var isLoggingIn by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        setContent {
            LetsLearnTheme {
                navController = rememberNavController()

                DisposableEffect(auth) {
                    val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        val currentUser = firebaseAuth.currentUser
                        val currentRoute = navController.currentDestination?.route
                        if (currentUser == null) {
                            if (currentRoute !in listOf("login", "signup", "splash")) {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        } else {
                            if (currentRoute in listOf("login", "signup", "splash")) {
                                navController.navigate("main") {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                    }
                    auth.addAuthStateListener(authListener)
                    onDispose {
                        auth.removeAuthStateListener(authListener)
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
                        MainScreen(navController = navController, username = viewModel.username, streak = { viewModel.streak })
                    }
                    composable("spelling_menu") {
                        SpellingMenuScreen(
                            navController = navController,
                            stars = viewModel.stars,
                            profileImageUri = viewModel.profileImageUri,
                            learnedWords = viewModel.learnedWords,
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
                            stars = viewModel.stars,
                            profileImageUri = viewModel.profileImageUri,
                            onCorrect = viewModel::onWordCorrect,
                            onWrong = viewModel::onWrongAnswer,
                            onNavigateUp = { navController.navigateUp() })
                    }
                    composable(
                        "word_detail/{word}",
                        arguments = listOf(navArgument("word") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val wordName = backStackEntry.arguments?.getString("word")
                        val word = wordsDatabase.find { it.name == wordName }
                        if (word != null) {
                            WordDetailScreen(
                                word = word,
                                username = viewModel.username,
                                stars = viewModel.stars,
                                profileImageUri = viewModel.profileImageUri,
                                onCorrect = { viewModel.onWordCorrect(word) },
                                onNavigateUp = { navController.navigateUp() },
                                onNextWord = {
                                    val unlearnedWords = wordsDatabase.filter { it.category == word.category && it.name !in viewModel.learnedWords }
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
                    composable("math_levels") {
                        MathLevelSelectionScreen(navController = navController)
                    }
                    composable(
                        "calculation/{level}",
                        arguments = listOf(navArgument("level") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val level = backStackEntry.arguments?.getInt("level") ?: 1
                        CalculationScreen(
                            username = viewModel.username,
                            stars = viewModel.stars,
                            profileImageUri = viewModel.profileImageUri,
                            level = level,
                            onCorrect = viewModel::onNumberCorrect,
                            onWrong = viewModel::onWrongAnswer,
                            onNavigateUp = { navController.navigateUp() })
                    }
                    composable("sticker_book") {
                        StickerBookScreen(allAchievements = allAchievements, unlockedAchievementIds = viewModel.unlockedAchievements, onNavigateUp = { navController.navigateUp() })
                    }
                    composable("daily_challenges") {
                        DailyChallengesScreen(
                            dailyChallenges = viewModel.dailyChallengesState,
                            onNavigateUp = { navController.navigateUp() })
                    }
                    composable("memory_match") {
                        MemoryMatchScreen(navController = navController)
                    }
                    composable("drawing") {
                        DrawingScreen(navController = navController)
                    }
                    composable("settings") {
                        SettingsScreen(
                            navController = navController,
                            username = viewModel.username,
                            profileImageUri = viewModel.profileImageUri,
                            isSoundEnabled = viewModel.isSoundEnabled,
                            onSoundEnabledChange = viewModel::onSoundEnabledChange,
                            onLogout = { viewModel.onLogout() },
                            onUsernameChanged = { newUsername ->
                                viewModel.onUsernameChanged(newUsername)
                                navController.navigate("main") {
                                    popUpTo("main") { inclusive = true }
                                }
                            },
                            onProfileImageChanged = { newImageUri: Uri? ->
                                viewModel.onProfileImageChanged(newImageUri)
                            }
                        )
                    }
                }
            }
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
                                        viewModel.onNewUserCreated(user, googleIdTokenCredential.displayName ?: "Player")
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
                        user.updateProfile(profileUpdates).addOnCompleteListener { 
                            if (it.isSuccessful) {
                                viewModel.onNewUserCreated(user, username)
                            }
                        }
                    }
                } else {
                    signUpError = task.exception?.message
                }
                isSigningUp = false
            }
    }
}
