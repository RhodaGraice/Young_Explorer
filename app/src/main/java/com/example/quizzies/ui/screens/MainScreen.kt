package com.example.quizzies.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController, username: String, streak: () -> Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Welcome, $username!",
                style = MaterialTheme.typography.headlineMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Whatshot,
                    contentDescription = "Streak",
                    tint = Color(0xFFFFA000)
                )
                Text(
                    text = "${streak()} Days",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MenuButton(
                    text = "Learn Alphabet",
                    icon = Icons.Default.SortByAlpha,
                    color = Color(0xFF009688),
                    onClick = { navController.navigate("alphabet_menu") }
                )
            }
            item {
                MenuButton(
                    text = "Learn Numbers",
                    icon = Icons.Default.Dialpad,
                    color = Color(0xFF009688),
                    onClick = { navController.navigate("numbers_menu") }
                )
            }
            item {
                MenuButton(
                    text = "Practice Words",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    color = Color(0xFF009688),
                    onClick = { navController.navigate("spelling_menu") }
                )
            }
            item {
                MenuButton(
                    text = "Spelling Bee",
                    icon = Icons.Default.Spellcheck,
                    color = Color(0xFF009688),
                    onClick = { navController.navigate("spelling_bee") }
                )
            }
            item {
                MenuButton(
                    text = "Practice Math",
                    icon = Icons.Filled.Calculate,
                    color = Color(0xFF009688),
                    onClick = { navController.navigate("calculation") }
                )
            }
            item {
                MenuButton(
                    text = "Sticker Book",
                    icon = Icons.Filled.Stars,
                    color = Color(0xFF009688),
                    onClick = { navController.navigate("sticker_book") }
                )
            }
            item {
                MenuButton(
                    text = "Daily Challenges",
                    icon = Icons.Filled.Check,
                    color = Color(0xFF009688),
                    onClick = { navController.navigate("daily_challenges") }
                )
            }
            item {
                MenuButton(
                    text = "Settings",
                    icon = Icons.Default.Settings,
                    color = Color.Gray,
                    onClick = { navController.navigate("settings") }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Let's Learn: Fun learning for kids!",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun MenuButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                icon,
                contentDescription = text,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }
    }
}
