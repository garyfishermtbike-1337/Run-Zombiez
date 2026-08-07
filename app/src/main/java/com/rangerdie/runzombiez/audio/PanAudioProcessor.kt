package com.rangerdie.runzombiez.audio

import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.cos
import kotlin.math.sin

/**
 * Applies equal-power stereo panning to 16-bit PCM audio so zombie/environment
 * cues can appear to move around Runner 007 (spec section 10). Mono input is
 * upmixed to stereo; stereo input is summed to mono first, then panned.
 *
 * NOTE: this is the piece of the audio pipeline most likely to need a small
 * adjustment against the exact Media3 version pinned in app/build.gradle.kts —
 * BaseAudioProcessor's exact method signatures have shifted slightly across
 * releases. Verify against the installed media3-common sources on first build.
 */
class PanAudioProcessor : BaseAudioProcessor() {

    /** -1.0 = full left, 0.0 = center, 1.0 = full right. */
    @Volatile
    var pan: Float = 0f

    private var inputChannelCount = 2

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }
        inputChannelCount = inputAudioFormat.channelCount
        return AudioProcessor.AudioFormat(inputAudioFormat.sampleRate, 2, C.ENCODING_PCM_16BIT)
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val frameCount = inputBuffer.remaining() / (2 * inputChannelCount)
        val outputBuffer = replaceOutputBuffer(frameCount * 2 * 2)

        val angle = ((pan.coerceIn(-1f, 1f) + 1f) * (Math.PI / 4.0)).toFloat()
        val leftGain = cos(angle.toDouble()).toFloat()
        val rightGain = sin(angle.toDouble()).toFloat()

        val input = inputBuffer.order(ByteOrder.LITTLE_ENDIAN)
        val output = outputBuffer.order(ByteOrder.LITTLE_ENDIAN)

        repeat(frameCount) {
            val mono: Int = if (inputChannelCount == 1) {
                input.short.toInt()
            } else {
                val l = input.short.toInt()
                val r = input.short.toInt()
                // drop any extra channels beyond stereo
                repeat(inputChannelCount - 2) { input.short }
                (l + r) / 2
            }
            output.putShort((mono * leftGain).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort())
            output.putShort((mono * rightGain).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort())
        }

        inputBuffer.position(inputBuffer.limit())
        outputBuffer.flip()
    }
}
