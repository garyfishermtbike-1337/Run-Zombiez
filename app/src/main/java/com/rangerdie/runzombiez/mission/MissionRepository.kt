package com.rangerdie.runzombiez.mission

import android.content.Context
import kotlinx.serialization.json.Json

/**
 * Loads bundled mission JSON from assets/missions/. All MVP missions ship
 * locally with the APK — offline-first, no network required (spec section 13).
 */
class MissionRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    fun loadMission(assetFileName: String): Mission {
        val raw = context.assets.open("missions/$assetFileName").bufferedReader().use { it.readText() }
        return json.decodeFromString(Mission.serializer(), raw)
    }

    fun listMissionFiles(): List<String> =
        context.assets.list("missions")?.filter { it.endsWith(".json") }.orEmpty()
}
