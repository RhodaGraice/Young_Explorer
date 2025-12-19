package com.example.quizzies.model

data class DailyChallenge(
    val name: String,
    val description: String,
    val isCompleted: Boolean = false,
    val reward: Int
)
