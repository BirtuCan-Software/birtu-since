package com.birtucansoftware.birtusince.ui.stats

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.birtucansoftware.birtusince.data.ItemEntity
import com.birtucansoftware.birtusince.domain.ItemState
import com.birtucansoftware.birtusince.domain.ItemStateResolver
import com.birtucansoftware.birtusince.domain.TimeEngine
import com.birtucansoftware.birtusince.ui.components.BirtuInfoIconButton
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcon
import com.birtucansoftware.birtusince.ui.icons.PhosphorIcons
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun StatsScreen(
    items: List<ItemEntity>,
    currentTick: Long,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current

    val resolvedItems = remember(items, currentTick) {
        items.map { item ->
            val derived = ItemStateResolver.resolve(
                startEpochMillis = item.startEpochMillis,
                isPaused = item.isPaused,
                pauseEpochMillis = item.pauseTimestamp,
                isArchived = item.isArchived,
                isPrivate = false,
                isUnlockedThisSession = true,
                targetIntervalMillis = item.targetIntervalMillis,
                unit = TimeEngine.UNIT_AUTO,
                nowEpochMillis = currentTick
            )
            item to derived
        }
    }

    val runningCount = remember(resolvedItems) {
        resolvedItems.count { it.second.state is ItemState.Running || it.second.state is ItemState.Late }
    }
    val lateCount = remember(resolvedItems) {
        resolvedItems.count { it.second.state is ItemState.Late }
    }
    val totalRestarts = remember(items) {
        items.sumOf { it.lifetimeRestarts }
    }

    // Top 5 longest runs
    val longestRuns = remember(resolvedItems) {
        resolvedItems
            .sortedByDescending { it.second.elapsedMillis }
            .take(5)
    }

    // Top 5 most restarts
    val mostRestarts = remember(items) {
        items
            .sortedByDescending { it.lifetimeRestarts }
            .take(5)
    }

    val maxElapsed = longestRuns.firstOrNull()?.second?.elapsedMillis?.toFloat() ?: 1f
    val maxRestartsCount = mostRestarts.firstOrNull()?.lifetimeRestarts?.toFloat() ?: 1f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        if (items.size < 2) {
            // Empty State (UI Spec 9.6)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(if (colors.isDark) colors.surfaceRaised else colors.primarySoft)
                            .border(
                                1.dp,
                                if (colors.isDark) colors.outline else colors.primaryStrong.copy(alpha = 0.35f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        PhosphorIcon(
                            icon = PhosphorIcons.ChartBar,
                            contentDescription = null,
                            tint = colors.primaryStrong,
                            size = 56.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Add a few items to see more.",
                        style = BirtuTypography.body.copy(color = colors.text2)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Three summary tiles: Running, Late, Restarts (104 x 80 dp each, 8 dp gap)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatsSummaryTile(
                        number = "$runningCount",
                        label = "Running",
                        numberColor = colors.text,
                        modifier = Modifier.weight(1f)
                    )

                    StatsSummaryTile(
                        number = "$lateCount",
                        label = "Late",
                        numberColor = if (lateCount > 0) colors.late else colors.text,
                        modifier = Modifier.weight(1f)
                    )

                    StatsSummaryTile(
                        number = "$totalRestarts",
                        label = "Restarts",
                        numberColor = colors.text,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Longest runs section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Longest runs",
                        style = BirtuTypography.titleM.copy(color = colors.text, fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    BirtuInfoIconButton(
                        title = "Longest Runs",
                        explanation = "Ranks your active records with the highest elapsed time currently tracked without restarting."
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    longestRuns.forEach { (item, derived) ->
                        val ratio = (derived.elapsedMillis.toFloat() / maxElapsed).coerceIn(0.05f, 1f)
                        val animatedRatio by animateFloatAsState(
                            targetValue = ratio,
                            animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
                            label = "longestRunRatio"
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    style = BirtuTypography.body.copy(color = colors.text),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = TimeEngine.formatCompact(derived.elapsedMillis),
                                    style = BirtuTypography.label.copy(
                                        color = colors.primaryStrong,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            // Bar over surfaceHigh track
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(colors.surfaceHigh)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(animatedRatio)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colors.primary)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Most restarts section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Most restarts",
                        style = BirtuTypography.titleM.copy(color = colors.text, fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    BirtuInfoIconButton(
                        title = "Most Restarts",
                        explanation = "Shows how many times you have clicked 'Start over' on each record. Useful for habit tracking and frequency analysis."
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    mostRestarts.forEach { item ->
                        val ratio = if (maxRestartsCount > 0) {
                            (item.lifetimeRestarts.toFloat() / maxRestartsCount).coerceIn(0.05f, 1f)
                        } else 0f
                        val animatedRatio by animateFloatAsState(
                            targetValue = ratio,
                            animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
                            label = "restartsRatio"
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    style = BirtuTypography.body.copy(color = colors.text),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${item.lifetimeRestarts}",
                                    style = BirtuTypography.label.copy(
                                        color = colors.text,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(colors.surfaceHigh)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(animatedRatio)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colors.primarySoft)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun StatsSummaryTile(
    number: String,
    label: String,
    numberColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = number,
                style = BirtuTypography.titleL.copy(
                    color = numberColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = BirtuTypography.caption.copy(color = colors.text2)
            )
        }
    }
}
