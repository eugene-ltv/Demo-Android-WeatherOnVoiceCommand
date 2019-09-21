package com.saiferwp.weatheronvoicecommand.ui.main

import android.content.Context
import android.os.Bundle
import android.speech.SpeechRecognizer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saiferwp.weatheronvoicecommand.App
import com.saiferwp.weatheronvoicecommand.misc.WeatherSpeechRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel : ViewModel() {

    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var packageName: String

    val micLevelLiveData = MutableLiveData<Float>()
    val eventLiveData = MutableLiveData<MainViewEvent>()

    fun startSpeechRecognizer(context: Context) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(speechRecognitionListener)

        packageName = context.packageName
        speechRecognizer?.startListening(
            WeatherSpeechRecognizer.newIntent(packageName)
        )
    }

    fun searchForWeather() {
        viewModelScope.launch {
            val provider = App.component.getWeatherDataProvider()
            val weatherData = provider.getWeatherData()
            if (weatherData != null) {
                eventLiveData.value = ShowDetailedWeatherEvent(
                    provider.getWebViewLink(weatherData.id)
                )
            }
        }
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
    }
}

sealed class MainViewEvent

object KeywordFoundEvent : MainViewEvent()
data class ShowDetailedWeatherEvent(val link: String) : MainViewEvent()
