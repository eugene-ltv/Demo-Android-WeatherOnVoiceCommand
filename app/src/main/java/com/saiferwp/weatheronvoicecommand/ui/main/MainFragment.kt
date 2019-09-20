package com.saiferwp.weatheronvoicecommand.ui.main


import android.Manifest
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.saiferwp.weatheronvoicecommand.R
import com.saiferwp.weatheronvoicecommand.misc.PermissionCode
import com.saiferwp.weatheronvoicecommand.misc.checkPermissions
import com.saiferwp.weatheronvoicecommand.misc.wereAllPermissionsGranted
import kotterknife.bindView


class MainFragment : Fragment() {

    private val textViewMessage: TextView by bindView(R.id.message)
    private val imageViewLevel: ImageView by bindView(R.id.level)

    private lateinit var viewModel: MainViewModel

    private var savedStreamVolume: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        checkPermissions(
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PermissionCode.RECORD_AUDIO_PERMISSION_CODE
        ) {
            viewModel.startSpeechRecognizer(requireContext())
        }

        viewModel.soundLevelLiveData
            .observe(this, Observer { level ->
                imageViewLevel.animate()
                    .scaleX(level)
                    .scaleY(level)
                    .setDuration(50)
                    .start()
            })

        viewModel.eventLiveData
            .observe(this, Observer { event ->
                when (event) {
                    MainViewModel.Event.KEYWORD_FOUND -> {
                        Toast.makeText(
                            requireContext(),
                            "Looking for current weather",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                    }
                }
            })
    }

    override fun onResume() {
        super.onResume()
        muteVolume()
    }

    override fun onPause() {
        restoreVolume()
        super.onPause()
    }

    private fun muteVolume() {
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        savedStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
    }

    private fun restoreVolume() {
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, savedStreamVolume, 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PermissionCode.RECORD_AUDIO_PERMISSION_CODE) {
            if (wereAllPermissionsGranted(grantResults)) {
                viewModel.startSpeechRecognizer(requireContext())
            }
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
