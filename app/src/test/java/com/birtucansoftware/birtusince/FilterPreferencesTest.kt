package com.birtucansoftware.birtusince

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.birtucansoftware.birtusince.data.PreferencesManager
import com.birtucansoftware.birtusince.domain.FilterSelection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FilterPreferencesTest {

    private lateinit var context: Context
    private lateinit var preferences: PreferencesManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        preferences = PreferencesManager(context)
    }

    @Test
    fun testDefaultFilterSelectionIsPersisted() = runBlocking {
        val initial = preferences.filterSelectionFlow.first()
        assertTrue(initial.isEmpty)
    }

    @Test
    fun testSaveAndRestoreFilterSelection() = runBlocking {
        val selection = FilterSelection(
            includedCategories = setOf("Work", "Health"),
            excludedCategories = setOf("Chores"),
            isLateIncluded = false,
            isLateExcluded = true
        )

        preferences.setFilterSelection(selection)

        // Read back from DataStore
        val restored = preferences.filterSelectionFlow.first()
        assertEquals(setOf("Work", "Health"), restored.includedCategories)
        assertEquals(setOf("Chores"), restored.excludedCategories)
        assertFalse(restored.isLateIncluded)
        assertTrue(restored.isLateExcluded)
    }

    @Test
    fun testLegacySetSelectedFilter() = runBlocking {
        preferences.setSelectedFilter("Personal")
        val restored = preferences.filterSelectionFlow.first()
        assertEquals(setOf("Personal"), restored.includedCategories)
        assertTrue(restored.excludedCategories.isEmpty())

        preferences.setSelectedFilter("All")
        val cleared = preferences.filterSelectionFlow.first()
        assertTrue(cleared.isEmpty)
    }
}
