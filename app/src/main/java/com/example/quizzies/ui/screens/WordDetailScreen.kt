package com.example.quizzies.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quizzies.data.SpellingWord
import com.example.quizzies.data.getSpellingAnswerOptions
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(
    word: SpellingWord,
    username: String,
    stars: Int,
    profileImageUri: Uri?,
    onCorrect: () -> Unit,
    onNavigateUp: () -> Unit,
    onNextWord: () -> Unit
) {
    var options by remember { mutableStateOf(getSpellingAnswerOptions(word)) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var answeredCorrectly by remember { mutableStateOf(false) }

    val title = when (word.category) {
        "Colors" -> "Guess the Color"
        "Shapes" -> "Guess the Shape"
        else -> "Guess the Word"
    }

    fun nextQuestion() {
        onNextWord()
    }

    if (answeredCorrectly) {
        LaunchedEffect(key1 = word) {
            delay(1500) // Wait for 1.5 seconds
            nextQuestion()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = title, style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier.padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Stars", tint = Color(0xFFFFC107))
                        Text(text = stars.toString())
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("What is this?", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(16.dp))
            Image(painter = painterResource(id = word.imageRes), contentDescription = word.name, modifier = Modifier.size(128.dp))
            Spacer(modifier = Modifier.size(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                options.forEach { option ->
                    Button(onClick = {
                        if (option.name == word.name) {
                            feedback = "Correct! +1 Star 🌟"
                            onCorrect()
                            answeredCorrectly = true
                        } else {
                            feedback = "Try again!"
                        }
                    }) {
                        Text(option.name)
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
            feedback?.let {
                Text(
                    it,
                    color = if (it.startsWith("Correct")) Color.Green else Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
