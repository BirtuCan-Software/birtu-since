package com.birtucansoftware.birtusince

import com.birtucansoftware.birtusince.domain.FilterSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterSelectionTest {

    @Test
    fun testDefaultFilterSelectionIsEmpty() {
        val filter = FilterSelection()
        assertTrue(filter.isEmpty)
        assertTrue(filter.includedCategories.isEmpty())
        assertTrue(filter.excludedCategories.isEmpty())
        assertFalse(filter.isLateIncluded)
        assertFalse(filter.isLateExcluded)
    }

    @Test
    fun testToggleIncludeCategory() {
        val filter = FilterSelection()
        val afterInclude = filter.toggleIncludeCategory("Work")
        assertFalse(afterInclude.isEmpty)
        assertTrue(afterInclude.isCategoryIncluded("Work"))
        assertFalse(afterInclude.isCategoryExcluded("Work"))

        // Case insensitivity
        assertTrue(afterInclude.isCategoryIncluded("work"))
        assertTrue(afterInclude.isCategoryIncluded("WORK"))

        // Toggle off
        val afterToggleOff = afterInclude.toggleIncludeCategory("Work")
        assertTrue(afterToggleOff.isEmpty)
        assertFalse(afterToggleOff.isCategoryIncluded("Work"))
    }

    @Test
    fun testToggleExcludeCategory() {
        val filter = FilterSelection()
        val afterExclude = filter.toggleExcludeCategory("Fitness")
        assertFalse(afterExclude.isEmpty)
        assertTrue(afterExclude.isCategoryExcluded("Fitness"))
        assertFalse(afterExclude.isCategoryIncluded("Fitness"))

        // Case insensitivity
        assertTrue(afterExclude.isCategoryExcluded("fitness"))

        // Toggle off
        val afterToggleOff = afterExclude.toggleExcludeCategory("Fitness")
        assertTrue(afterToggleOff.isEmpty)
        assertFalse(afterToggleOff.isCategoryExcluded("Fitness"))
    }

    @Test
    fun testIncludeCategoryRemovesFromExcluded() {
        val filter = FilterSelection().toggleExcludeCategory("Health")
        assertTrue(filter.isCategoryExcluded("Health"))
        assertFalse(filter.isCategoryIncluded("Health"))

        // Now include Health
        val updated = filter.toggleIncludeCategory("Health")
        assertTrue(updated.isCategoryIncluded("Health"))
        assertFalse(updated.isCategoryExcluded("Health"))
    }

    @Test
    fun testExcludeCategoryRemovesFromIncluded() {
        val filter = FilterSelection().toggleIncludeCategory("Personal")
        assertTrue(filter.isCategoryIncluded("Personal"))
        assertFalse(filter.isCategoryExcluded("Personal"))

        // Now exclude Personal
        val updated = filter.toggleExcludeCategory("Personal")
        assertTrue(updated.isCategoryExcluded("Personal"))
        assertFalse(updated.isCategoryIncluded("Personal"))
    }

    @Test
    fun testMultipleIncludedAndExcludedCategories() {
        val filter = FilterSelection()
            .toggleIncludeCategory("Work")
            .toggleIncludeCategory("Study")
            .toggleExcludeCategory("Entertainment")

        assertEquals(setOf("Work", "Study"), filter.includedCategories)
        assertEquals(setOf("Entertainment"), filter.excludedCategories)

        assertTrue(filter.matches(category = "Work", isLate = false))
        assertTrue(filter.matches(category = "Study", isLate = false))
        assertFalse(filter.matches(category = "Entertainment", isLate = false))
        assertFalse(filter.matches(category = "Random", isLate = false))
    }

    @Test
    fun testMatchesOnlyExclusion() {
        val filter = FilterSelection().toggleExcludeCategory("Chores")

        // Anything not Chores should match
        assertTrue(filter.matches(category = "Work", isLate = false))
        assertTrue(filter.matches(category = "Fitness", isLate = true))
        assertTrue(filter.matches(category = "", isLate = false))
        assertFalse(filter.matches(category = "Chores", isLate = false))
        assertFalse(filter.matches(category = "chores", isLate = false))
    }

    @Test
    fun testLateInclusionAndExclusion() {
        val lateIncluded = FilterSelection().toggleIncludeLate()
        assertTrue(lateIncluded.isLateIncluded)
        assertFalse(lateIncluded.isLateExcluded)
        assertTrue(lateIncluded.matches(category = "Work", isLate = true))
        assertFalse(lateIncluded.matches(category = "Work", isLate = false))

        val lateExcluded = FilterSelection().toggleExcludeLate()
        assertFalse(lateExcluded.isLateIncluded)
        assertTrue(lateExcluded.isLateExcluded)
        assertTrue(lateExcluded.matches(category = "Work", isLate = false))
        assertFalse(lateExcluded.matches(category = "Work", isLate = true))

        // Toggling include when excluded clears excluded and sets included
        val switched = lateExcluded.toggleIncludeLate()
        assertTrue(switched.isLateIncluded)
        assertFalse(switched.isLateExcluded)
    }

    @Test
    fun testFromLegacyString() {
        assertEquals(FilterSelection(), FilterSelection.fromLegacyString("All"))
        assertEquals(FilterSelection(), FilterSelection.fromLegacyString(null))
        assertEquals(FilterSelection(), FilterSelection.fromLegacyString(""))
        assertEquals(FilterSelection(isLateIncluded = true), FilterSelection.fromLegacyString("Late"))
        assertEquals(FilterSelection(includedCategories = setOf("Work")), FilterSelection.fromLegacyString("Work"))
    }

    @Test
    fun testToggleAllSelectsEveryGroup() {
        val all = FilterSelection(includedCategories = setOf("Work"), isLateIncluded = true).toggleAll()
        assertTrue(all.isAllSelected)
        assertTrue(all.isEmpty)
        assertTrue(all.isCategorySelectedInAll("Work"))
        assertTrue(all.isCategorySelectedInAll("Health"))
        assertTrue(all.matches("Work", isLate = false))
        assertTrue(all.matches("", isLate = false))
    }

    @Test
    fun testDeselectGroupInAllMode() {
        val all = FilterSelection().toggleAll()
        val next = all.toggleCategoryChip("Chores")
        assertTrue(next.isAllSelected)
        assertFalse(next.isCategorySelectedInAll("Chores"))
        assertTrue(next.isCategorySelectedInAll("Work"))
        assertFalse(next.matches("Chores", isLate = false))
        assertTrue(next.matches("Work", isLate = false))
        assertTrue(next.matches("", isLate = false))

        // Tapping again reselects it
        val reselected = next.toggleCategoryChip("Chores")
        assertTrue(reselected.isCategorySelectedInAll("Chores"))
        assertTrue(reselected.matches("Chores", isLate = false))
    }

    @Test
    fun testToggleAllAgainResetsToDefault() {
        val all = FilterSelection().toggleAll()
        assertEquals(FilterSelection(), all.toggleAll())

        // With a deselected group, All reselects everything instead of leaving the mode
        val withDeselected = all.toggleCategoryChip("Chores")
        assertEquals(FilterSelection(isAllSelected = true), withDeselected.toggleAll())
    }

    @Test
    fun testCategoryChipOutsideAllModeIncludes() {
        val next = FilterSelection().toggleCategoryChip("Work").toggleCategoryChip("Health")
        assertFalse(next.isAllSelected)
        assertEquals(setOf("Work", "Health"), next.includedCategories)
    }
}
