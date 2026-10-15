package com.birtucansoftware.birtusince.domain

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Deterministic Elapsed Time Computation Engine implementing FR-ELP-001, FR-ELP-002, FR-ELP-003.
 * Elapsed time is always calculated as (now - start), never an incrementing counter loop.
 */
object TimeEngine {

    const val UNIT_AUTO = "auto"
    const val UNIT_DAYS = "days"
    const val UNIT_HOURS = "hours"
    const val UNIT_MIN = "min"
    const val UNIT_SEC = "sec"

    data class FormattedDuration(
        val primaryText: String,
        val secondaryUnit: String? = null,
        val isClockAnomaly: Boolean = false,
        val spokenWords: String = ""
    )

    /**
     * Computes elapsed millis from start time to now (or pause time).
     * Returns 0 if clock looks off (now < start).
     */
    fun computeElapsedMillis(
        startEpochMillis: Long,
        nowEpochMillis: Long = System.currentTimeMillis(),
        isPaused: Boolean = false,
        pauseEpochMillis: Long? = null
    ): Pair<Long, Boolean> {
        val effectiveEnd = if (isPaused && pauseEpochMillis != null) pauseEpochMillis else nowEpochMillis
        val delta = effectiveEnd - startEpochMillis
        return if (delta < 0) {
            0L to true // Clock anomaly detected
        } else {
            delta to false
        }
    }

    /**
     * Formats duration according to FR-ELP-002 (Dynamic Precision Scaling) or chosen unit (FR-ELP-003).
     */
    fun formatDuration(
        elapsedMillis: Long,
        unit: String = UNIT_AUTO,
        isClockAnomaly: Boolean = false
    ): FormattedDuration {
        if (isClockAnomaly) {
            return FormattedDuration(
                primaryText = "00:00:00",
                isClockAnomaly = true,
                spokenWords = "Clock looks off"
            )
        }

        val totalSec = elapsedMillis / 1000
        val totalMin = totalSec / 60
        val totalHours = totalMin / 60
        val totalDays = totalHours / 24

        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

        when (unit.lowercase()) {
            UNIT_DAYS -> {
                val daysDouble = elapsedMillis.toDouble() / (1000.0 * 60 * 60 * 24)
                val text = String.format(Locale.getDefault(), "%.1f", daysDouble)
                return FormattedDuration(
                    primaryText = "$text days",
                    secondaryUnit = "days",
                    spokenWords = "$text days"
                )
            }
            UNIT_HOURS -> {
                val text = numberFormat.format(totalHours)
                return FormattedDuration(
                    primaryText = "$text hours",
                    secondaryUnit = "hours",
                    spokenWords = "$text hours"
                )
            }
            UNIT_MIN -> {
                val text = numberFormat.format(totalMin)
                return FormattedDuration(
                    primaryText = "$text min",
                    secondaryUnit = "min",
                    spokenWords = "$text minutes"
                )
            }
            UNIT_SEC -> {
                val text = numberFormat.format(totalSec)
                return FormattedDuration(
                    primaryText = "$text sec",
                    secondaryUnit = "sec",
                    spokenWords = "$text seconds"
                )
            }
        }

        // UNIT_AUTO Precision Scaling (FR-ELP-002)
        // 1. Durations < 60 minutes: HH:MM:SS
        if (totalMin < 60) {
            val hh = totalHours
            val mm = totalMin % 60
            val ss = totalSec % 60
            val text = String.format(Locale.US, "%02d:%02d:%02d", hh, mm, ss)
            val spoken = buildString {
                if (hh > 0) append("$hh hours, ")
                append("$mm minutes, $ss seconds")
            }
            return FormattedDuration(primaryText = text, spokenWords = spoken)
        }

        // 2. Durations >= 1 hour and < 24 hours: HH:MM
        if (totalHours < 24) {
            val hh = totalHours
            val mm = totalMin % 60
            val text = String.format(Locale.US, "%02d:%02d", hh, mm)
            return FormattedDuration(
                primaryText = text,
                spokenWords = "$hh hours, $mm minutes"
            )
        }

        // 3. Durations >= 24 hours and < 30 days: DDd HHh MMm
        if (totalDays < 30) {
            val dd = totalDays
            val hh = totalHours % 24
            val mm = totalMin % 60
            val text = String.format(Locale.US, "%dd %02dh %02dm", dd, hh, mm)
            return FormattedDuration(
                primaryText = text,
                spokenWords = "$dd days, $hh hours, $mm minutes"
            )
        }

        // 4. Durations >= 30 days: MMmo DDd HHh
        val months = totalDays / 30
        val remainingDays = totalDays % 30
        val hh = totalHours % 24
        val text = String.format(Locale.US, "%dmo %02dd %02dh", months, remainingDays, hh)
        return FormattedDuration(
            primaryText = text,
            spokenWords = "$months months, $remainingDays days, $hh hours"
        )
    }

    /**
     * Compact format for chips and sheets, e.g. "12d 3h", "4h 12m", "45m"
     */
    fun formatCompact(millis: Long): String {
        val totalSec = millis / 1000
        val totalMin = totalSec / 60
        val totalHours = totalMin / 60
        val totalDays = totalHours / 24

        return when {
            totalDays >= 30 -> "${totalDays / 30}mo ${totalDays % 30}d"
            totalDays > 0 -> "${totalDays}d ${totalHours % 24}h"
            totalHours > 0 -> "${totalHours}h ${totalMin % 60}m"
            totalMin > 0 -> "${totalMin}m"
            else -> "${totalSec}s"
        }
    }

    /**
     * "Since Mon, 9:14 AM" or "Since Sep 14, 9:14 AM"
     */
    fun formatSinceDate(epochMillis: Long): String {
        val now = System.currentTimeMillis()
        val deltaDays = (now - epochMillis) / (1000 * 60 * 60 * 24)
        val pattern = if (deltaDays < 7) {
            "EEE, h:mm a"
        } else {
            "MMM d, h:mm a"
        }
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(Date(epochMillis))
    }

    /**
     * Date range format for past runs, e.g. "Sep 2 to Sep 14"
     */
    fun formatDateRange(startMillis: Long, endMillis: Long): String {
        val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
        val startStr = sdf.format(Date(startMillis))
        val endStr = sdf.format(Date(endMillis))
        return "$startStr to $endStr"
    }
}
