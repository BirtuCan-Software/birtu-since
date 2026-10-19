package com.birtucansoftware.birtusince

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.birtucansoftware.birtusince.data.BackupSerializer
import com.birtucansoftware.birtusince.data.BirtuDatabase
import com.birtucansoftware.birtusince.data.BirtuRepository
import com.birtucansoftware.birtusince.data.ItemEntity
import com.birtucansoftware.birtusince.data.PreferencesManager
import com.birtucansoftware.birtusince.domain.ItemStateResolver
import com.birtucansoftware.birtusince.domain.SortOption
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
class SortAndPinTest {

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
    fun testTogglePinItem() = runBlocking {
        val itemId = repository.createItem(title = "Item 1", startEpochMillis = 1000L)
        val initial = repository.getItem(itemId)
        assertFalse(initial!!.isPinned)

        repository.togglePinItem(itemId)
        val afterPin = repository.getItem(itemId)
        assertTrue(afterPin!!.isPinned)
        assertTrue(afterPin.lastModifiedEpochMillis >= initial.lastModifiedEpochMillis)

        repository.togglePinItem(itemId)
        val afterUnpin = repository.getItem(itemId)
        assertFalse(afterUnpin!!.isPinned)
    }

    @Test
    fun testPinnedItemsStayAtTopRegardlessOfSort() {
        val item1 = ItemEntity(
            id = 1,
            title = "Unpinned High Restarts",
            startEpochMillis = 5000L,
            lifetimeRestarts = 50,
            isPinned = false,
            createdAtEpochMillis = 1000L
        )
        val item2 = ItemEntity(
            id = 2,
            title = "Pinned Low Restarts",
            startEpochMillis = 9000L,
            lifetimeRestarts = 1,
            isPinned = true,
            createdAtEpochMillis = 2000L
        )
        val item3 = ItemEntity(
            id = 3,
            title = "Unpinned Low Restarts",
            startEpochMillis = 1000L,
            lifetimeRestarts = 0,
            isPinned = false,
            createdAtEpochMillis = 3000L
        )

        val items = listOf(item1, item2, item3)
        val resolved = items.map { item ->
            item to ItemStateResolver.resolve(
                startEpochMillis = item.startEpochMillis,
                isPaused = false,
                pauseEpochMillis = null,
                isArchived = false,
                isPrivate = false,
                isUnlockedThisSession = true,
                targetIntervalMillis = null,
                unit = "auto",
                nowEpochMillis = 10000L
            )
        }

        // Sort by Most Frequent Laps (descending)
        val sorted = resolved.sortedWith { a, b ->
            if (a.first.isPinned != b.first.isPinned) {
                if (a.first.isPinned) -1 else 1
            } else {
                -a.first.lifetimeRestarts.compareTo(b.first.lifetimeRestarts)
            }
        }

        // Pinned item must be first even though it has fewer restarts!
        assertEquals("Pinned Low Restarts", sorted[0].first.title)
        assertEquals("Unpinned High Restarts", sorted[1].first.title)
        assertEquals("Unpinned Low Restarts", sorted[2].first.title)
    }

    @Test
    fun testSortByCreatedAt() {
        val itemOld = ItemEntity(id = 1, title = "Old", startEpochMillis = 1000L, createdAtEpochMillis = 1000L)
        val itemMid = ItemEntity(id = 2, title = "Mid", startEpochMillis = 2000L, createdAtEpochMillis = 2000L)
        val itemNew = ItemEntity(id = 3, title = "New", startEpochMillis = 3000L, createdAtEpochMillis = 3000L)

        val list = listOf(itemMid, itemOld, itemNew)

        // Descending (newest first)
        val newestFirst = list.sortedWith { a, b ->
            -a.createdAtEpochMillis.compareTo(b.createdAtEpochMillis)
        }
        assertEquals("New", newestFirst[0].title)
        assertEquals("Mid", newestFirst[1].title)
        assertEquals("Old", newestFirst[2].title)

        // Ascending (oldest first)
        val oldestFirst = list.sortedWith { a, b ->
            a.createdAtEpochMillis.compareTo(b.createdAtEpochMillis)
        }
        assertEquals("Old", oldestFirst[0].title)
        assertEquals("Mid", oldestFirst[1].title)
        assertEquals("New", oldestFirst[2].title)
    }

    @Test
    fun testSortByLongestElapsed() {
        val now = 100000L
        val itemRunningLong = ItemEntity(id = 1, title = "Running Long", startEpochMillis = 10000L) // 90000ms elapsed
        val itemRunningShort = ItemEntity(id = 2, title = "Running Short", startEpochMillis = 80000L) // 20000ms elapsed

        val resolved = listOf(itemRunningShort, itemRunningLong).map { item ->
            item to ItemStateResolver.resolve(
                startEpochMillis = item.startEpochMillis,
                isPaused = false,
                pauseEpochMillis = null,
                isArchived = false,
                isPrivate = false,
                isUnlockedThisSession = true,
                targetIntervalMillis = null,
                unit = "auto",
                nowEpochMillis = now
            )
        }

        val longestFirst = resolved.sortedWith { a, b ->
            -a.second.elapsedMillis.compareTo(b.second.elapsedMillis)
        }
        assertEquals("Running Long", longestFirst[0].first.title)
        assertEquals("Running Short", longestFirst[1].first.title)

        val shortestFirst = resolved.sortedWith { a, b ->
            a.second.elapsedMillis.compareTo(b.second.elapsedMillis)
        }
        assertEquals("Running Short", shortestFirst[0].first.title)
        assertEquals("Running Long", shortestFirst[1].first.title)
    }

    @Test
    fun testSortByLastModified() {
        val itemOldMod = ItemEntity(id = 1, title = "Modified Long Ago", startEpochMillis = 1000L, lastModifiedEpochMillis = 1000L)
        val itemNewMod = ItemEntity(id = 2, title = "Modified Recently", startEpochMillis = 1000L, lastModifiedEpochMillis = 9000L)

        val list = listOf(itemOldMod, itemNewMod)
        val sortedDesc = list.sortedWith { a, b ->
            -a.lastModifiedEpochMillis.compareTo(b.lastModifiedEpochMillis)
        }
        assertEquals("Modified Recently", sortedDesc[0].title)
        assertEquals("Modified Long Ago", sortedDesc[1].title)
    }

    @Test
    fun testBackupSerializationWithPinAndLastModified() {
        val items = listOf(
            ItemEntity(
                id = 42,
                title = "Pinned Item",
                startEpochMillis = 5000L,
                isPinned = true,
                lastModifiedEpochMillis = 88888L,
                isOneTime = true
            )
        )
        val json = BackupSerializer.serialize(items, emptyList())
        assertTrue(json.contains("\"isPinned\": true"))
        assertTrue(json.contains("\"lastModifiedEpochMillis\": 88888"))
        assertTrue(json.contains("\"isOneTime\": true"))

        val deserialized = BackupSerializer.deserialize(json)
        assertEquals(1, deserialized!!.items.size)
        val item = deserialized.items[0].entity
        assertEquals("Pinned Item", item.title)
        assertTrue(item.isPinned)
        assertEquals(88888L, item.lastModifiedEpochMillis)
        assertTrue(item.isOneTime)
    }

    @Test
    fun testOneTimeEventDefaultIsFalse() = runBlocking {
        val itemId = repository.createItem(title = "Default Item", startEpochMillis = 1000L)
        val item = repository.getItem(itemId)
        assertNotNull(item)
        assertFalse(item!!.isOneTime)

        // Repeatable item can be started over
        val lap = repository.startOver(itemId, 2000L)
        assertNotNull(lap)
        val updated = repository.getItem(itemId)
        assertEquals(1, updated!!.lifetimeRestarts)
    }

    @Test
    fun testOneTimeEventCannotBeStartedOver() = runBlocking {
        val itemId = repository.createItem(title = "Graduation", startEpochMillis = 1000L, isOneTime = true)
        val item = repository.getItem(itemId)
        assertNotNull(item)
        assertTrue(item!!.isOneTime)

        // Start over must return null and not create lap or modify item
        val lap = repository.startOver(itemId, 5000L)
        assertEquals(null, lap)

        val itemAfter = repository.getItem(itemId)
        assertEquals(1000L, itemAfter!!.startEpochMillis)
        assertEquals(0, itemAfter.lifetimeRestarts)
    }

    @Test
    fun testOneTimeEventExclusionFromStats() {
        val item1 = ItemEntity(id = 1, title = "Standard", startEpochMillis = 1000L, isOneTime = false)
        val item2 = ItemEntity(id = 2, title = "One Time Event", startEpochMillis = 2000L, isOneTime = true)

        val allItems = listOf(item1, item2)
        val statsItems = allItems.filter { !it.isOneTime }

        assertEquals(1, statsItems.size)
        assertEquals("Standard", statsItems[0].title)
    }
}
