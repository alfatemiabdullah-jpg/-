package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeechHelper", "US English Language is not supported on this device.")
            } else {
                isInitialized = true
                tts?.setSpeechRate(0.9f) // Slightly slower for clear learning speed
            }
        } else {
            Log.e("TextToSpeechHelper", "Initialization Failed!")
        }
    }

    fun speak(text: String) {
        if (isInitialized && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "EnglishSpeech")
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
