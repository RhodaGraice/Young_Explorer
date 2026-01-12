package com.example.quizzies.utils

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val SOUND_ENABLED = "sound_enabled"
    }

    fun isSoundEnabled(): Boolean {
        // Sound is enabled by default
        return prefs.getBoolean(SOUND_ENABLED, true)
    }

    fun setSoundEnabled(isEnabled: Boolean) {
        prefs.edit().putBoolean(SOUND_ENABLED, isEnabled).apply()
    }
}
