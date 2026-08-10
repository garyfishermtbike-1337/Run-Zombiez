package com.rangerdie.runzombiez.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rangerdie.runzombiez.R
import com.rangerdie.runzombiez.ui.theme.BoneWhite
import com.rangerdie.runzombiez.ui.theme.EmergencyAmber
import com.rangerdie.runzombiez.ui.theme.HavenBlack

@Composable
fun HelpScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HavenBlack)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(R.string.help_title),
            style = MaterialTheme.typography.headlineMedium,
            color = EmergencyAmber
        )
        Text(
            text = stringResource(R.string.help_body),
            style = MaterialTheme.typography.bodyLarge,
            color = BoneWhite
        )
        Text(
            text = stringResource(R.string.help_credits),
            style = MaterialTheme.typography.bodyMedium,
            color = BoneWhite.copy(alpha = 0.55f)
        )
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("BACK TO HAVEN BASE", style = MaterialTheme.typography.labelLarge)
        }
    }
}
