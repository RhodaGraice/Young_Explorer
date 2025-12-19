package com.example.quizzies.model

import androidx.annotation.DrawableRes

data class Sticker(
    val name: String,
    @DrawableRes val icon: Int,
    val isUnlocked: Boolean = false
)
