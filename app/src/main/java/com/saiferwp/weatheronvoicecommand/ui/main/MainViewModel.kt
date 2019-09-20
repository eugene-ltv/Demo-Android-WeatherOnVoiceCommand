package com.saiferwp.weatheronvoicecommand.ui.main

import android.content.Context
import android.os.Bundle
import android.speech.SpeechRecognizer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saiferwp.weatheronvoicecommand.misc.WeatherSpeechRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel : ViewModel() {

    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var packageName: String

    val soundLevelLiveData = MutableLiveData<Float>()
    val eventLiveData = MutableLiveData<Event>()

    enum class Event {
        KEYWORD_FOUND
    }

    fun startSpeechRecognizer(context: Context) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(speechRecognitionListener)

        packageName = context.packageName
        speechRecognizer?.startListening(
            WeatherSpeechRecognizer.newIntent(packageName)
        )
    }

    private val speechRecognitionListener = object : WeatherSpeechRecognizer.Listener() {


        override fun onEvent(eventCode: Int, data: Bundle) {
            println("onEvent $eventCode")
        }

        override fun onRmsChanged(rmsdB: Float) {
            soundLevelLiveData.value = 1 + rmsdB / 10f
        }

        override fun onResults(results: Bundle) {
            val list =
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            list?.forEach {
                if (it.toLowerCase(Locale.ENGLISH).contains(WeatherSpeechRecognizer.KEYWORD)) {
                    eventLiveData.value = Event.KEYWORD_FOUND
                    return@forEach
                }
            }

            soundLevelLiveData.value = 1f

            viewModelScope.launch(Dispatchers.Main) {
                speechRecognizer?.stopListening()
                delay(300)
                speechRecognizer?.startListening(
                    WeatherSpeechRecognizer.newIntent(packageName)
                )
            }
        }

        override fun onError(errorCode: Int) {
            if (errorCode == SpeechRecognizer.ERROR_NO_MATCH
                || errorCode == SpeechRecognizer.ERROR_SPEECH_TIMEOUT
            ) {
                println(errorCode)

                viewModelScope.launch(Dispatchers.Main) {
                    speechRecognizer?.stopListening()
                    delay(300)
                    speechRecognizer?.startListening(
                        WeatherSpeechRecognizer.newIntent(packageName)
                    )
                }
            }
        }
    }

    override fun onCleared() {
        speechRecognizer?.destroy()
    }
}
