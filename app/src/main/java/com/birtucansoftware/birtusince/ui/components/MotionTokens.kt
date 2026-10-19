package com.birtucansoftware.birtusince.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

/**
 * Standard press scale and state-layer overlay replacing default ripple (UI Spec 7.2)
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.birtuPress(
    scaleDown: Float = 0.96f,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(16.dp),
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val colors = LocalBirtuColors.current
    val view = LocalView.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) scaleDown else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "birtuPressScale"
    )

    val stateLayerAlpha by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.08f else 0f,
        animationSpec = tween(if (isPressed) 90 else 160),
        label = "stateLayerAlpha"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clip(shape)
        .combinedClickable(
            interactionSource = interactionSource,
            indication = null, // Suppress default ripple
            enabled = enabled,
            onLongClick = if (onLongClick != null) {
                {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onLongClick()
                }
            } else null,
            onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onClick()
            }
        )
        .background(colors.text.copy(alpha = stateLayerAlpha))
}

fun Modifier.birtuCardPress(
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(20.dp),
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier = birtuPress(
    scaleDown = 0.98f,
    enabled = enabled,
    shape = shape,
    onLongClick = onLongClick,
    onClick = onClick
)

fun Modifier.birtuChipPress(
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(percent = 50),
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier = birtuPress(
    scaleDown = 0.95f,
    enabled = enabled,
    shape = shape,
    onLongClick = onLongClick,
    onClick = onClick
)

