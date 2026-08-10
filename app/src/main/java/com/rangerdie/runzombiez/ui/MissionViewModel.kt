package com.rangerdie.runzombiez.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.rangerdie.runzombiez.audio.AudioEngine
import com.rangerdie.runzombiez.demo.DemoController
import com.rangerdie.runzombiez.mission.MissionEngine
import com.rangerdie.runzombiez.mission.MissionRepository
import com.rangerdie.runzombiez.mission.MissionUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Owns the app's single [AudioEngine] and [MissionEngine] instance for the
 * lifetime of the process, so mission/demo playback survives screen navigation.
 */
@UnstableApi
class MissionViewModel(application: Application) : AndroidViewModel(application) {

    private val audioEngine = AudioEngine(application)
    private val missionRepository = MissionRepository(application)
    private val missionEngine = MissionEngine(audioEngine)
    private val demoController = DemoController(missionRepository, missionEngine)

    val missionState: StateFlow<MissionUiState> = missionEngine.state

    fun startMission(assetFileName: String) {
        viewModelScope.launch {
            val mission = missionRepository.loadMission(assetFileName)
            missionEngine.start(mission)
        }
    }

    fun startDemo() {
        viewModelScope.launch { demoController.run() }
    }

    fun stopMission() {
        missionEngine.stop()
    }

    override fun onCleared() {
        audioEngine.release()
        super.onCleared()
    }
}
