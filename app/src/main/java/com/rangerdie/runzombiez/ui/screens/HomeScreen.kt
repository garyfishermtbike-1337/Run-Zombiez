package com.rangerdie.runzombiez.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rangerdie.runzombiez.R
import com.rangerdie.runzombiez.ui.theme.BoneWhite
import com.rangerdie.runzombiez.ui.theme.HavenBlack
import com.rangerdie.runzombiez.ui.theme.WarningRed

/**
 * MVP home screen (spec section 17): four clearly separated controls, nothing else.
 */
@Composable
fun HomeScreen(
    onStartMission: () -> Unit,
    onDemo: () -> Unit,
    onStop: () -> Unit,
    onHelp: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HavenBlack)
            .padding(PaddingValues(horizontal = 32.dp, vertical = 48.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "RUN! ZOMBIEZ",
                style = MaterialTheme.typography.displayLarge,
                color = WarningRed,
                fontWeight = FontWeight.Black
            )
            Text(
                text = stringResource(R.string.home_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = BoneWhite
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onStartMission,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarningRed, contentColor = BoneWhite)
            ) {
                Text(stringResource(R.string.btn_start_mission), style = MaterialTheme.typography.labelLarge)
            }

            OutlinedButton(
                onClick = onDemo,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.btn_demo), style = MaterialTheme.typography.labelLarge)
            }

            OutlinedButton(
                onClick = onStop,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.btn_stop), style = MaterialTheme.typography.labelLarge)
            }

            OutlinedButton(
                onClick = onHelp,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.btn_help), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
