package com.birtucansoftware.birtusince.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIconData
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

enum class FilterChipState {
    Neutral,
    Included,
    Excluded
}

@Composable
fun BirtuChip(
    label: String,
    state: FilterChipState = FilterChipState.Neutral,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    icon: PhosphorIconData? = null
) {
    val colors = LocalBirtuColors.current
    val shape = RoundedCornerShape(percent = 50)

    val targetBgColor = when (state) {
        FilterChipState.Included -> colors.primarySoft
        FilterChipState.Excluded -> colors.roseSoft
        FilterChipState.Neutral -> colors.surfaceHigh
    }
    val targetTextColor = when (state) {
        FilterChipState.Included -> colors.primaryStrong
        FilterChipState.Excluded -> colors.roseStrong
        FilterChipState.Neutral -> colors.text
    }
    val targetBorderColor = when (state) {
        FilterChipState.Included -> colors.primaryStrong.copy(alpha = 0.35f)
        FilterChipState.Excluded -> colors.roseStrong.copy(alpha = 0.35f)
        FilterChipState.Neutral -> colors.outline
    }

    val animSpec = tween<Color>(durationMillis = 200)
    val bgColor by animateColorAsState(targetBgColor, animSpec, label = "chipBg")
    val textColor by animateColorAsState(targetTextColor, animSpec, label = "chipText")
    val borderColor by animateColorAsState(targetBorderColor, animSpec, label = "chipBorder")

    val isHighlighted = state != FilterChipState.Neutral
    val scale by animateFloatAsState(
        targetValue = if (isHighlighted) 1.03f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "chipScale"
    )

    Box(
        modifier = modifier
            .height(36.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(bgColor)
            .border(1.dp, borderColor, shape)
            .birtuChipPress(
                shape = shape,
                onLongClick = onLongClick,
                onClick = onClick
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = state == FilterChipState.Included,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row {
                    PhosphorIcon(
                        icon = PhosphorIcons.Check,
                        contentDescription = "Included",
                        tint = textColor,
                        size = 16.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }

            AnimatedVisibility(
                visible = state == FilterChipState.Excluded,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ) {
                Row {
                    PhosphorIcon(
                        icon = PhosphorIcons.X,
                        contentDescription = "Excluded",
                        tint = textColor,
                        size = 16.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }

            if (state == FilterChipState.Neutral && icon != null) {
                PhosphorIcon(
                    icon = icon,
                    contentDescription = null,
                    tint = textColor,
                    size = 16.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = label,
                style = BirtuTypography.label.copy(
                    color = textColor,
                    fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 13.sp
                )
            )
        }
    }
}

@Composable
fun BirtuChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: PhosphorIconData? = null
) = BirtuChip(
    label = label,
    state = if (selected) FilterChipState.Included else FilterChipState.Neutral,
    onClick = onClick,
    modifier = modifier,
    icon = icon
)

