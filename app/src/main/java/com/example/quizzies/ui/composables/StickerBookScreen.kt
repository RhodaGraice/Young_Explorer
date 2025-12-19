package com.example.quizzies.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quizzies.data.Achievement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerBookScreen(allAchievements: List<Achievement>, unlockedAchievementIds: List<String>, onNavigateUp: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Sticker Book") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
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
                itemsIndexed(allAchievements) { index, achievement ->
                    val isUnlocked = achievement.id in unlockedAchievementIds
                    AchievementItem(achievement = achievement, isUnlocked = isUnlocked, index = index)
                }
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement, isUnlocked: Boolean, index: Int) {
    val colors = listOf(
        Color(0xFF81D4FA), // Light Blue
        Color(0xFFA5D6A7), // Light Green
        Color(0xFFFFF59D), // Light Yellow
        Color(0xFFFFCC80), // Light Orange
        Color(0xFFEF9A9A)  // Light Red
    )
    val cardColor = if (isUnlocked) colors[index % colors.size] else Color.LightGray

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = if (isUnlocked) achievement.unlockedIcon else achievement.lockedIcon),
                contentDescription = achievement.name,
                modifier = Modifier.size(40.dp),
                tint = if (isUnlocked) Color.Black.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isUnlocked) Color.Black else Color.Black.copy(alpha = 0.6f)

                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUnlocked) Color.Black.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    }
}
