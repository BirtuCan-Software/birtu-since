package com.birtucansoftware.birtusince.ui.detail

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.birtucansoftware.birtusince.domain.PatternStatsEngine
import com.birtucansoftware.birtusince.domain.TimeEngine
import com.birtucansoftware.birtusince.ui.components.BirtuInfoIconButton
import com.birtucansoftware.birtusince.ui.theme.BirtuTypography
import com.birtucansoftware.birtusince.ui.theme.LocalBirtuColors

@Composable
fun PatternStatsSection(
    patternStats: PatternStatsEngine.PatternStats?,
    targetIntervalMillis: Long?,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Your pattern",
                style = BirtuTypography.titleM.copy(color = colors.text, fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.width(4.dp))
            BirtuInfoIconButton(
                title = "Your Pattern",
                explanation = "'Your pattern' calculates metrics across all your past completed runs. 'Average' shows mean duration, while 'Steady %' measures how consistently your runs remain within your target duration."
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (patternStats != null) {
            // 2x2 grid of tiles: Average, Shortest, Longest, Steady (%)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PatternStatTile(
                    title = "Average",
                    value = TimeEngine.formatCompact(patternStats.averageMillis),
                    modifier = Modifier.weight(1f)
                )
                PatternStatTile(
                    title = "Shortest",
                    value = TimeEngine.formatCompact(patternStats.shortestMillis),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PatternStatTile(
                    title = "Longest",
                    value = TimeEngine.formatCompact(patternStats.longestMillis),
                    modifier = Modifier.weight(1f)
                )
                PatternStatTile(
                    title = "Steady",
                    value = "${patternStats.steadyPercent}%",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar chart below: up to 12 runs, 160 dp tall
            PatternBarChart(
                stats = patternStats,
                targetGoalMillis = targetIntervalMillis,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
        } else {
            Text(
                text = "Start over once more to see your pattern.",
                style = BirtuTypography.body.copy(color = colors.text2)
            )
        }
    }
}

@Composable
fun PatternStatTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current
    Box(
        modifier = modifier
            .height(76.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column {
            Text(
                text = title,
                style = BirtuTypography.caption.copy(color = colors.text2)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = BirtuTypography.timerM.copy(color = colors.text)
            )
        }
    }
}

@Composable
fun PatternBarChart(
    stats: PatternStatsEngine.PatternStats,
    targetGoalMillis: Long?,
    modifier: Modifier = Modifier
) {
    val colors = LocalBirtuColors.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Baseline
            drawLine(
                color = colors.outline,
                start = Offset(0f, h - 10f),
                end = Offset(w, h - 10f),
                strokeWidth = 2f
            )

            val maxDuration = stats.longestMillis.coerceAtLeast(targetGoalMillis ?: 0L).toFloat()
            if (maxDuration <= 0f) return@Canvas

            // Goal line if target set
            if (targetGoalMillis != null && targetGoalMillis > 0) {
                val goalY = (h - 10f) - ((targetGoalMillis.toFloat() / maxDuration) * (h - 30f))
                drawLine(
                    color = colors.text2,
                    start = Offset(0f, goalY),
                    end = Offset(w, goalY),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            // Draw bars
            val barCount = stats.recentDurations.size
            if (barCount > 0) {
                val barWidth = 16.dp.toPx()
                val totalBarsWidth = barCount * barWidth
                val remainingSpace = (w - totalBarsWidth).coerceAtLeast(0f)
                val spacing = if (barCount > 1) remainingSpace / (barCount - 1) else 0f

                stats.recentDurations.forEachIndexed { idx, lapItem ->
                    val barHeight = ((lapItem.durationMillis.toFloat() / maxDuration) * (h - 30f)).coerceAtLeast(4f)
                    val x = idx * (barWidth + spacing)
                    val y = (h - 10f) - barHeight

                    val barColor = if (lapItem.isLateAgainstGoal) colors.late else colors.primary

                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                }
            }
        }
    }
}
