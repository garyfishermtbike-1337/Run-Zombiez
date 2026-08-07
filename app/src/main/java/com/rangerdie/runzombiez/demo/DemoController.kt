package com.rangerdie.runzombiez.demo

import com.rangerdie.runzombiez.mission.MissionEngine
import com.rangerdie.runzombiez.mission.MissionRepository

private const val DEMO_MISSION_ASSET = "demo_mission.json"

/**
 * One-tap showcase (spec sections 18-20). Loads and plays the bundled demo
 * mission through the same [MissionEngine] real missions use, so the demo is
 * an honest preview of the real experience rather than a separate code path.
 */
class DemoController(
    private val missionRepository: MissionRepository,
    private val missionEngine: MissionEngine
) {
    suspend fun run() {
        val mission = missionRepository.loadMission(DEMO_MISSION_ASSET)
        missionEngine.start(mission)
    }
}
