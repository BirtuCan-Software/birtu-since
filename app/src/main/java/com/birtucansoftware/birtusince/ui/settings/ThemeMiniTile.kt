package com.birtucansoftware.birtusince.ui.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuColors
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors
import com.birtucansoftware.birtusince.ui.theme.ThemePreset

@Composable
fun ThemeMiniTile(
    name: String,
    preset: ThemePreset,
    themeColors: BirtuColors,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentColors = LocalBirtuColors.current
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1.0f,
        animationSpec = tween(durationMillis = 80),
        label = "themeScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(104.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 132.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clip(RoundedCornerShape(20.dp))
                .background(themeColors.bg) // Mini preview of the app background
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) currentColors.primary else currentColors.outline,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable(onClick = onClick)
                .padding(10.dp)
        ) {
            // Mini mockup inside tile
            Column {
                // Mini top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 28.dp, height = 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(themeColors.text)
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(themeColors.primary)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mini card rectangle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(themeColors.surface)
                        .border(1.dp, themeColors.outline, RoundedCornerShape(10.dp))
                        .padding(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(themeColors.primarySoft)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(width = 36.dp, height = 5.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(themeColors.text)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(width = 24.dp, height = 5.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(themeColors.text2)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mini primary pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(themeColors.primary)
                )
            }

            // Selected check badge
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(currentColors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    PhosphorIcon(
                        icon = PhosphorIcons.Check,
                        contentDescription = null,
                        tint = currentColors.onPrimary,
                        size = 14.dp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = name,
            style = BirtuTypography.label.copy(
                color = currentColors.text,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}
