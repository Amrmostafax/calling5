package com.example.myvoicebackend

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myvoicebackend.databinding.ActivityMainBinding
import com.example.myvoicebackend.models.ApiResponse
import com.example.myvoicebackend.models.Message
import com.example.myvoicebackend.network.NetworkConfig
import com.example.myvoicebackend.ui.ChatAdapter
import com.example.myvoicebackend.utils.AudioRecorder
import com.example.myvoicebackend.utils.TTSManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var ttsManager: TTSManager
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<Message>()
    private var isRecording = false

    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize components
        audioRecorder = AudioRecorder(this)
        ttsManager = TTSManager(this)

        // Setup RecyclerView
        setupRecyclerView()

        // Check permissions
        checkPermissions()

        // Setup record button
        binding.recordButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        // Add welcome message
        addMessage(Message("Hello! Tap the microphone to start talking.", false))
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = chatAdapter
        }
    }

    private fun checkPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions are required for voice chat", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Microphone permission required", Toast.LENGTH_SHORT).show()
            checkPermissions()
            return
        }

        try {
            audioRecorder.startRecording()
            isRecording = true
            binding.recordButton.text = "🔴 Recording..."
            binding.recordButton.setBackgroundColor(getColor(android.R.color.holo_red_dark))
            showLoading(false)
        } catch (e: Exception) {
            Toast.makeText(this, "Error starting recording: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        try {
            val audioFile = audioRecorder.stopRecording()
            isRecording = false
            binding.recordButton.text = "🎤 Tap to Speak"
            binding.recordButton.setBackgroundColor(getColor(R.color.primary))

            if (audioFile != null && audioFile.exists()) {
                showLoading(true)
                sendAudioToServer(audioFile)
            } else {
                Toast.makeText(this, "Recording failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error stopping recording: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun sendAudioToServer(audioFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestFile = audioFile.asRequestBody("audio/wav".toMediaTypeOrNull())
                val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, requestFile)

                val response = NetworkConfig.apiService.uploadAudio(audioPart)

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    handleResponse(response)
                }

                // Clean up audio file
                audioFile.delete()

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun handleResponse(response: ApiResponse) {
        // Add user message
        addMessage(Message(response.transcription, true))

        // Add AI response
        addMessage(Message(response.response, false))

        // Speak the response
        ttsManager.speak(response.response)
    }

    private fun addMessage(message: Message) {
        messages.add(message)
        chatAdapter.notifyItemInserted(messages.size - 1)
        binding.recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.recordButton.isEnabled = !show
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRecording) {
            audioRecorder.stopRecording()
        }
        ttsManager.shutdown()
    }
}
