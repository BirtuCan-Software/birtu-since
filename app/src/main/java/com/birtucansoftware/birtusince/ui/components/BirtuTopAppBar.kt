package com.birtucansoftware.birtusince.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.R
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun BirtuTopAppBar(
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier,
    logoModifier: Modifier = Modifier,
    helpButtonModifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    onToggleDarkMode: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val colors = LocalBirtuColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(colors.bg)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left End: App Logo & App Name
            Row(
                modifier = logoModifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Logo Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colors.primarySoft),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "BirtuSince Logo",
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // App Name: "Birtu" + "Since" in primaryStrong
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Birtu",
                        style = BirtuTypography.titleL.copy(color = colors.text)
                    )
                    Text(
                        text = "Since",
                        style = BirtuTypography.titleL.copy(color = colors.primaryStrong)
                    )
                }
            }

            // Right End: Optional custom actions + Theme toggle + Help button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                trailingContent?.invoke()

                // Theme Mode Quick Toggle
                if (onToggleDarkMode != null) {
                    BirtuIconButton(
                        icon = if (isDarkMode) PhosphorIcons.Sun else PhosphorIcons.Moon,
                        contentDescription = if (isDarkMode) "Switch to light mode" else "Switch to dark mode",
                        onClick = onToggleDarkMode
                    )
                }

                // Always present Help icon button
                BirtuIconButton(
                    icon = PhosphorIcons.Question,
                    contentDescription = "How it works",
                    onClick = onHelpClick,
                    modifier = helpButtonModifier
                )
            }
        }
    }
}
