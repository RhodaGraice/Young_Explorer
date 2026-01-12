package com.example.quizzies.ui.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quizzies.data.SpellingWord
import com.example.quizzies.data.wordsDatabase
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpellingBeeScreen(
    stars: Int,
    profileImageUri: Uri?,
    onNavigateUp: () -> Unit,
    onCorrect: (SpellingWord) -> Unit,
    onWrong: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var word by remember { mutableStateOf(wordsDatabase.random()) }
    val letters = remember(word) { word.name.toList().shuffled() }
    val targetSlots = remember { mutableStateListOf<Char?>() }
    val sourceLetters = remember { mutableStateListOf<Char>() }
    var feedback by remember { mutableStateOf<String?>(null) }
    var answerCorrect by remember { mutableStateOf<Boolean?>(null) }

    val starScale = remember { Animatable(1f) }
    var previousStars by remember { mutableIntStateOf(stars) }

    LaunchedEffect(stars) {
        if (stars > previousStars) {
            starScale.animateTo(1.5f, animationSpec = tween(200))
            starScale.animateTo(1f, animationSpec = tween(200))
        }
        previousStars = stars
    }

    LaunchedEffect(letters) {
        targetSlots.clear()
        targetSlots.addAll(List(word.name.length) { null })
        sourceLetters.clear()
        sourceLetters.addAll(letters)
        feedback = null
        answerCorrect = null
    }

    LaunchedEffect(answerCorrect) {
        if (answerCorrect == true) {
            delay(1500)
            // Ensure the next word is different
            var nextWord = wordsDatabase.random()
            while (nextWord.name == word.name) {
                nextWord = wordsDatabase.random()
            }
            word = nextWord
        }
    }

    fun checkWord() {
        if (targetSlots.none { it == null }) {
            val spelledWord = targetSlots.joinToString("")
            if (spelledWord == word.name) {
                feedback = "Awesome! You got a star! 🌟"
                answerCorrect = true
                onCorrect(word)
            } else {
                feedback = "Not quite, try again!"
                answerCorrect = false
                onWrong()
            }
        } else {
            feedback = null
            answerCorrect = null
        }
    }

    fun onTargetLetterClick(index: Int) {
        val letter = targetSlots[index]
        if (letter != null) {
            targetSlots[index] = null
            sourceLetters.add(letter)
            checkWord()
        }
    }

    fun onSourceLetterClick(index: Int) {
        val letter = sourceLetters[index]
        val targetIndex = targetSlots.indexOfFirst { it == null }
        if (targetIndex != -1) {
            targetSlots[targetIndex] = letter
            sourceLetters.removeAt(index)
            checkWord()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Spelling Bee", style = MaterialTheme.typography.headlineSmall) },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Spell the word", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = word.imageRes),
                contentDescription = "Object to spell",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                targetSlots.forEachIndexed { index, letter ->
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (letter != null) {
                            LetterCard(
                                letter = letter,
                                index = index,
                                modifier = Modifier.clickable { onTargetLetterClick(index) }
                            )
                        } else {
                            EmptySlot()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(sourceLetters) { index, letter ->
                    LetterCard(
                        letter = letter,
                        index = index,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable { onSourceLetterClick(index) }
                    )
                }
            }

            AnimatedVisibility(
                visible = feedback != null,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(48.dp),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = feedback.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (answerCorrect == true) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}


@Composable
fun LetterCard(letter: Char, index: Int, modifier: Modifier = Modifier) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(key1 = letter) {
        scale.animateTo(1.2f, animationSpec = tween(100))
        scale.animateTo(1f, animationSpec = tween(100))
    }
    val colors = listOf(
        Color(0xFFE0BBE4), // Light Purple
        Color(0xFF957DAD), // Muted Purple
        Color(0xFFD291BC), // Pinkish
        Color(0xFFFEC8D8), // Lighter Pink
        Color(0xFFB8E0D2)  // Muted Teal
    )
    val cardColor = colors[index % colors.size]
    Card(
        modifier = modifier
            .size(42.dp)
            .scale(scale.value),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun EmptySlot(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.size(42.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {}
    }
}
