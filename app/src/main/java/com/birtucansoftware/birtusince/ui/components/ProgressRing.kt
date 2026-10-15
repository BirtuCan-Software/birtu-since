package com.birtucansoftware.birtusince.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun CardProgressRing(
    progressFraction: Float,
    isLate: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    strokeWidth: Dp = 3.dp,
    content: @Composable () -> Unit
) {
    val colors = LocalBirtuColors.current
    val arcColor = if (isLate) colors.late else colors.primary

    val sweepAngle by animateFloatAsState(
        targetValue = (progressFraction.coerceIn(0f, 1f)) * 360f,
        animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
        label = "cardRingSweep"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val radius = (this.size.minDimension - strokePx) / 2f
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)
            val arcSize = Size(radius * 2f, radius * 2f)

            // Background track
            drawCircle(
                color = colors.surfaceHigh,
                radius = radius,
                style = Stroke(strokePx)
            )

            // Progress arc
            if (sweepAngle > 0f) {
                drawArc(
                    color = arcColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(strokePx, cap = StrokeCap.Round)
                )
            }
        }
        content()
    }
}

@Composable
fun HeroProgressRing(
    progressFraction: Float,
    isLate: Boolean,
    hasReminder: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    strokeWidth: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    val colors = LocalBirtuColors.current
    val arcColor = if (isLate) colors.late else colors.primary

    val sweepAngle by animateFloatAsState(
        targetValue = if (hasReminder) (progressFraction.coerceIn(0f, 1f)) * 360f else 0f,
        animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
        label = "heroRingSweep"
    )

    // Breathing dot animation when no reminder set (UI Spec 9.3: 40 to 100% opacity, 2s)
    val infiniteTransition = rememberInfiniteTransition(label = "breathingDot")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val radius = (this.size.minDimension - strokePx) / 2f
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)
            val arcSize = Size(radius * 2f, radius * 2f)

            // Track
            drawCircle(
                color = colors.surfaceHigh,
                radius = radius,
                style = Stroke(strokePx)
            )

            if (hasReminder && sweepAngle > 0f) {
                drawArc(
                    color = arcColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(strokePx, cap = StrokeCap.Round)
                )
            } else if (!hasReminder) {
                // Breathing 8dp dot at top
                val dotRadius = 4.dp.toPx()
                val dotCenter = Offset(this.size.width / 2f, strokePx / 2f)
                drawCircle(
                    color = colors.primary.copy(alpha = dotAlpha),
                    radius = dotRadius,
                    center = dotCenter
                )
            }
        }
        content()
    }
}
