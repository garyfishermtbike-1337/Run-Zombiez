package com.rangerdie.runzombiez.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.sp
import com.rangerdie.runzombiez.R
import com.rangerdie.runzombiez.ui.theme.AshGrayLight
import com.rangerdie.runzombiez.ui.theme.BoneWhite
import com.rangerdie.runzombiez.ui.theme.HavenBlack
import com.rangerdie.runzombiez.ui.theme.WarningRed

/**
 * MVP home screen (spec section 17), styled to match the "Classic Look" visual
 * direction (see art/concept/ mockups, DECISIONS.md). Title/tagline are baked
 * into the hero key art rather than drawn as text.
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.home_hero),
                contentDescription = stringResource(R.string.home_hero_description),
                modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(18.dp))

            IconLabelButton(
                label = stringResource(R.string.btn_demo),
                leadingIcon = "💀", // skull
                trailingIcon = "▶", // play triangle
                trailingIconColor = WarningRed,
                onClick = onDemo,
                modifier = Modifier.fillMaxWidth()
            )

            IconLabelButton(
                label = stringResource(R.string.btn_start_mission),
                leadingIcon = "👣", // footprints
                trailingIcon = "›", // chevron
                onClick = onStartMission,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                IconLabelButton(
                    label = stringResource(R.string.btn_stop),
                    leadingIcon = "⏹", // stop square
                    borderColor = WarningRed,
                    contentColor = WarningRed,
                    modifier = Modifier.weight(1f),
                    onClick = onStop
                )
                IconLabelButton(
                    label = stringResource(R.string.btn_help),
                    leadingIcon = "❓", // question mark
                    modifier = Modifier.weight(1f),
                    onClick = onHelp
                )
            }
        }
    }
}

@Composable
private fun IconLabelButton(
    label: String,
    leadingIcon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: String? = null,
    trailingIconColor: Color = BoneWhite,
    borderColor: Color = AshGrayLight,
    contentColor: Color = BoneWhite
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = leadingIcon, fontSize = 18.sp)
                Text(text = label, style = MaterialTheme.typography.labelLarge, color = contentColor)
            }
            if (trailingIcon != null) {
                Text(text = trailingIcon, fontSize = 16.sp, color = trailingIconColor)
            }
        }
    }
}
