package com.birtucansoftware.birtusince.domain

import kotlin.math.sqrt

/**
 * Pattern Statistics Engine implementing FR-CAD-003.
 * Derives Mean, Min, Max, and Steadiness (%) when >= 2 historical laps exist.
 */
object PatternStatsEngine {

    data class PatternStats(
        val count: Int,
        val averageMillis: Long,
        val shortestMillis: Long,
        val longestMillis: Long,
        val steadyPercent: Int,
        val recentDurations: List<LapDurationItem>
    )

    data class LapDurationItem(
        val runNumber: Int,
        val durationMillis: Long,
        val isLateAgainstGoal: Boolean
    )

    fun calculate(
        durations: List<Long>,
        targetGoalMillis: Long? = null
    ): PatternStats? {
        if (durations.size < 2) return null

        val count = durations.size
        val sumDouble = durations.sumOf { it.toDouble() }
        val meanDouble = sumDouble / count
        val mean = meanDouble.toLong()

        var min = Long.MAX_VALUE
        var max = Long.MIN_VALUE
        var varianceSum = 0.0

        for (d in durations) {
            if (d < min) min = d
            if (d > max) max = d
            val diff = d.toDouble() - mean.toDouble()
            varianceSum += diff * diff
        }

        val stdDev = sqrt(varianceSum / count)
        val cv = if (mean > 0) stdDev / mean.toDouble() else 0.0
        val steadyPercent = ((1.0 - cv) * 100.0).toInt().coerceIn(0, 100)

        // Up to the last 12 runs, oldest to newest for left-to-right rendering
        val recentLaps = durations.takeLast(12).mapIndexed { idx, d ->
            LapDurationItem(
                runNumber = (durations.size - durations.takeLast(12).size + idx + 1),
                durationMillis = d,
                isLateAgainstGoal = targetGoalMillis != null && d > targetGoalMillis
            )
        }

        return PatternStats(
            count = count,
            averageMillis = mean,
            shortestMillis = min,
            longestMillis = max,
            steadyPercent = steadyPercent,
            recentDurations = recentLaps
        )
    }
}
