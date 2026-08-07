package com.rangerdie.runzombiez.mission

import kotlinx.serialization.Serializable

/**
 * A complete, data-driven mission definition (spec section 30).
 * Bundled as JSON under assets/missions/ for the offline-first MVP.
 */
@Serializable
data class Mission(
    val id: String,
    val name: String,
    val description: String,
    val durationSeconds: Int,
    val storyArtwork: String? = null,
    val timeline: List<MissionEvent>
)

/**
 * A single scheduled beat in a mission's timeline. `atSeconds` is measured
 * from mission start — the MVP mission engine is timeline-driven, not GPS-driven
 * (spec section 7). GPS-triggered events are a future extension of this same model.
 */
@Serializable
data class MissionEvent(
    val atSeconds: Int,
    val type: MissionEventType,
    val audioAsset: String? = null,
    val text: String? = null,
    val speaker: String? = null,
    val artwork: String? = null,
    /** Stereo pan for spatial zombie/environment audio: -1.0 (full left) to 1.0 (full right). */
    val pan: Float = 0f,
    /** Playback channel this event's audio should play on; channels can layer independently. */
    val channel: AudioChannel = AudioChannel.SFX
)

@Serializable
enum class MissionEventType {
    MUSIC_CUE,
    HAVEN_TRANSMISSION,
    SURVIVOR_DIALOGUE,
    ENVIRONMENT_AMBIENCE,
    ZOMBIE_ENCOUNTER,
    STORY_BEAT,
    MISSION_COMPLETE
}

@Serializable
enum class AudioChannel {
    MUSIC,
    VOICE,
    SFX,
    AMBIENCE
}
