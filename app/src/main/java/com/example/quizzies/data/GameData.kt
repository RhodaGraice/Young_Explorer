package com.example.quizzies.data

import androidx.annotation.DrawableRes
import com.example.quizzies.R
import kotlin.random.Random

// --- Data Layer ---
data class SpellingWord(val name: String, @param:DrawableRes val imageRes: Int, val category: String)

data class MathProblem(
    val question: String,
    val answer: Int,
    val num1: Int? = null,
    val num2: Int? = null,
    val operator: String? = null,
    val emoji: String? = null,
    val wrongAnswer: Int? = null // Optional wrong answer for targeted distractors
)

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
private val storyVerbs = listOf("gets", "finds", "buys")
private val storyVerbsPast = listOf("gave away", "lost", "sold")
private val storyContainers = listOf("baskets", "boxes", "bags")
private val storyFriends = listOf("friends", '"' + "students" + '"', "kids")
private val shapes = listOf(
    "Circle" to 0, "Oval" to 0, "Triangle" to 3, "Square" to 4, "Rectangle" to 4, "Rhombus" to 4, "Pentagon" to 5, "Hexagon" to 6, "Heptagon" to 7, "Octagon" to 8, "Star" to 10 // Simplified star
)
private val visualMathEmojis = listOf("🍎", "🍊", "🍓", "🍇", "🍉", "🍌", "🥑", "🐶", "🐱", "🐨", "🚗", "🚲", "⚽️", "🏀", "⭐")

fun generateMathProblem(level: Int = 1): MathProblem {
    return when (level) {
        1 -> { // Level 1: Visual Addition/Subtraction with Emojis
            val useAddition = Random.nextBoolean()
            val emojiToUse = visualMathEmojis.random()
            if (useAddition) {
                val num1 = Random.nextInt(1, 6)
                val num2 = Random.nextInt(1, 6)
                MathProblem("$num1 + $num2 = ?", num1 + num2, num1, num2, "+", emoji = emojiToUse)
            } else {
                val num1 = Random.nextInt(4, 11)
                val num2 = Random.nextInt(1, num1)
                MathProblem("$num1 - $num2 = ?", num1 - num2, num1, num2, "-", emoji = emojiToUse)
            }
        }
        2 -> { // Level 2: Text-based Addition/Subtraction
            val useAddition = Random.nextBoolean()
            if (useAddition) {
                val num1 = Random.nextInt(5, 21)
                val num2 = Random.nextInt(5, 21)
                MathProblem("$num1 + $num2 = ?", num1 + num2)
            } else {
                val num1 = Random.nextInt(10, 31)
                val num2 = Random.nextInt(1, num1)
                MathProblem("$num1 - $num2 = ?", num1 - num2)
            }
        }
        3 -> { // Level 3: Simple Multiplication
            val num1 = Random.nextInt(2, 10)
            val num2 = Random.nextInt(2, 10)
            MathProblem("$num1 × $num2 = ?", num1 * num2)
        }
        4 -> { // Level 4: Simple Division
            val answer = Random.nextInt(2, 10)
            val num2 = Random.nextInt(2, 10)
            val num1 = answer * num2
            MathProblem("$num1 ÷ $num2 = ?", answer)
        }
        5 -> { // Level 5: Shapes
            val (shape, sides) = shapes.random()
            MathProblem("How many sides does a $shape have?", sides)
        }
        6 -> { // Level 6: Word Problems (Add/Sub)
            val useAddition = Random.nextBoolean()
            val item = storyItems.random()
            if (useAddition) {
                val num1 = Random.nextInt(5, 15)
                val num2 = Random.nextInt(5, 15)
                val verb = storyVerbs.random()
                MathProblem("You have $num1 $item and $verb $num2 more. How many do you have now?", num1 + num2)
            } else {
                val num1 = Random.nextInt(10, 20)
                val num2 = Random.nextInt(1, num1)
                val verb = storyVerbsPast.random()
                MathProblem("You have $num1 $item and $verb $num2. How many are left?", num1 - num2)
            }
        }
        7 -> { // Level 7: Word Problems (Mul/Div)
            val useMultiplication = Random.nextBoolean()
            if (useMultiplication) {
                val num1 = Random.nextInt(2, 7)
                val num2 = Random.nextInt(2, 7)
                val item = storyItems.random()
                val container = storyContainers.random()
                MathProblem("You have $num1 $container with $num2 $item in each. How many $item in total?", num1 * num2)
            } else {
                val answer = Random.nextInt(2, 7)
                val num2 = Random.nextInt(2, 7)
                val num1 = answer * num2
                val item = storyItems.random()
                val friend = storyFriends.random()
                MathProblem("You have $num1 $item to share among $num2 $friend. How many $item does each get?", answer)
            }
        }
        8 -> { // Level 8: Number Sequences
            val start = Random.nextInt(1, 15)
            val step = Random.nextInt(2, 6)
            val sequence = List(3) { start + it * step }.joinToString(", ")
            val answer = start + 3 * step
            MathProblem("What comes next?\n$sequence, ...", answer)
        }
        9 -> { // Level 9: Addition/Subtraction with larger numbers
            val useAddition = Random.nextBoolean()
            if (useAddition) {
                val num1 = Random.nextInt(20, 101)
                val num2 = Random.nextInt(20, 101)
                MathProblem("$num1 + $num2 = ?", num1 + num2)
            } else {
                val num1 = Random.nextInt(50, 201)
                val num2 = Random.nextInt(10, num1)
                MathProblem("$num1 - $num2 = ?", num1 - num2)
            }
        }
        10 -> { // Level 10: Multiplication/Division with larger numbers
            val useMultiplication = Random.nextBoolean()
            if (useMultiplication) {
                val num1 = Random.nextInt(5, 13)
                val num2 = Random.nextInt(5, 13)
                MathProblem("$num1 × $num2 = ?", num1 * num2)
            } else {
                val answer = Random.nextInt(5, 13)
                val num2 = Random.nextInt(5, 13)
                val num1 = answer * num2
                MathProblem("$num1 ÷ $num2 = ?", answer)
            }
        }
        11 -> { // Level 11: Simple Fractions
            val denominator = Random.nextInt(2, 5) // 2, 3, or 4
            val mult = Random.nextInt(2, 6)
            val num = denominator * mult
            MathProblem("1/$denominator of $num is?", num / denominator)
        }
        12 -> { // Level 12: Mixed Operations (Order of Operations)
            val num1 = Random.nextInt(1, 11)
            val num2 = Random.nextInt(2, 6)
            val num3 = Random.nextInt(2, 6)
            val question = "$num1 + $num2 × $num3 = ?"
            val correctAnswer = num1 + num2 * num3
            val wrongAnswer = (num1 + num2) * num3 // Common mistake: no order of operations
            MathProblem(question, correctAnswer, wrongAnswer = wrongAnswer)
        }
        else -> generateMathProblem(1) // Default to level 1
    }
}

fun getAnswerOptions(problem: MathProblem): List<Int> {
    val options = mutableSetOf(problem.answer)

    // Add the specific wrong answer if it exists and is different from the correct one
    if (problem.wrongAnswer != null && problem.wrongAnswer != problem.answer) {
        options.add(problem.wrongAnswer)
    }

    // Generate random distractors
    while (options.size < 4) {
        val offset = (problem.answer / 4).coerceAtLeast(1) + 2
        val randomOffset = Random.nextInt(-offset, offset + 1)
        val wrongAnswer = problem.answer + randomOffset

        if (wrongAnswer != problem.answer && wrongAnswer >= 0 && !options.contains(wrongAnswer)) {
            options.add(wrongAnswer)
        }
    }
    return options.shuffled()
}
