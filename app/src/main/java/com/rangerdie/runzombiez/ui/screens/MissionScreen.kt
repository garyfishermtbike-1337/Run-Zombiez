package com.rangerdie.runzombiez.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rangerdie.runzombiez.R
import com.rangerdie.runzombiez.mission.MissionUiState
import com.rangerdie.runzombiez.ui.theme.BoneWhite
import com.rangerdie.runzombiez.ui.theme.EmergencyAmber
import com.rangerdie.runzombiez.ui.theme.HavenBlack
import com.rangerdie.runzombiez.ui.theme.WarningRed

/**
 * Playback screen shown while a mission (or the demo) is running. Designed to
 * be glanced at rather than watched — the mission is meant to be experienced
 * hands-free, phone away (spec section 5/11).
 */
@Composable
fun MissionScreen(
    state: MissionUiState,
    onStop: () -> Unit,
    onReturnHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HavenBlack)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (state.isComplete) {
                        stringResource(R.string.mission_complete_label)
                    } else {
                        stringResource(R.string.mission_active_label)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = if (state.isComplete) EmergencyAmber else WarningRed
                )
                Text(
                    text = state.mission?.name.orEmpty(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = BoneWhite
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.currentSpeaker?.let {
                    Text(text = it.uppercase(), style = MaterialTheme.typography.labelLarge, color = EmergencyAmber)
                }
                state.currentText?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyLarge, color = BoneWhite)
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.isComplete) {
                    Button(
                        onClick = onReturnHome,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyAmber, contentColor = HavenBlack)
                    ) {
                        Text("RETURN TO HAVEN BASE", style = MaterialTheme.typography.labelLarge)
                    }
                } else {
                    Button(
                        onClick = onStop,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningRed, contentColor = BoneWhite)
                    ) {
                        Text(stringResource(R.string.btn_stop), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
