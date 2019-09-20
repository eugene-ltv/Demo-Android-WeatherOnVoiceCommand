package com.saiferwp.weatheronvoicecommand.misc

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent

object WeatherSpeechRecognizer {

    const val KEYWORD = "weather"

    fun newIntent(packageName: String): Intent {
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE,
            packageName
        )
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "en"
        )
        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
            "en"
        )

        return recognizerIntent
    }

    open class Listener : RecognitionListener {

        override fun onBeginningOfSpeech() {}

        override fun onBufferReceived(data: ByteArray) {}

        override fun onEndOfSpeech() {}

        override fun onError(errorCode: Int) {}

        override fun onEvent(eventCode: Int, data: Bundle) {}

        override fun onPartialResults(partialResults: Bundle) {}

        override fun onReadyForSpeech(params: Bundle) {}

        override fun onResults(results: Bundle) {}

        override fun onRmsChanged(rmsdB: Float) {}
    }

}