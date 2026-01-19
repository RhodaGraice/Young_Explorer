package com.example.quizzies.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class DrawingPath(val path: Path, val color: Color, val strokeWidth: Float)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(navController: NavController) {
    val paths = remember { mutableStateListOf<DrawingPath>() }
    val selectedColor = remember { mutableStateOf(Color.Black) }
    val selectedStrokeWidth = remember { mutableStateOf(10f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Drawing Board") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { paths.clear() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            ) {
                Canvas(modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val newPath = Path().apply {
                                moveTo(change.position.x - dragAmount.x, change.position.y - dragAmount.y)
                                lineTo(change.position.x, change.position.y)
                            }
                            paths.add(
                                DrawingPath(
                                    newPath,
                                    selectedColor.value,
                                    selectedStrokeWidth.value
                                )
                            )
                        }
                    }
                ) {
                    paths.forEach { (path, color, strokeWidth) ->
                        drawPath(
                            path = path,
                            color = color,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            // Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Color Palette
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val colors = listOf(
                        Color.Black, Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Magenta,
                        Color.Cyan, Color.Gray, Color(0xFFFFA500) // Orange
                    )
                    items(colors) { color ->
                        val isSelected = selectedColor.value == color
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColor.value = color }
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tools (Pen sizes and Eraser)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pen Sizes
                    val strokeWidths = listOf(5f, 10f, 20f)
                    strokeWidths.forEach { strokeWidth ->
                        TextButton(
                            onClick = { selectedStrokeWidth.value = strokeWidth },
                            colors = if (selectedStrokeWidth.value == strokeWidth) ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.textButtonColors()
                        ) {
                            Text(text = "${strokeWidth.toInt()}pt")
                        }
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))

                    // Eraser
                    IconButton(onClick = { selectedColor.value = Color.White }) {
                        Icon(Icons.Default.Edit, contentDescription = "Eraser")
                    }
                }
            }
        }
    }
}
