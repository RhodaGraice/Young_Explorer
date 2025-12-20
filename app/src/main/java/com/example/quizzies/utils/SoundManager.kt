package com.example.quizzies.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.example.quizzies.R

class SoundManager(context: Context) {

    private val soundPool: SoundPool
    private var correctSoundId: Int = 0
    private var wrongSoundId: Int = 0
    private val soundsLoaded = mutableSetOf<Int>()

    init {
        Log.d("SoundManager", "Initializing SoundManager")
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                soundsLoaded.add(sampleId)
                Log.d("SoundManager", "Sound loaded successfully: $sampleId")
            } else {
                Log.e("SoundManager", "Error loading sound $sampleId, status: $status")
            }
        }
        
        loadSounds(context)
    }

    fun playCorrectSound() {
        if (soundsLoaded.contains(correctSoundId)) {
            soundPool.play(correctSoundId, 1f, 1f, 0, 0, 1f)
            Log.d("SoundManager", "Played correct sound")
        } else {
            Log.w("SoundManager", "Attempted to play correct sound, but it's not loaded yet.")
        }
    }

    fun playWrongSound() {
        if (soundsLoaded.contains(wrongSoundId)) {
            soundPool.play(wrongSoundId, 1f, 1f, 0, 0, 1f)
            Log.d("SoundManager", "Played wrong sound")
        } else {
            Log.w("SoundManager", "Attempted to play wrong sound, but it's not loaded yet.")
        }
    }

    private fun loadSounds(context: Context) {
        Log.d("SoundManager", "Loading sounds...")
        correctSoundId = soundPool.load(context, R.raw.thats_correct, 1)
        wrongSoundId = soundPool.load(context, R.raw.try_again, 1)
        Log.d("SoundManager", "correctSoundId will be: $correctSoundId")
        Log.d("SoundManager", "wrongSoundId will be: $wrongSoundId")
    }

    fun release() {
        Log.d("SoundManager", "Releasing SoundPool")
        soundPool.release()
    }
}
