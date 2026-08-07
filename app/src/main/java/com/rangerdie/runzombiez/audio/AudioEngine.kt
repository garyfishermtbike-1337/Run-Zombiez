package com.rangerdie.runzombiez.audio

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.rangerdie.runzombiez.mission.AudioChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val FADE_STEP_MS = 30L

/**
 * Owns one ExoPlayer per audio channel so music, voice, sfx, and ambience can
 * layer and cross-fade independently (spec section 9/28). Built on Media3/ExoPlayer
 * with a custom [PanAudioProcessor] on the SFX channel for spatial zombie audio
 * (spec section 10).
 */
class AudioEngine(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob())
    private var fadeJobs = mutableMapOf<AudioChannel, Job>()

    private val panProcessor = PanAudioProcessor()

    private val attributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val players: Map<AudioChannel, ExoPlayer> = AudioChannel.entries.associateWith { channel ->
        val builder = if (channel == AudioChannel.SFX) {
            ExoPlayer.Builder(context, PanningRenderersFactory(context, arrayOf(panProcessor)))
        } else {
            ExoPlayer.Builder(context)
        }
        builder.build().apply {
            setAudioAttributes(attributes, /* handleAudioFocus= */ channel == AudioChannel.MUSIC)
            volume = 0f
            // Missing/unresolvable audio assets (expected until docs/ASSETS.md is filled in) surface
            // here rather than crashing playback — the mission timeline keeps running silently.
            addListener(object : Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    android.util.Log.w("AudioEngine", "[$channel] playback error: ${error.message}")
                }
            })
        }
    }

    /** Plays [assetPath] (relative to assets/audio/) on [channel], replacing whatever is currently playing there. */
    fun play(
        channel: AudioChannel,
        assetPath: String,
        loop: Boolean = false,
        fadeInMs: Long = 400L,
        targetVolume: Float = 1f,
        pan: Float = 0f
    ) {
        val player = players.getValue(channel)
        if (channel == AudioChannel.SFX) panProcessor.pan = pan

        player.setMediaItem(MediaItem.fromUri("asset:///audio/$assetPath"))
        player.repeatMode = if (loop) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        player.prepare()
        player.play()
        fadeTo(channel, targetVolume, fadeInMs)
    }

    fun stop(channel: AudioChannel, fadeOutMs: Long = 400L) {
        fadeTo(channel, 0f, fadeOutMs) {
            players.getValue(channel).stop()
        }
    }

    fun stopAll(fadeOutMs: Long = 500L) {
        AudioChannel.entries.forEach { stop(it, fadeOutMs) }
    }

    /** Temporarily lowers a channel's volume (e.g. ducking music under Haven Base dialogue). */
    fun duck(channel: AudioChannel, to: Float = 0.25f, durationMs: Long = 250L) {
        fadeTo(channel, to, durationMs)
    }

    fun restore(channel: AudioChannel, to: Float = 1f, durationMs: Long = 250L) {
        fadeTo(channel, to, durationMs)
    }

    fun setPan(pan: Float) {
        panProcessor.pan = pan
    }

    private fun fadeTo(channel: AudioChannel, target: Float, durationMs: Long, onComplete: (() -> Unit)? = null) {
        fadeJobs[channel]?.cancel()
        fadeJobs[channel] = scope.launch {
            val player = players.getValue(channel)
            val start = player.volume
            if (durationMs <= 0L) {
                player.volume = target
            } else {
                val steps = (durationMs / FADE_STEP_MS).coerceAtLeast(1)
                for (i in 1..steps) {
                    player.volume = start + (target - start) * (i / steps.toFloat())
                    delay(FADE_STEP_MS)
                }
            }
            onComplete?.invoke()
        }
    }

    fun release() {
        fadeJobs.values.forEach { it.cancel() }
        players.values.forEach { it.release() }
    }
}
