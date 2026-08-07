package com.rangerdie.runzombiez.mission

import com.rangerdie.runzombiez.audio.AudioEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives a [Mission]'s timeline. The MVP mission engine is deterministic and
 * timeline-driven, not GPS-driven (spec section 7) — events fire at fixed
 * offsets from mission start. GPS-triggered events are a future extension
 * that can hook into the same [dispatch] path.
 */
class MissionEngine(private val audioEngine: AudioEngine) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timelineJob: Job? = null
    private var duckRestoreJob: Job? = null

    private val _state = MutableStateFlow(MissionUiState())
    val state: StateFlow<MissionUiState> = _state.asStateFlow()

    fun start(mission: Mission) {
        stopInternal(resetState = false)
        _state.value = MissionUiState(mission = mission, isRunning = true)

        val sortedEvents = mission.timeline.sortedBy { it.atSeconds }
        timelineJob = scope.launch {
            var lastSecond = 0
            for (event in sortedEvents) {
                val waitMs = ((event.atSeconds - lastSecond) * 1000L).coerceAtLeast(0)
                delay(waitMs)
                lastSecond = event.atSeconds
                dispatch(event)
            }
            val remainingMs = ((mission.durationSeconds - lastSecond) * 1000L).coerceAtLeast(0)
            if (remainingMs > 0) delay(remainingMs)
            complete()
        }
    }

    fun stop() = stopInternal(resetState = true)

    private fun stopInternal(resetState: Boolean) {
        timelineJob?.cancel()
        duckRestoreJob?.cancel()
        audioEngine.stopAll()
        if (resetState) _state.value = MissionUiState()
    }

    private fun dispatch(event: MissionEvent) {
        val isDialogue = event.type == MissionEventType.HAVEN_TRANSMISSION ||
            event.type == MissionEventType.SURVIVOR_DIALOGUE
        _state.update {
            it.copy(
                elapsedSeconds = event.atSeconds,
                currentText = event.text ?: it.currentText,
                // Only dialogue events have a speaker — clear it for narration (zombie
                // encounters, story beats) instead of leaving a stale name on screen.
                currentSpeaker = if (isDialogue) event.speaker else null,
                currentArtwork = event.artwork ?: it.currentArtwork
            )
        }

        when (event.type) {
            MissionEventType.MUSIC_CUE ->
                event.audioAsset?.let { audioEngine.play(AudioChannel.MUSIC, it, loop = true, targetVolume = 0.55f) }

            MissionEventType.HAVEN_TRANSMISSION, MissionEventType.SURVIVOR_DIALOGUE -> {
                audioEngine.duck(AudioChannel.MUSIC)
                audioEngine.duck(AudioChannel.AMBIENCE)
                event.audioAsset?.let { audioEngine.play(AudioChannel.VOICE, it, fadeInMs = 150L, targetVolume = 1f) }
                scheduleDuckRestore()
            }

            MissionEventType.ENVIRONMENT_AMBIENCE ->
                event.audioAsset?.let { audioEngine.play(AudioChannel.AMBIENCE, it, loop = true, targetVolume = 0.35f) }

            MissionEventType.ZOMBIE_ENCOUNTER ->
                event.audioAsset?.let {
                    audioEngine.play(AudioChannel.SFX, it, targetVolume = 0.9f, pan = event.pan, fadeInMs = 200L)
                }

            MissionEventType.STORY_BEAT -> Unit // text/artwork already applied above

            MissionEventType.MISSION_COMPLETE -> complete()
        }
    }

    private fun scheduleDuckRestore(afterMs: Long = 4500L) {
        duckRestoreJob?.cancel()
        duckRestoreJob = scope.launch {
            delay(afterMs)
            audioEngine.restore(AudioChannel.MUSIC, to = 0.55f)
            audioEngine.restore(AudioChannel.AMBIENCE, to = 0.35f)
        }
    }

    private fun complete() {
        timelineJob?.cancel()
        _state.update { it.copy(isRunning = false, isComplete = true) }
        audioEngine.stopAll()
    }
}

data class MissionUiState(
    val mission: Mission? = null,
    val isRunning: Boolean = false,
    val isComplete: Boolean = false,
    val elapsedSeconds: Int = 0,
    val currentText: String? = null,
    val currentSpeaker: String? = null,
    val currentArtwork: String? = null
)
