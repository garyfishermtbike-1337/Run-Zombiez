package com.rangerdie.runzombiez.audio

import android.content.Context
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink

/**
 * Injects extra [AudioProcessor]s (namely [PanAudioProcessor]) into an ExoPlayer
 * instance's audio pipeline. Only the SFX channel's player uses this — music,
 * voice, and ambience use the stock renderers factory.
 */
class PanningRenderersFactory(
    context: Context,
    private val extraProcessors: Array<AudioProcessor>
) : DefaultRenderersFactory(context) {

    override fun buildAudioSink(
        context: Context,
        enableFloatOutput: Boolean,
        enableAudioTrackPlaybackParams: Boolean
    ): AudioSink {
        return DefaultAudioSink.Builder(context)
            .setAudioProcessors(extraProcessors)
            .setEnableFloatOutput(enableFloatOutput)
            .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
            .build()
    }
}
