package com.birtucansoftware.birtusince.ui.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun HomeScreenLoadingSkeleton(
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "skeletonPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.surface)
                    .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Title skeleton line
                        Box(
                            modifier = Modifier
                                .size(width = 140.dp, height = 18.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.surfaceHigh.copy(alpha = alpha))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        // Timer skeleton line
                        Box(
                            modifier = Modifier
                                .size(width = 100.dp, height = 24.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.surfaceHigh.copy(alpha = alpha))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Subtitle skeleton line
                        Box(
                            modifier = Modifier
                                .size(width = 80.dp, height = 14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(colors.surfaceHigh.copy(alpha = alpha))
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Ring skeleton
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceHigh.copy(alpha = alpha))
                        )
                        // Button skeleton
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(colors.surfaceHigh.copy(alpha = alpha))
                        )
                    }
                }
            }
        }
    }
}
