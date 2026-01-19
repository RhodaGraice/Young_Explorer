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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Memory
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

private data class MenuItem(
    val text: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@Composable
fun MainScreen(navController: NavController, username: String, streak: () -> Int, modifier: Modifier = Modifier) {
    val menuColors = listOf(
        Color(0xFF4CAF50), // Green 500
        Color(0xFF66BB6A), // Green 400
        Color(0xFF81C784), // Green 300
        Color(0xFF388E3C), // Green 700
        Color(0xFF2E7D32), // Green 800
        Color(0xFF1B5E20), // Green 900
        Color(0xFFA5D6A7), // Green 200
        Color(0xFFC8E6C9)  // Green 100
    )

    val menuItems = listOf(
        MenuItem("Learn Alphabet", Icons.Default.SortByAlpha, menuColors[0], "alphabet_menu"),
        MenuItem("Learn Numbers", Icons.Default.Dialpad, menuColors[1], "numbers_menu"),
        MenuItem("Practice Words", Icons.AutoMirrored.Filled.MenuBook, menuColors[2], "spelling_menu"),
        MenuItem("Spelling Bee", Icons.Default.Spellcheck, menuColors[3], "spelling_bee"),
        MenuItem("Practice Math", Icons.Filled.Calculate, menuColors[4], "math_levels"),
        MenuItem("Memory Match", Icons.Default.Memory, menuColors[5], "memory_match"),
        MenuItem("Drawing", Icons.Default.Brush, menuColors[6], "drawing"),
        MenuItem("Sticker Book", Icons.Filled.Stars, menuColors[7], "sticker_book"),
        MenuItem("Daily Challenges", Icons.Filled.Check, menuColors[0], "daily_challenges"),
        MenuItem("Settings", Icons.Default.Settings, Color.Gray, "settings")
    )

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
            items(menuItems) { item ->
                MenuButton(
                    text = item.text,
                    icon = item.icon,
                    color = item.color,
                    onClick = { navController.navigate(item.route) }
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
                modifier = Modifier.size(ButtonDefaults.IconSize),
                tint = Color.White // Changed to white for better contrast on blue
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = Color.White // Changed to white for better contrast on blue
            )
        }
    }
}
