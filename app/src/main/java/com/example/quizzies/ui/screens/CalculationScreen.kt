package com.example.quizzies.ui.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizzies.R
import com.example.quizzies.data.MathProblem
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
    level: Int,
    onCorrect: () -> Unit,
    onWrong: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    LetsLearnTheme {
        var problem by remember { mutableStateOf(generateMathProblem(level)) }
        var options by remember { mutableStateOf(getAnswerOptions(problem)) }
        var selectedAnswer by remember { mutableStateOf<Int?>(null) }
        var answeredCorrectly by remember { mutableStateOf<Boolean?>(null) }
        var feedbackText by remember { mutableStateOf<String?>(null) }

        val starScale = remember { Animatable(1f) }
        var previousStars by remember { mutableStateOf(stars) }

        LaunchedEffect(stars) {
            if (stars > previousStars) {
                starScale.animateTo(1.5f, animationSpec = tween(200))
                starScale.animateTo(1f, animationSpec = tween(200))
            }
            previousStars = stars
        }

        fun nextQuestion() {
            val newProblem = generateMathProblem(level)
            problem = newProblem
            options = getAnswerOptions(newProblem)
            selectedAnswer = null
            answeredCorrectly = null
            feedbackText = null
        }

        LaunchedEffect(answeredCorrectly) {
            if (answeredCorrectly == true) {
                delay(1500) // Wait for 1.5 seconds
                nextQuestion()
            }
        }

        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Level $level", style = MaterialTheme.typography.headlineSmall) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        Row(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .scale(starScale.value),
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
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "What is the answer?",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(32.dp))

                    VisualMathProblem(problem = problem)

                    Spacer(modifier = Modifier.size(32.dp))

                    // Feedback text now appears here
                    AnimatedVisibility(
                        visible = feedbackText != null,
                        modifier = Modifier.height(48.dp),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = feedbackText.orEmpty(),
                                style = MaterialTheme.typography.headlineSmall,
                                color = if (answeredCorrectly == true) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        options.forEach { answer ->
                            val isSelected = selectedAnswer == answer
                            val isCorrect = problem.answer == answer

                            val buttonColor = when {
                                isSelected && answeredCorrectly == true -> Color.Green
                                isSelected && answeredCorrectly == false -> Color.Red
                                answeredCorrectly != null && isCorrect -> Color.Green
                                else -> MaterialTheme.colorScheme.primary
                            }

                            Button(
                                onClick = {
                                    selectedAnswer = answer
                                    if (isCorrect) {
                                        feedbackText = "Correct!"
                                        onCorrect()
                                        answeredCorrectly = true
                                    } else {
                                        feedbackText = "Try again!"
                                        onWrong()
                                        answeredCorrectly = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                                enabled = answeredCorrectly != true
                            ) {
                                Text(
                                    answer.toString(),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VisualMathProblem(problem: MathProblem) {
    if (problem.num1 != null && problem.num2 != null && problem.operator != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val itemVisual = problem.emoji ?: ""
            Text(itemVisual.repeat(problem.num1), fontSize = 32.sp)
            when (problem.operator) {
                "+" -> Icon(Icons.Default.Add, "Add", modifier = Modifier.size(32.dp))
                "-" -> Icon(Icons.Default.HorizontalRule, "Subtract", modifier = Modifier.size(32.dp))
                "×" -> Icon(Icons.Default.Close, "Multiply", modifier = Modifier.size(32.dp))
                "÷" -> Icon(painterResource(R.drawable.ic_division), "Divide", modifier = Modifier.size(32.dp))
            }
            Text(itemVisual.repeat(problem.num2), fontSize = 32.sp)
        }
    } else {
        // Text-based problem
        Text(problem.question, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
    }
}
