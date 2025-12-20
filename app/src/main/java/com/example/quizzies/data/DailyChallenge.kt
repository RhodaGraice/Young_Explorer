package com.example.quizzies.data

data class DailyChallenge(
    val name: String,
    val description: String,
    val reward: Int,
    var isCompleted: Boolean = false
)

fun createDefaultDailyChallenges(): List<DailyChallenge> {
    return listOf(
        DailyChallenge("Word Wizard", "Spell 5 words correctly", 10),
        DailyChallenge("Math Magician", "Solve 3 math problems", 10),
        DailyChallenge("Alphabet Ace", "Trace 10 letters", 5),
        DailyChallenge("Spelling Bee Ace", "Put 5 words together", 5),
        DailyChallenge("Category Conqueror", "Complete a whole category of words", 20),
        DailyChallenge("Math Whiz", "Solve 5 math problems", 10)
    )
}
