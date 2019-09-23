package com.saiferwp.weatheronvoicecommand.ui.main

import android.content.Context
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saiferwp.weatheronvoicecommand.App
import com.saiferwp.weatheronvoicecommand.data.api.response.WeatherOnLatLngResponse
import com.saiferwp.weatheronvoicecommand.misc.WeatherSpeechRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt

class MainViewModel : ViewModel() {

    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var packageName: String

    private var tts: TextToSpeech? = null

    val micLevelLiveData = MutableLiveData<Float>()
    val eventLiveData = MutableLiveData<MainViewEvent>()

    fun startSpeechRecognizer(context: Context) {
        reset()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(speechRecognitionListener)

        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.ERROR) {
                // Error initializing Text-To-Speech Engine
            }
        })

        packageName = context.packageName
        speechRecognizer?.startListening(
            WeatherSpeechRecognizer.newIntent(packageName)
        )
    }

    private fun reset() {
        speechRecognizer?.destroy()
        tts?.shutdown()
    }

    fun searchForWeather() {
        viewModelScope.launch {
            val provider = App.component.getWeatherDataProvider()
            val weatherData = provider.getWeatherData()
            if (weatherData != null) {
                eventLiveData.value = ShowDetailedWeatherEvent(
                    provider.getWebViewLink(weatherData.id)
                )

                speechRecognizer?.setRecognitionListener(null)
                speechRecognizer?.stopListening()

                delay(1000)

                tellWeatherInfo(weatherData)
            }
        }
    }

    private fun tellWeatherInfo(weatherData: WeatherOnLatLngResponse) {
        eventLiveData.value = UnmuteAudio

        val message1 = "The weather in ${weatherData.name} is ${weatherData.weather[0].main}"
        val message2 = "Temperature is around ${weatherData.main.temp.roundToInt()}"
        tts?.stop()
        tts?.speak(message1, TextToSpeech.QUEUE_ADD, null)
        tts?.speak(message2, TextToSpeech.QUEUE_ADD, null)
    }

    private val speechRecognitionListener = object : WeatherSpeechRecognizer.Listener() {

        override fun onRmsChanged(rmsdB: Float) {
            micLevelLiveData.value = 1 + rmsdB / 10f
        }

        override fun onResults(results: Bundle) {
            val list =
                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            list?.forEach {
                if (it.toLowerCase(Locale.ENGLISH).contains(WeatherSpeechRecognizer.KEYWORD)) {
                    eventLiveData.value = KeywordFoundEvent
                    return@forEach
                }
            }

            micLevelLiveData.value = 1f

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
        tts?.shutdown()
    }
}

sealed class MainViewEvent

object KeywordFoundEvent : MainViewEvent()
data class ShowDetailedWeatherEvent(val link: String) : MainViewEvent()
object UnmuteAudio : MainViewEvent()
object MuteAudio : MainViewEvent()
