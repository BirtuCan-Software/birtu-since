package com.birtucansoftware.birtusince

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.birtucansoftware.birtusince.data.BirtuDatabase
import com.birtucansoftware.birtusince.data.BirtuRepository
import com.birtucansoftware.birtusince.data.ItemEntity
import com.birtucansoftware.birtusince.data.LapEntity
import com.birtucansoftware.birtusince.data.PreferencesManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class BackupRestoreTest {

    private lateinit var db: BirtuDatabase
    private lateinit var repository: BirtuRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, BirtuDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val prefs = PreferencesManager(context)
        repository = BirtuRepository(db.itemDao(), prefs)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testExportAndRestoreReplaceAll() = runBlocking {
        // Given an item and a lap
        val itemId = db.itemDao().insertItem(
            ItemEntity(
                title = "Water intake",
                startEpochMillis = 1000L,
                category = "Health",
                targetIntervalMillis = 86400000L
            )
        )
        db.itemDao().insertLap(
            LapEntity(
                itemId = itemId,
                runNumber = 1,
                startEpochMillis = 500L,
                endEpochMillis = 1000L,
                durationMillis = 500L,
                note = "First run"
            )
        )

        // Export
        val json = repository.exportBackupJson()
        assertNotNull(json)
        assertTrue(json.contains("Water intake"))

        // Add a second item that should be wiped on replaceAll
        db.itemDao().insertItem(
            ItemEntity(
                title = "Temporary item",
                startEpochMillis = 2000L
            )
        )
        assertEquals(2, db.itemDao().getAllItems().size)

        // Restore Replace All
        val success = repository.restoreBackupJson(json, replaceAll = true)
        assertTrue(success)

        val itemsAfter = db.itemDao().getAllItems()
        assertEquals(1, itemsAfter.size)
        assertEquals("Water intake", itemsAfter[0].title)

        val lapsAfter = db.itemDao().getAllLaps()
        assertEquals(1, lapsAfter.size)
        assertEquals(itemsAfter[0].id, lapsAfter[0].itemId)
    }

    @Test
    fun testExportAndRestoreMerge() = runBlocking {
        // Given an item
        val itemId = db.itemDao().insertItem(
            ItemEntity(
                title = "Coffee",
                startEpochMillis = 1000L
            )
        )
        val lapId = db.itemDao().insertLap(
            LapEntity(
                itemId = itemId,
                runNumber = 1,
                startEpochMillis = 500L,
                endEpochMillis = 1000L,
                durationMillis = 500L
            )
        )

        val json = repository.exportBackupJson()

        // Restore Merge (replaceAll = false)
        val success = repository.restoreBackupJson(json, replaceAll = false)
        assertTrue(success)

        val itemsAfter = db.itemDao().getAllItems()
        assertEquals(2, itemsAfter.size)

        val lapsAfter = db.itemDao().getAllLaps()
        assertEquals(2, lapsAfter.size)
    }
}
