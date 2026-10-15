package com.birtucansoftware.birtusince.ui.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.R
import com.birtucansoftware.birtusince.ui.components.BirtuPrimaryButton
import com.birtucansoftware.birtusince.ui.components.BirtuTextButton
import com.birtucansoftware.birtusince.ui.components.BirtuTonalButton
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors
import kotlin.math.roundToInt

@Composable
fun InteractiveSpotlightTourOverlay(
    currentStep: GuidedTourStep,
    stepIndex: Int,
    totalSteps: Int,
    targetBounds: Rect?,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onCancelTour: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    val density = LocalDensity.current

    // Dim overlay color
    val dimColor = Color.Black.copy(alpha = 0.72f)
    val highlightColor = colors.primary
    val pulseAnim = remember { Animatable(1f) }

    LaunchedEffect(stepIndex) {
        pulseAnim.snapTo(1f)
        pulseAnim.animateTo(
            targetValue = 1.08f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        pulseAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .semantics {
                contentDescription = "Help tour step ${stepIndex + 1} of $totalSteps"
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    // Tap on dimmed background advances tour or does not dismiss accidentally
                }
            }
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()

        // 1. Cutout Spotlight Layer using graphicsLayer and BlendMode.Clear
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = 0.99f // Needed for offscreen layer BlendMode.Clear
                }
        ) {
            // Fill background with dim
            drawRect(color = dimColor)

            if (targetBounds != null && targetBounds.width > 0 && targetBounds.height > 0) {
                val padding = 8.dp.toPx()
                val radius = 16.dp.toPx()
                val cutoutRect = Rect(
                    left = (targetBounds.left - padding).coerceAtLeast(0f),
                    top = (targetBounds.top - padding).coerceAtLeast(0f),
                    right = (targetBounds.right + padding).coerceAtMost(screenWidthPx),
                    bottom = (targetBounds.bottom + padding).coerceAtMost(screenHeightPx)
                )

                // Cut hole in dim
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(cutoutRect.left, cutoutRect.top),
                    size = Size(cutoutRect.width, cutoutRect.height),
                    cornerRadius = CornerRadius(radius, radius),
                    blendMode = BlendMode.Clear
                )

                // Draw pulsing highlight ring around cutout
                drawRoundRect(
                    color = highlightColor,
                    topLeft = Offset(cutoutRect.left, cutoutRect.top),
                    size = Size(cutoutRect.width, cutoutRect.height),
                    cornerRadius = CornerRadius(radius, radius),
                    style = Stroke(width = 3.dp.toPx() * pulseAnim.value, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }

        // 2. Explanatory Card with Arrow pointing to target
        if (targetBounds != null && targetBounds.width > 0 && targetBounds.height > 0) {
            val isTargetInTopHalf = targetBounds.center.y < screenHeightPx * 0.5f

            // Card position logic: if target is in top half, card goes below it. If in bottom half, card goes above it.
            val cardPaddingDp = 16.dp
            val cardPaddingPx = with(density) { cardPaddingDp.toPx() }

            val cardTopY = if (isTargetInTopHalf) {
                (targetBounds.bottom + with(density) { 24.dp.toPx() })
                    .coerceAtMost(screenHeightPx - with(density) { 280.dp.toPx() })
            } else {
                (targetBounds.top - with(density) { 260.dp.toPx() })
                    .coerceAtLeast(with(density) { 48.dp.toPx() })
            }

            // Arrow connection
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val arrowStroke = 2.5.dp.toPx()
                val targetCenter = targetBounds.center
                val startX = targetCenter.x.coerceIn(
                    cardPaddingPx + 40.dp.toPx(),
                    screenWidthPx - cardPaddingPx - 40.dp.toPx()
                )

                if (isTargetInTopHalf) {
                    val arrowTop = targetBounds.bottom + 8.dp.toPx()
                    val arrowBottom = cardTopY - 4.dp.toPx()

                    if (arrowBottom > arrowTop) {
                        // Arrow line from card up to target
                        drawLine(
                            color = highlightColor,
                            start = Offset(startX, arrowBottom),
                            end = Offset(startX, arrowTop),
                            strokeWidth = arrowStroke,
                            cap = StrokeCap.Round
                        )
                        // Arrowhead pointing UP
                        val headPath = Path().apply {
                            moveTo(startX - 8.dp.toPx(), arrowTop + 10.dp.toPx())
                            lineTo(startX, arrowTop)
                            lineTo(startX + 8.dp.toPx(), arrowTop + 10.dp.toPx())
                        }
                        drawPath(
                            path = headPath,
                            color = highlightColor,
                            style = Stroke(width = arrowStroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                } else {
                    val arrowBottom = targetBounds.top - 8.dp.toPx()
                    val arrowTop = cardTopY + with(density) { 220.dp.toPx() }

                    if (arrowBottom > arrowTop) {
                        // Arrow line from card down to target
                        drawLine(
                            color = highlightColor,
                            start = Offset(startX, arrowTop),
                            end = Offset(startX, arrowBottom),
                            strokeWidth = arrowStroke,
                            cap = StrokeCap.Round
                        )
                        // Arrowhead pointing DOWN
                        val headPath = Path().apply {
                            moveTo(startX - 8.dp.toPx(), arrowBottom - 10.dp.toPx())
                            lineTo(startX, arrowBottom)
                            lineTo(startX + 8.dp.toPx(), arrowBottom - 10.dp.toPx())
                        }
                        drawPath(
                            path = headPath,
                            color = highlightColor,
                            style = Stroke(width = arrowStroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            }

            // The Explanatory Card Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, cardTopY.roundToInt()) }
                    .padding(horizontal = cardPaddingDp),
                contentAlignment = Alignment.Center
            ) {
                TourExplanationCard(
                    stepIndex = stepIndex,
                    totalSteps = totalSteps,
                    title = stringResource(currentStep.titleRes),
                    body = stringResource(currentStep.bodyRes),
                    isFirstStep = stepIndex == 0,
                    isLastStep = stepIndex == totalSteps - 1,
                    onNext = onNextStep,
                    onBack = onPreviousStep,
                    onCancel = onCancelTour,
                    modifier = Modifier.widthIn(max = 500.dp)
                )
            }
        } else {
            // Fallback if targetBounds not measured yet: centered explanation card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                TourExplanationCard(
                    stepIndex = stepIndex,
                    totalSteps = totalSteps,
                    title = stringResource(currentStep.titleRes),
                    body = stringResource(currentStep.bodyRes),
                    isFirstStep = stepIndex == 0,
                    isLastStep = stepIndex == totalSteps - 1,
                    onNext = onNextStep,
                    onBack = onPreviousStep,
                    onCancel = onCancelTour,
                    modifier = Modifier.widthIn(max = 500.dp)
                )
            }
        }
    }
}

@Composable
private fun TourExplanationCard(
    stepIndex: Int,
    totalSteps: Int,
    title: String,
    body: String,
    isFirstStep: Boolean,
    isLastStep: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(colors.surfaceRaised)
            .border(1.5.dp, colors.outline, shape)
            .padding(20.dp)
    ) {
        Column {
            // Header row: Step counter & Cancel button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.primarySoft)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${stepIndex + 1} of $totalSteps",
                            style = BirtuTypography.caption.copy(
                                color = colors.primaryStrong,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                BirtuTextButton(
                    text = stringResource(R.string.action_cancel),
                    onClick = onCancel,
                    textColor = colors.primaryStrong,
                    height = 36.dp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = title,
                style = BirtuTypography.titleL.copy(color = colors.text)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Body
            Text(
                text = body,
                style = BirtuTypography.body.copy(color = colors.text2)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Progress indicator pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until totalSteps) {
                    val isCurrent = i == stepIndex
                    val isPast = i < stepIndex

                    val pillWidth by animateFloatAsState(
                        targetValue = if (isCurrent) 24f else 8f,
                        animationSpec = tween(durationMillis = 200),
                        label = "pillWidth"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(width = pillWidth.dp, height = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when {
                                    isCurrent -> colors.primary
                                    isPast -> colors.primarySoft
                                    else -> colors.surfaceHigh
                                }
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isFirstStep) {
                    BirtuTonalButton(
                        text = stringResource(R.string.action_back),
                        onClick = onBack,
                        modifier = Modifier.weight(0.4f),
                        height = 48.dp
                    )
                }

                BirtuPrimaryButton(
                    text = if (isLastStep) stringResource(R.string.action_done) else stringResource(R.string.action_next),
                    onClick = onNext,
                    icon = if (isLastStep) PhosphorIcons.Check else null,
                    modifier = Modifier.weight(if (isFirstStep) 1f else 0.6f),
                    height = 48.dp
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Tour Step 1 - Tangelo")
@Composable
private fun PreviewTourStep1Tangelo() {
    com.birtucansoftware.birtusince.ui.theme.BirtuTheme(preset = com.birtucansoftware.birtusince.ui.theme.ThemePreset.Tangelo) {
        InteractiveSpotlightTourOverlay(
            currentStep = GuidedTourStep(
                stepIndex = 0,
                target = TourHighlightTarget.LOGO_APP_NAME,
                titleRes = R.string.tour_guided_step1_title,
                bodyRes = R.string.tour_guided_step1_body
            ),
            stepIndex = 0,
            totalSteps = 5,
            targetBounds = Rect(16f, 48f, 220f, 96f),
            onNextStep = {},
            onPreviousStep = {},
            onCancelTour = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Tour Step 2 - Arctic")
@Composable
private fun PreviewTourStep2Arctic() {
    com.birtucansoftware.birtusince.ui.theme.BirtuTheme(preset = com.birtucansoftware.birtusince.ui.theme.ThemePreset.Arctic) {
        InteractiveSpotlightTourOverlay(
            currentStep = GuidedTourStep(
                stepIndex = 1,
                target = TourHighlightTarget.HELP_BUTTON,
                titleRes = R.string.tour_guided_step2_title,
                bodyRes = R.string.tour_guided_step2_body
            ),
            stepIndex = 1,
            totalSteps = 5,
            targetBounds = Rect(300f, 48f, 348f, 96f),
            onNextStep = {},
            onPreviousStep = {},
            onCancelTour = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Tour Step 5 - Obsidian")
@Composable
private fun PreviewTourStep5Obsidian() {
    com.birtucansoftware.birtusince.ui.theme.BirtuTheme(preset = com.birtucansoftware.birtusince.ui.theme.ThemePreset.Obsidian) {
        InteractiveSpotlightTourOverlay(
            currentStep = GuidedTourStep(
                stepIndex = 4,
                target = TourHighlightTarget.STATS_TAB,
                titleRes = R.string.tour_guided_step5_title,
                bodyRes = R.string.tour_guided_step5_body
            ),
            stepIndex = 4,
            totalSteps = 5,
            targetBounds = Rect(80f, 650f, 150f, 720f),
            onNextStep = {},
            onPreviousStep = {},
            onCancelTour = {}
        )
    }
}

