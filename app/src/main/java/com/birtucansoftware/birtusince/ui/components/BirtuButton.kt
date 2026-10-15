package com.birtucansoftware.birtusince.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIconData
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun BirtuPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: PhosphorIconData? = null,
    isSuccessCheck: Boolean = false,
    height: Dp = 56.dp
) {
    val colors = LocalBirtuColors.current
    val shape = RoundedCornerShape(16.dp)

    val bgColor = if (enabled) colors.primary else colors.surfaceHigh
    val textColor = if (enabled) colors.onPrimary else colors.text2

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 120.dp)
            .height(height)
            .clip(shape)
            .background(bgColor)
            .birtuPress(scaleDown = 0.96f, enabled = enabled, shape = shape, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isSuccessCheck,
            transitionSpec = { fadeIn(tween(160)) togetherWith fadeOut(tween(160)) },
            label = "btnSuccess"
        ) { success ->
            if (success) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    PhosphorIcon(
                        icon = PhosphorIcons.Check,
                        contentDescription = null,
                        tint = textColor,
                        size = 24.dp
                    )
                }
            } else {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        PhosphorIcon(
                            icon = icon,
                            contentDescription = null,
                            tint = textColor,
                            size = 22.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = text,
                        style = BirtuTypography.label.copy(
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun BirtuTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: PhosphorIconData? = null,
    height: Dp = 48.dp
) {
    val colors = LocalBirtuColors.current
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(colors.surfaceRaised)
            .border(1.dp, colors.outline, shape)
            .birtuPress(scaleDown = 0.96f, shape = shape, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                PhosphorIcon(
                    icon = icon,
                    contentDescription = null,
                    tint = colors.text,
                    size = 20.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = BirtuTypography.label.copy(
                    color = colors.text,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun BirtuDangerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: PhosphorIconData? = null,
    height: Dp = 48.dp
) {
    val colors = LocalBirtuColors.current
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(colors.late)
            .birtuPress(scaleDown = 0.96f, shape = shape, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                PhosphorIcon(
                    icon = icon,
                    contentDescription = null,
                    tint = colors.onLate,
                    size = 20.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = BirtuTypography.label.copy(
                    color = colors.onLate,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun BirtuTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = LocalBirtuColors.current.primaryStrong,
    height: Dp = 48.dp
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .birtuPress(scaleDown = 0.96f, shape = shape, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = BirtuTypography.label.copy(
                color = textColor,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
