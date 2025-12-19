package com.example.quizzies.viewModel

import androidx.lifecycle.ViewModel
import com.example.quizzies.model.DailyChallenge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DailyChallengesViewModel : ViewModel() {

    private val _challenges = MutableStateFlow<List<DailyChallenge>>(emptyList())
    val challenges: StateFlow<List<DailyChallenge>> = _challenges

    init {
        loadChallenges()
    }

    private fun loadChallenges() {
        _challenges.value = listOf(
            DailyChallenge(
                name = "Solve 5 Math Problems",
                description = "Test your math skills and solve 5 problems.",
                reward = 10
            ),
            DailyChallenge(
                name = "Spell 3 Animal Names",
                description = "Head to the spelling section and spell 3 animal names correctly.",
                reward = 10,
                isCompleted = true
            ),
            DailyChallenge(
                name = "Learn 5 New Letters",
                description = "Explore the alphabet and learn 5 new letters.",
                reward = 5
            )
        )
    }
}
