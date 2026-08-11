package com.rangerdie.runzombiez.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rangerdie.runzombiez.R
import com.rangerdie.runzombiez.ui.theme.BoneWhite
import com.rangerdie.runzombiez.ui.theme.HavenBlack
import com.rangerdie.runzombiez.ui.theme.WarningRed

/**
 * MVP home screen (spec section 17): four clearly separated controls, nothing else.
 * Title/tagline art is baked into the hero key art rather than drawn as text.
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
            Image(
                painter = painterResource(R.drawable.home_hero),
                contentDescription = stringResource(R.string.home_hero_description),
                modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = stringResource(R.string.home_tagline),
                style = MaterialTheme.typography.bodyLarge,
                color = BoneWhite
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onStartMission,
                modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarningRed, contentColor = BoneWhite)
            ) {
                HomeButtonContent(
                    label = stringResource(R.string.btn_start_mission),
                    caption = stringResource(R.string.caption_start_mission),
                    captionColor = BoneWhite.copy(alpha = 0.75f)
                )
            }

            OutlinedButton(
                onClick = onDemo,
                modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp)
            ) {
                HomeButtonContent(
                    label = stringResource(R.string.btn_demo),
                    caption = stringResource(R.string.caption_demo),
                    captionColor = BoneWhite.copy(alpha = 0.7f)
                )
            }

            OutlinedButton(
                onClick = onStop,
                modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp)
            ) {
                HomeButtonContent(
                    label = stringResource(R.string.btn_stop),
                    caption = stringResource(R.string.caption_stop),
                    captionColor = BoneWhite.copy(alpha = 0.7f)
                )
            }

            OutlinedButton(
                onClick = onHelp,
                modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp)
            ) {
                HomeButtonContent(
                    label = stringResource(R.string.btn_help),
                    caption = stringResource(R.string.caption_help),
                    captionColor = BoneWhite.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun HomeButtonContent(label: String, caption: String, captionColor: Color) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Text(text = caption, style = MaterialTheme.typography.bodyMedium, color = captionColor)
    }
}
