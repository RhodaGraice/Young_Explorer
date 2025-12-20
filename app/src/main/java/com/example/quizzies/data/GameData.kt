package com.example.quizzies.data

import androidx.annotation.DrawableRes
import com.example.quizzies.R
import kotlin.random.Random

// --- Data Layer ---
data class SpellingWord(val name: String, @param:DrawableRes val imageRes: Int, val category: String)
data class MathProblem(val question: String, val answer: Int)
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val requiredCount: Int,
    @DrawableRes val lockedIcon: Int,
    @DrawableRes val unlockedIcon: Int
)

val wordsDatabase = listOf(
    SpellingWord("APPLE", R.drawable.apple, "Fruit"),
    SpellingWord("BALL", R.drawable.ball, "Toys"),
    SpellingWord("SUN", R.drawable.sun, "Nature"),
    SpellingWord("RABBIT", R.drawable.rabbit, "Animals"),
    SpellingWord("CHICKEN", R.drawable.chicken, "Animals"),
    SpellingWord("WATERFALL", R.drawable.waterfall, "Nature"),
    SpellingWord("BICYCLE", R.drawable.bicycle, "Toys"),
    SpellingWord("COOKIE", R.drawable.cookie, "Food"),
    SpellingWord("COW", R.drawable.cow, "Animals"),
    SpellingWord("SHEEP", R.drawable.sheep, "Animals"),
    SpellingWord("CIRCLE", R.drawable.circle, "Shapes"),
    SpellingWord("SQUARE", R.drawable.square, "Shapes"),
    SpellingWord("TRIANGLE", R.drawable.triangle, "Shapes"),
    SpellingWord("STAR", R.drawable.star, "Shapes"),
    SpellingWord("PENTAGON", R.drawable.pentagon, "Shapes"),
    SpellingWord("RECTANGLE", R.drawable.rectangle, "Shapes"),
    SpellingWord("ORANGE", R.drawable.orange, "Fruit"),
    SpellingWord("BLUE", R.drawable.blue, "Colors"),
    SpellingWord("GREEN", R.drawable.green, "Colors"),
    SpellingWord("RED", R.drawable.red, "Colors"),
    SpellingWord("PURPLE", R.drawable.purple, "Colors"),
    SpellingWord("CUPCAKE", R.drawable.cupcake, "Food"),
    SpellingWord("SAMOSAS", R.drawable.samosas, "Food"),
    SpellingWord("PINEAPPLE", R.drawable.pineapple, "Fruit"),
    SpellingWord("AVOCADO", R.drawable.avocado, "Fruit"),
    SpellingWord("WATERMELON", R.drawable.watermelon, "Fruit"),
    SpellingWord(name = "BURGER", imageRes = R.drawable.burger, category = "Food"),
    SpellingWord(name = "DOUGHNUT", imageRes = R.drawable.doughnut, category = "Food"),

)

val streakAchievements = listOf(
    Achievement("streak_3", "3-Day Streak!", "Logged in for 3 days in a row.", 3, R.drawable.circle, R.drawable.star),
    Achievement("streak_7", "Weekly Wiz!", "Logged in for 7 days in a row.", 7, R.drawable.circle, R.drawable.star),
    Achievement("streak_30", "Monthly Master!", "Logged in for a whole month!", 30, R.drawable.circle, R.drawable.star)
)

val wordAchievements = listOf(
    Achievement("words_5", "Word Learner!", "Learned 5 new words today.", 5, R.drawable.circle, R.drawable.star),
    Achievement("words_10", "Word Whiz!", "Learned 10 new words today.", 10, R.drawable.circle, R.drawable.star)
)

val numberAchievements = listOf(
    Achievement("numbers_5", "Math Whiz!", "Solved 5 number problems today.", 5, R.drawable.circle, R.drawable.star),
    Achievement("numbers_10", "Calculation King!", "Solved 10 number problems today.", 10, R.drawable.circle, R.drawable.star)
)

val allAchievements = streakAchievements + wordAchievements + numberAchievements

val alphabet = ('A'..'Z').toList()

fun getSpellingAnswerOptions(correctWord: SpellingWord): List<SpellingWord> {
    val options = mutableSetOf(correctWord)
    val incorrectWords = wordsDatabase.filter { it.category == correctWord.category && it.name != correctWord.name }
    while (options.size < 3 && incorrectWords.isNotEmpty() && options.size - 1 < incorrectWords.size) {
        options.add(incorrectWords.filterNot(options::contains).random())
    }
    return options.shuffled()
}

private val storyItems = listOf("apples", "pencils", "toys", "books", "stickers", "marbles", "crayons")
private val storyContainers = listOf("baskets", "boxes", "bags")
private val storyFriends = listOf("friends", "students", "kids")

fun generateMathProblem(): MathProblem {
    val operation = Random.nextInt(5)
    return when (operation) {
        0 -> { // Addition
            val num1 = Random.nextInt(1, 10)
            val num2 = Random.nextInt(1, 10)
            MathProblem("$num1 + $num2 = ?", num1 + num2)
        }
        1 -> { // Subtraction
            val num1 = Random.nextInt(5, 15)
            val num2 = Random.nextInt(1, num1)
            MathProblem("$num1 - $num2 = ?", num1 - num2)
        }
        2 -> { // Multiplication Word Problem
            val num1 = Random.nextInt(2, 6)
            val num2 = Random.nextInt(2, 6)
            val item = storyItems.random()
            val container = storyContainers.random()
            MathProblem("You have $num1 $container with $num2 $item in each. How many $item in total?", num1 * num2)
        }
        3 -> { // Division Word Problem
            val answer = Random.nextInt(2, 6)
            val num2 = Random.nextInt(2, 6)
            val num1 = answer * num2
            val item = storyItems.random()
            val friend = storyFriends.random()
            MathProblem("You have $num1 $item to share among $num2 $friend. How many $item does each get?", answer)
        }
        else -> { // Number Sequence
            val start = Random.nextInt(1, 10)
            val step = Random.nextInt(2, 5)
            val sequence = List(3) { start + it * step }.joinToString(", ")
            val answer = start + 3 * step
            MathProblem("What comes next?\n$sequence, ...", answer)
        }
    }
}

fun getAnswerOptions(correctAnswer: Int): List<Int> {
    val options = mutableSetOf(correctAnswer)
    while (options.size < 3) {
        val wrongAnswer = correctAnswer + Random.nextInt(-5, 6)
        if (wrongAnswer != correctAnswer && wrongAnswer >= 0) {
            options.add(wrongAnswer)
        }
    }
    return options.shuffled()
}
