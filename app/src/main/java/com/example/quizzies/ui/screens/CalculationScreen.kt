package com.example.quizzies.ui.screens

import android.net.Uri
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quizzies.data.generateMathProblem
import com.example.quizzies.data.getAnswerOptions
import com.example.quizzies.ui.theme.LetsLearnTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculationScreen(
    username: String,
    stars: Int,
    profileImageUri: Uri?,
    onCorrect: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    LetsLearnTheme {
        var problem by remember { mutableStateOf(generateMathProblem()) }
        var options by remember { mutableStateOf(getAnswerOptions(problem.answer)) }
        var feedback by remember { mutableStateOf<String?>(null) }
        var answeredCorrectly by remember { mutableStateOf(false) }

        fun nextQuestion() {
            val newProblem = generateMathProblem()
            problem = newProblem
            options = getAnswerOptions(newProblem.answer)
            feedback = null
            answeredCorrectly = false
        }

        if (answeredCorrectly) {
            LaunchedEffect(key1 = problem) {
                delay(1500) // Wait for 1.5 seconds
                nextQuestion()
            }
        }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Practice Math", style = MaterialTheme.typography.headlineSmall) },
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
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("What is the answer?", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.size(32.dp))

                Text(problem.question, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.size(32.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    options.forEach { answer ->
                        Button(onClick = {
                            if (answer == problem.answer) {
                                feedback = "Correct! +1 Star 🌟"
                                onCorrect()
                                answeredCorrectly = true
                            } else {
                                feedback = "Nice try, have another go!"
                            }
                        }) {
                            Text(answer.toString(), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.size(32.dp))

                feedback?.let {
                    val feedbackColor = if (it.startsWith("Correct")) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
                    Text(it, style = MaterialTheme.typography.titleMedium, color = feedbackColor, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
