package com.birtucansoftware.birtusince

import com.birtucansoftware.birtusince.domain.ItemState
import com.birtucansoftware.birtusince.domain.ItemStateResolver
import com.birtucansoftware.birtusince.domain.PatternStatsEngine
import com.birtucansoftware.birtusince.domain.TimeEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainLogicUnitTest {

    @Test
    fun testElapsedComputation_deterministic() {
        val start = 1_000_000L
        val now = 1_060_000L // 60 seconds later
        val (elapsed, isClockOff) = TimeEngine.computeElapsedMillis(start, now)
        assertEquals(60_000L, elapsed)
        assertFalse(isClockOff)
    }

    @Test
    fun testClockAnomaly_handledGracefullyWithoutCrash() {
        val start = 2_000_000L
        val now = 1_000_000L // clock moved backward
        val (elapsed, isClockOff) = TimeEngine.computeElapsedMillis(start, now)
        assertEquals(0L, elapsed)
        assertTrue(isClockOff)

        val formatted = TimeEngine.formatDuration(elapsed, TimeEngine.UNIT_AUTO, isClockAnomaly = true)
        assertTrue(formatted.isClockAnomaly)
        assertEquals("Clock looks off", formatted.spokenWords)
    }

    @Test
    fun testPrecisionScaling_under60Minutes() {
        val elapsed = (4 * 60 + 12) * 1000L // 4m 12s
        val formatted = TimeEngine.formatDuration(elapsed, TimeEngine.UNIT_AUTO)
        assertEquals("00:04:12", formatted.primaryText)
    }

    @Test
    fun testPrecisionScaling_between1And24Hours() {
        val elapsed = (3 * 3600 + 45 * 60) * 1000L // 3h 45m
        val formatted = TimeEngine.formatDuration(elapsed, TimeEngine.UNIT_AUTO)
        assertEquals("03:45", formatted.primaryText)
    }

    @Test
    fun testPrecisionScaling_between24HoursAnd30Days() {
        val elapsed = (3L * 86400L + 4L * 3600L + 12L * 60L) * 1000L // 3d 4h 12m
        val formatted = TimeEngine.formatDuration(elapsed, TimeEngine.UNIT_AUTO)
        assertEquals("3d 04h 12m", formatted.primaryText)
    }

    @Test
    fun testPrecisionScaling_over30Days() {
        val elapsed = (122L * 86400L + 11L * 3600L) * 1000L // 4mo 2d 11h
        val formatted = TimeEngine.formatDuration(elapsed, TimeEngine.UNIT_AUTO)
        assertEquals("4mo 02d 11h", formatted.primaryText)
    }

    @Test
    fun testUnitConversions() {
        val elapsed = (73L * 86400L + 10L * 3600L) * 1000L // ~73.4 days

        val daysFormat = TimeEngine.formatDuration(elapsed, TimeEngine.UNIT_DAYS)
        assertTrue(daysFormat.primaryText.contains("73.4") || daysFormat.primaryText.contains("73,4"))

        val hoursFormat = TimeEngine.formatDuration(elapsed, TimeEngine.UNIT_HOURS)
        assertTrue(hoursFormat.primaryText.contains("hours"))

        val minFormat = TimeEngine.formatDuration(elapsed, TimeEngine.UNIT_MIN)
        assertTrue(minFormat.primaryText.contains("min"))

        val secFormat = TimeEngine.formatDuration(elapsed, TimeEngine.UNIT_SEC)
        assertTrue(secFormat.primaryText.contains("sec"))
    }

    @Test
    fun testPatternStats_lessThan2RunsReturnsNull() {
        val stats0 = PatternStatsEngine.calculate(emptyList())
        assertNull(stats0)

        val stats1 = PatternStatsEngine.calculate(listOf(100_000L))
        assertNull(stats1)
    }

    @Test
    fun testPatternStats_twoOrMoreRunsCalculatesMetrics() {
        val laps = listOf(10_000L, 20_000L, 30_000L)
        val stats = PatternStatsEngine.calculate(laps)

        assertNotNull(stats)
        assertEquals(3, stats!!.count)
        assertEquals(20_000L, stats.averageMillis)
        assertEquals(10_000L, stats.shortestMillis)
        assertEquals(30_000L, stats.longestMillis)
        assertTrue(stats.steadyPercent in 0..100)
    }

    @Test
    fun testPatternStats_identicalRunsGives100PercentSteadiness() {
        val laps = listOf(15_000L, 15_000L, 15_000L)
        val stats = PatternStatsEngine.calculate(laps)

        assertNotNull(stats)
        assertEquals(100, stats!!.steadyPercent)
    }

    @Test
    fun testItemStateResolver_lateStateDerived() {
        val start = 1_000_000L
        val target = 50_000L // 50 seconds target
        val now = 1_060_000L // 60 seconds elapsed -> Late by 10 seconds

        val derived = ItemStateResolver.resolve(
            startEpochMillis = start,
            isPaused = false,
            pauseEpochMillis = null,
            isArchived = false,
            isPrivate = false,
            isUnlockedThisSession = true,
            targetIntervalMillis = target,
            nowEpochMillis = now
        )

        assertTrue(derived.state is ItemState.Late)
        assertEquals(10_000L, (derived.state as ItemState.Late).lateByMillis)
        assertTrue(derived.progressFraction > 1.0f)
    }

    @Test
    fun testItemStateResolver_lockedStateConcealsTitleAndTime() {
        val derived = ItemStateResolver.resolve(
            startEpochMillis = 1_000_000L,
            isPaused = false,
            pauseEpochMillis = null,
            isArchived = false,
            isPrivate = true,
            isUnlockedThisSession = false,
            targetIntervalMillis = 60_000L,
            nowEpochMillis = 1_030_000L
        )

        assertEquals(ItemState.Locked, derived.state)
        assertEquals("Locked", derived.formattedDuration.primaryText)
    }
}
