package com.birtucansoftware.birtusince.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.delay

@Composable
fun BirtuBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onAddClick: () -> Unit,
    shouldNudge: Boolean = false,
    isReducedMotion: Boolean = false,
    addButtonModifier: Modifier = Modifier,
    statsTabModifier: Modifier = Modifier,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current

    // Nudge animation values
    val nudgeOffsetY = remember { Animatable(0f) }
    val nudgeRotation = remember { Animatable(0f) }
    var nudgeCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(shouldNudge, isReducedMotion) {
        if (!shouldNudge || isReducedMotion) {
            nudgeOffsetY.snapTo(0f)
            nudgeRotation.snapTo(0f)
            return@LaunchedEffect
        }

        while (nudgeCount < 3) {
            delay(5000)
            nudgeCount++
            // Rises another 4dp (-4), tilts 8 degrees
            nudgeOffsetY.animateTo(-4f, tween(160))
            nudgeRotation.animateTo(8f, tween(160))
            // Settles back with spring
            nudgeOffsetY.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
            nudgeRotation.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        }
    }

    // Gentle ambient pulse glow for the attractive new button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isReducedMotion) 1f else 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Top edge 1 dp outline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(colors.outline)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Home
            NavTabItem(
                label = "Home",
                icon = PhosphorIcons.House,
                isSelected = currentRoute == "home",
                onClick = { onNavigate("home") },
                modifier = Modifier.weight(1f)
            )

            // Tab 2: Stats
            NavTabItem(
                label = "Stats",
                icon = PhosphorIcons.ChartBar,
                isSelected = currentRoute == "stats",
                onClick = { onNavigate("stats") },
                modifier = Modifier
                    .weight(1f)
                    .then(statsTabModifier)
            )

            // Center slot placeholder for Nudge Add button
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                // Label under Add Button
                Text(
                    text = "Track New",
                    style = BirtuTypography.caption.copy(
                        color = colors.primaryStrong,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                )
            }

            // Tab 4: Put away
            NavTabItem(
                label = "Put away",
                icon = PhosphorIcons.Archive,
                isSelected = currentRoute == "put_away",
                onClick = { onNavigate("put_away") },
                modifier = Modifier.weight(1f)
            )

            // Tab 5: Settings
            NavTabItem(
                label = "Settings",
                icon = PhosphorIcons.GearSix,
                isSelected = currentRoute == "settings",
                onClick = { onNavigate("settings") },
                modifier = Modifier.weight(1f)
            )
        }

        // Center Attractive Hero Add Button:
        // Sits elevated (-16.dp), tactile spring physics, gradient surface, dual-ring outline,
        // and distinct Sparkle/Plus duotone iconography
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp + nudgeOffsetY.value.dp)
                .graphicsLayer {
                    rotationZ = nudgeRotation.value
                    scaleX = pulseScale
                    scaleY = pulseScale
                }
                .size(68.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(colors.bg) // 4 dp outer cutout ring in canvas bg color
                .padding(4.dp)
                .then(addButtonModifier),
            contentAlignment = Alignment.Center
        ) {
            // Button Container with vibrant gradient and neon border
            val gradientBrush = Brush.linearGradient(
                colors = listOf(
                    colors.primary,
                    colors.primaryStrong
                )
            )

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .shadow(
                        elevation = if (colors.isDark) 10.dp else 6.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = colors.primary,
                        spotColor = colors.primary
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(gradientBrush)
                    .border(
                        width = 1.5.dp,
                        color = Color.White.copy(alpha = if (colors.isDark) 0.35f else 0.55f),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .birtuPress(scaleDown = 0.92f, shape = RoundedCornerShape(22.dp), onClick = onAddClick),
                contentAlignment = Alignment.Center
            ) {
                // Center icon combination: Custom Sparkle + Plus for high visual attractiveness
                Box(contentAlignment = Alignment.Center) {
                    PhosphorIcon(
                        icon = PhosphorIcons.Sparkle,
                        contentDescription = null,
                        tint = colors.onPrimary.copy(alpha = 0.28f),
                        secondaryTint = Color.White.copy(alpha = 0.25f),
                        size = 36.dp
                    )
                    PhosphorIcon(
                        icon = PhosphorIcons.Plus,
                        contentDescription = "Track new event",
                        tint = colors.onPrimary,
                        size = 28.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: PhosphorIconData,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    val shape = RoundedCornerShape(16.dp)

    val iconScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        animationSpec = tween(durationMillis = 100),
        label = "navIconScale"
    )

    val pillWidth by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isSelected) 56.dp else 40.dp,
        animationSpec = tween(durationMillis = 100),
        label = "navPillWidth"
    )

    val pillColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) colors.primarySoft else Color.Transparent,
        animationSpec = tween(durationMillis = 100),
        label = "navPillColor"
    )

    val textColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) colors.primaryStrong else colors.text2,
        animationSpec = tween(durationMillis = 100),
        label = "navTextColor"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(shape)
            .birtuPress(
                scaleDown = 0.90f,
                shape = shape,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Active pill (56 x 32 dp, radius 16)
            Box(
                modifier = Modifier
                    .width(pillWidth)
                    .height(32.dp)
                    .clip(shape)
                    .background(pillColor),
                contentAlignment = Alignment.Center
            ) {
                PhosphorIcon(
                    icon = icon,
                    contentDescription = label,
                    tint = textColor,
                    size = 24.dp,
                    modifier = Modifier.graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                style = BirtuTypography.caption.copy(
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            )
        }
    }
}
