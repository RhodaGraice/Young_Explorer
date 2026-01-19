package com.example.quizzies.data

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DailyChallenge(
    val title: String = "",
    val progress: Int = 0,
    val goal: Int = 0,
    val reward: Int = 0,
    val type: String = "", // "words", "numbers", "login"
    val isCompleted: Boolean = false
) {
    companion object
}

fun createDefaultDailyChallenges(): List<DailyChallenge> {
    return listOf(
        DailyChallenge("Learn 5 new words", 0, 5, 10, "words"),
        DailyChallenge("Solve 10 math problems", 0, 10, 15, "numbers"),
        DailyChallenge("Log in to the app", 1, 1, 5, "login", isCompleted = true) // Auto-completed on creation
    )
}
