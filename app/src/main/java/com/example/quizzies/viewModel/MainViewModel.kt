package com.example.quizzies.viewModel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzies.data.DailyChallenge
import com.example.quizzies.data.SpellingWord
import com.example.quizzies.data.createDefaultDailyChallenges
import com.example.quizzies.data.numberAchievements
import com.example.quizzies.data.wordAchievements
import com.example.quizzies.utils.SoundManager
import com.example.quizzies.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val soundManager: SoundManager = SoundManager(application)
    private val userPreferences: UserPreferences = UserPreferences(application)

    private var userListener: ListenerRegistration? = null

    var username by mutableStateOf("")
        private set
    var stars by mutableIntStateOf(0)
        private set
    var profileImageUri by mutableStateOf<Uri?>(null)
        private set
    var streak by mutableIntStateOf(0)
        private set
    var unlockedAchievements by mutableStateOf<List<String>>(emptyList())
        private set
    var dailyChallengesState by mutableStateOf<List<DailyChallenge>>(emptyList())
        private set
    var learnedWords by mutableStateOf<List<String>>(emptyList())
        private set
    var isSoundEnabled by mutableStateOf(false)
        private set

    init {
        isSoundEnabled = userPreferences.isSoundEnabled()
        auth.addAuthStateListener { firebaseAuth ->
            userListener?.remove()
            firebaseAuth.currentUser?.let {
                listenToUserData(it)
            }
        }
    }

    private fun listenToUserData(user: FirebaseUser) {
        val userRef = db.collection("users").document(user.uid)
        userListener = userRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                username = snapshot.getString("username") ?: user.displayName ?: "Player"
                profileImageUri = (snapshot.getString("profileImageUri"))?.toUri()
                stars = (snapshot.getLong("stars") ?: 0L).toInt()
                streak = (snapshot.getLong("streak") ?: 0L).toInt()
                unlockedAchievements = snapshot.get("unlockedAchievements") as? List<String> ?: emptyList()
                learnedWords = snapshot.get("learnedWords") as? List<String> ?: emptyList()

                val lastLoginDate = snapshot.getDate("lastLogin")
                if (isNewDay(lastLoginDate)) {
                    userRef.update("lastLogin", FieldValue.serverTimestamp())
                    resetDailyChallenges(user.uid)
                } else {
                    // Load existing challenges
                    val challenges = snapshot.get("dailyChallenges") as? List<HashMap<String, Any>>
                    if (challenges != null) {
                        dailyChallengesState = challenges.map { DailyChallenge.fromHashMap(it) }
                    } else {
                        // If no challenges, create new ones
                        resetDailyChallenges(user.uid)
                    }
                }
            }
        }
    }
    fun onNewUserCreated(user: FirebaseUser, username: String) {
        val userRef = db.collection("users").document(user.uid)
        val newUser = hashMapOf(
            "username" to username,
            "stars" to 0,
            "streak" to 1,
            "lastLogin" to FieldValue.serverTimestamp(),
            "profileImageUri" to (user.photoUrl?.toString()),
            "unlockedAchievements" to emptyList<String>(),
            "learnedWords" to emptyList<String>(),
        )
        userRef.set(newUser)
        resetDailyChallenges(user.uid)
    }


    private fun resetDailyChallenges(userId: String) {
        val newChallenges = createDefaultDailyChallenges()
        dailyChallengesState = newChallenges
        db.collection("users").document(userId).update("dailyChallenges", newChallenges.map { it.toHashMap() })
    }

    private fun isNewDay(lastLogin: Date?): Boolean {
        if (lastLogin == null) return true
        val lastLoginCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        lastLoginCalendar.time = lastLogin
        val today = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        return lastLoginCalendar.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR) ||
                lastLoginCalendar.get(Calendar.YEAR) != today.get(Calendar.YEAR)
    }

    private fun updateStars(count: Int) {
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).update("stars", FieldValue.increment(count.toLong()))
    }

    private fun updateChallengeProgress(type: String) {
        val user = auth.currentUser ?: return
        val challengeIndex = dailyChallengesState.indexOfFirst { it.type == type && !it.isCompleted }
        if (challengeIndex != -1) {
            val challenge = dailyChallengesState[challengeIndex]
            val newProgress = challenge.progress + 1

            // Create a new list with the updated challenge
            val updatedChallenges = dailyChallengesState.toMutableList()
            updatedChallenges[challengeIndex] = challenge.copy(progress = newProgress)

            // Check if completed
            if (newProgress >= challenge.goal) {
                updatedChallenges[challengeIndex] = challenge.copy(progress = newProgress, isCompleted = true)
                updateStars(challenge.reward)
            }

            // Update the local state and Firestore
            dailyChallengesState = updatedChallenges
            db.collection("users").document(user.uid).update("dailyChallenges", updatedChallenges.map { it.toHashMap() })
        }
    }

    fun onWordCorrect(word: SpellingWord) {
        if (isSoundEnabled) soundManager.playCorrectSound()
        updateStars(1)
        updateChallengeProgress("words")
        checkWordAchievements()
        // Add to learned words
        val user = auth.currentUser ?: return
        db.collection("users").document(user.uid).update("learnedWords", FieldValue.arrayUnion(word.name))
    }

    fun onNumberCorrect() {
        if (isSoundEnabled) soundManager.playCorrectSound()
        updateStars(1)
        updateChallengeProgress("numbers")
        checkNumberAchievements()
    }

    fun onWrongAnswer() {
        if (isSoundEnabled) soundManager.playWrongSound()
    }

    private fun checkWordAchievements() {
        val solvedToday = dailyChallengesState.firstOrNull { it.type == "words" }?.progress ?: 0
        val achievementToUnlock = wordAchievements.firstOrNull { it.requiredCount == solvedToday && !unlockedAchievements.contains(it.id) }

        if (achievementToUnlock != null) {
            val user = auth.currentUser ?: return
            db.collection("users").document(user.uid).update("unlockedAchievements", FieldValue.arrayUnion(achievementToUnlock.id))
        }
    }

    private fun checkNumberAchievements() {
        val solvedToday = dailyChallengesState.firstOrNull { it.type == "numbers" }?.progress ?: 0
        val achievementToUnlock = numberAchievements.firstOrNull { it.requiredCount == solvedToday && !unlockedAchievements.contains(it.id) }

        if (achievementToUnlock != null) {
            val user = auth.currentUser ?: return
            db.collection("users").document(user.uid).update("unlockedAchievements", FieldValue.arrayUnion(achievementToUnlock.id))
        }
    }

    fun onSoundEnabledChange(isEnabled: Boolean) {
        isSoundEnabled = isEnabled
        userPreferences.setSoundEnabled(isEnabled)
    }

    fun onUsernameChanged(newUsername: String) {
        val user = auth.currentUser ?: return
        username = newUsername
        val request = UserProfileChangeRequest.Builder().setDisplayName(newUsername).build()
        user.updateProfile(request)
        db.collection("users").document(user.uid).update("username", newUsername)
    }

    fun onProfileImageChanged(newImageUri: Uri?) {
        val user = auth.currentUser ?: return
        profileImageUri = newImageUri
        val request = UserProfileChangeRequest.Builder().setPhotoUri(newImageUri).build()
        user.updateProfile(request)
        db.collection("users").document(user.uid).update("profileImageUri", newImageUri.toString())
    }

    fun onLogout() {
        auth.signOut()
    }

    override fun onCleared() {
        super.onCleared()
        userListener?.remove()
        soundManager.release()
    }
}

private fun DailyChallenge.toHashMap(): HashMap<String, Any> {
    return hashMapOf(
        "title" to title,
        "progress" to progress,
        "goal" to goal,
        "reward" to reward,
        "type" to type,
        "isCompleted" to isCompleted,
    )
}

private fun DailyChallenge.Companion.fromHashMap(hash: HashMap<String, Any>): DailyChallenge {
    return DailyChallenge(
        title = hash["title"] as String,
        progress = (hash["progress"] as Long).toInt(),
        goal = (hash["goal"] as Long).toInt(),
        reward = (hash["reward"] as Long).toInt(),
        type = hash["type"] as String,
        isCompleted = hash["isCompleted"] as Boolean
    )
}
