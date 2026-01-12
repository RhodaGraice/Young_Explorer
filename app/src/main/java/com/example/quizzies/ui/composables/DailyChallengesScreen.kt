package com.example.quizzies.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.quizzies.data.DailyChallenge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengesScreen(dailyChallenges: List<DailyChallenge>, onChallengeCompleted: (DailyChallenge) -> Unit, onNavigateUp: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Daily Challenges") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        if (dailyChallenges.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(dailyChallenges) { index, challenge ->
                        ChallengeItem(challenge = challenge, index = index, onChallengeCompleted = onChallengeCompleted)
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeItem(challenge: DailyChallenge, index: Int, onChallengeCompleted: (DailyChallenge) -> Unit) {
    val colors = listOf(
        Color(0xFFE0BBE4), // Light Purple
        Color(0xFF957DAD), // Muted Purple
        Color(0xFFD291BC), // Pinkish
        Color(0xFFFEC8D8), // Lighter Pink
        Color(0xFFB8E0D2)  // Muted Teal
    )
    val cardColor = colors[index % colors.size]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChallengeCompleted(challenge) },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (challenge.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (challenge.isCompleted) "Completed" else "Incomplete",
                tint = if (challenge.isCompleted) Color.Black.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = challenge.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "+${challenge.reward}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}