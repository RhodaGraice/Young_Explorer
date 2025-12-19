package com.example.quizzies.viewModel

import androidx.lifecycle.ViewModel
import com.example.quizzies.R
import com.example.quizzies.model.Achievement
import com.example.quizzies.model.Sticker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StickerBookViewModel : ViewModel() {

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements

    init {
        loadAchievements()
    }

    private fun loadAchievements() {
        _achievements.value = listOf(
            Achievement(
                name = "First 10 Stars!",
                description = "Earn 10 stars by completing quizzes.",
                sticker = Sticker(name = "10 Stars", icon = R.drawable.ic_launcher_background)
            ),
            Achievement(
                name = "Spelling Master",
                description = "Complete a word category.",
                sticker = Sticker(name = "Spelling Master", icon = R.drawable.ic_launcher_background)
            ),
            Achievement(
                name = "Math Whiz",
                description = "Solve 20 math problems.",
                sticker = Sticker(name = "Math Whiz", icon = R.drawable.ic_launcher_background, isUnlocked = true)
            )
        )
    }
}
