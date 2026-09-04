package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(context: Context) {
    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                    tts?.setPitch(1.2f) // Slightly higher pitch for friendly child-oriented voice
                    tts?.setSpeechRate(0.85f) // Slightly slower for clear letter comprehension
                }
            } else {
                Log.w("SoundManager", "TTS initialization failed: $status")
            }
        }
    }

    fun speakLetter(char: Char) {
        speak(char.toString())
    }

    fun speakLetterAndWord(char: Char, word: String) {
        speak("$char! $char is for $word!")
    }

    fun speak(text: String) {
        if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ABC_UTTERANCE")
        }
    }

    fun speakPraise() {
        val praises = listOf(
            "Great job!",
            "Super star!",
            "Awesome!",
            "You did it!",
            "Hooray!",
            "Wonderful!",
            "High five!"
        )
        speak(praises.random())
    }

    fun playPopSound() {
        scope.launch {
            generateAndPlayToneSequence(
                tones = listOf(
                    ToneSpec(startFreq = 650.0, endFreq = 220.0, durationMs = 80, amplitude = 0.5f)
                )
            )
        }
    }

    fun playCelebrationChime() {
        scope.launch {
            generateAndPlayToneSequence(
                tones = listOf(
                    ToneSpec(startFreq = 523.25, endFreq = 523.25, durationMs = 80, amplitude = 0.35f), // C5
                    ToneSpec(startFreq = 659.25, endFreq = 659.25, durationMs = 80, amplitude = 0.4f),  // E5
                    ToneSpec(startFreq = 783.99, endFreq = 783.99, durationMs = 80, amplitude = 0.45f), // G5
                    ToneSpec(startFreq = 1046.50, endFreq = 1046.50, durationMs = 180, amplitude = 0.5f) // C6
                )
            )
        }
    }

    fun playStarSound() {
        scope.launch {
            generateAndPlayToneSequence(
                tones = listOf(
                    ToneSpec(startFreq = 1318.5, endFreq = 1318.5, durationMs = 60, amplitude = 0.3f), // E6
                    ToneSpec(startFreq = 1760.0, endFreq = 1760.0, durationMs = 120, amplitude = 0.4f) // A6
                )
            )
        }
    }

    fun playGentleMiss() {
        scope.launch {
            generateAndPlayToneSequence(
                tones = listOf(
                    ToneSpec(startFreq = 300.0, endFreq = 240.0, durationMs = 120, amplitude = 0.3f)
                )
            )
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }

    private data class ToneSpec(
        val startFreq: Double,
        val endFreq: Double,
        val durationMs: Int,
        val amplitude: Float
    )

    private fun generateAndPlayToneSequence(tones: List<ToneSpec>) {
        val sampleRate = 44100
        val totalDurationMs = tones.sumOf { it.durationMs }
        val totalSamples = (sampleRate * totalDurationMs / 1000.0).toInt()
        val buffer = ShortArray(totalSamples)

        var currentSample = 0
        for (tone in tones) {
            val numSamples = (sampleRate * tone.durationMs / 1000.0).toInt()
            var phase = 0.0
            for (i in 0 until numSamples) {
                val progress = i.toDouble() / numSamples
                val currentFreq = tone.startFreq + (tone.endFreq - tone.startFreq) * progress
                val envelope = (1.0 - progress) // simple exponential-like linear decay
                val sampleValue = (sin(phase) * tone.amplitude * envelope * Short.MAX_VALUE).toInt()
                if (currentSample < buffer.size) {
                    buffer[currentSample] = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    currentSample++
                }
                phase += 2.0 * PI * currentFreq / sampleRate
                if (phase > 2.0 * PI) phase -= 2.0 * PI
            }
        }

        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val bufferSizeBytes = buffer.size * 2
            val track = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(bufferSizeBytes)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)

            track.play()
            // Wait for track to finish then release
            Thread.sleep(totalDurationMs.toLong() + 50)
            track.stop()
            track.release()
        } catch (e: Exception) {
            Log.e("SoundManager", "Error playing synthesized tone", e)
        }
    }
}
