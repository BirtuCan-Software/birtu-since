package com.birtucansoftware.birtusince.data

import android.content.Context
import com.birtucansoftware.birtusince.domain.TimeEngine
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class BirtuRepository(
    private val itemDao: ItemDao,
    val preferences: PreferencesManager
) {

    fun observeActiveItems(): Flow<List<ItemEntity>> = itemDao.observeActiveItems()

    fun observeArchivedItems(): Flow<List<ItemEntity>> = itemDao.observeArchivedItems()

    fun observeItem(id: Long): Flow<ItemEntity?> = itemDao.observeItemById(id)

    suspend fun getItem(id: Long): ItemEntity? = itemDao.getItemById(id)

    fun observeLaps(itemId: Long): Flow<List<LapEntity>> = itemDao.observeLapsForItem(itemId)

    suspend fun getLapsAsc(itemId: Long): List<LapEntity> = itemDao.getLapsForItemAsc(itemId)

    suspend fun createItem(
        title: String,
        startEpochMillis: Long = System.currentTimeMillis(),
        category: String = "",
        categoryIcon: String = "Timer",
        notes: String = "",
        targetIntervalMillis: Long? = null,
        isPrivate: Boolean = false,
        isOneTime: Boolean = false
    ): Long {
        val (encTitle, encNotes) = if (isPrivate) {
            KeystoreCrypto.encrypt(title) to KeystoreCrypto.encrypt(notes)
        } else {
            null to null
        }

        val item = ItemEntity(
            title = title,
            startEpochMillis = startEpochMillis,
            category = category,
            categoryIcon = categoryIcon,
            notes = notes,
            targetIntervalMillis = targetIntervalMillis,
            isPrivate = isPrivate,
            isPaused = false,
            pauseTimestamp = null,
            isArchived = false,
            lifetimeRestarts = 0,
            preferredUnit = TimeEngine.UNIT_AUTO,
            encryptedTitle = encTitle,
            encryptedNotes = encNotes,
            createdAtEpochMillis = System.currentTimeMillis(),
            isPinned = false,
            lastModifiedEpochMillis = System.currentTimeMillis(),
            isOneTime = isOneTime
        )
        return itemDao.insertItem(item)
    }

    suspend fun updateItem(item: ItemEntity) {
        val realTitle = if (item.title == "Locked Item" && !item.encryptedTitle.isNullOrBlank()) {
            val decrypted = KeystoreCrypto.decrypt(item.encryptedTitle)
            if (decrypted.isNotBlank() && decrypted != item.encryptedTitle) decrypted else item.title
        } else {
            item.title
        }

        val realNotes = if (item.notes.isBlank() && !item.encryptedNotes.isNullOrBlank()) {
            val decrypted = KeystoreCrypto.decrypt(item.encryptedNotes)
            if (decrypted != item.encryptedNotes) decrypted else item.notes
        } else {
            item.notes
        }

        val (encTitle, encNotes) = if (item.isPrivate) {
            KeystoreCrypto.encrypt(realTitle) to KeystoreCrypto.encrypt(realNotes)
        } else {
            null to null
        }

        val processedItem = item.copy(
            title = realTitle,
            notes = realNotes,
            encryptedTitle = encTitle,
            encryptedNotes = encNotes,
            lastModifiedEpochMillis = System.currentTimeMillis()
        )
        itemDao.updateItem(processedItem)
    }

    suspend fun togglePinItem(itemId: Long) {
        val item = itemDao.getItemById(itemId) ?: return
        val updated = item.copy(
            isPinned = !item.isPinned,
            lastModifiedEpochMillis = System.currentTimeMillis()
        )
        itemDao.updateItem(updated)
    }

    suspend fun setItemPinned(itemId: Long, isPinned: Boolean) {
        val item = itemDao.getItemById(itemId) ?: return
        val updated = item.copy(
            isPinned = isPinned,
            lastModifiedEpochMillis = System.currentTimeMillis()
        )
        itemDao.updateItem(updated)
    }

    /**
     * Executes Interval Reset Action (FR-CAD-001 & FR-CAD-002).
     * @param itemId item to reset
     * @param resetAtEpochMillis retroactive reset point or now()
     */
    suspend fun startOver(itemId: Long, resetAtEpochMillis: Long = System.currentTimeMillis()): LapEntity? {
        val item = itemDao.getItemById(itemId) ?: return null
        if (item.isOneTime) return null

        val previousStart = item.startEpochMillis
        val effectiveEnd = if (resetAtEpochMillis > previousStart) resetAtEpochMillis else System.currentTimeMillis()
        val duration = (effectiveEnd - previousStart).coerceAtLeast(0L)

        val runNumber = item.lifetimeRestarts + 1
        val lap = LapEntity(
            itemId = itemId,
            runNumber = runNumber,
            startEpochMillis = previousStart,
            endEpochMillis = effectiveEnd,
            durationMillis = duration,
            note = ""
        )
        val generatedId = itemDao.insertLap(lap)

        val updatedItem = item.copy(
            startEpochMillis = effectiveEnd,
            isPaused = false,
            pauseTimestamp = null,
            lifetimeRestarts = runNumber,
            lastModifiedEpochMillis = System.currentTimeMillis()
        )
        itemDao.updateItem(updatedItem)
        return lap.copy(id = generatedId)
    }

    /**
     * Undoes the last start over action (reverts item start time and removes lap).
     */
    suspend fun undoStartOver(itemId: Long, lapId: Long) {
        val lap = itemDao.getLapById(lapId) ?: return
        val item = itemDao.getItemById(itemId) ?: return

        itemDao.deleteLapById(lapId)
        val revertedItem = item.copy(
            startEpochMillis = lap.startEpochMillis,
            lifetimeRestarts = (item.lifetimeRestarts - 1).coerceAtLeast(0),
            lastModifiedEpochMillis = System.currentTimeMillis()
        )
        itemDao.updateItem(revertedItem)
    }

    suspend fun pauseItem(itemId: Long) {
        val item = itemDao.getItemById(itemId) ?: return
        if (item.isPaused) return
        val now = System.currentTimeMillis()
        itemDao.updateItem(item.copy(isPaused = true, pauseTimestamp = now, lastModifiedEpochMillis = now))
    }

    suspend fun resumeItem(itemId: Long) {
        val item = itemDao.getItemById(itemId) ?: return
        if (!item.isPaused || item.pauseTimestamp == null) return
        val now = System.currentTimeMillis()
        val pausedDuration = (now - item.pauseTimestamp).coerceAtLeast(0L)
        val newStart = item.startEpochMillis + pausedDuration
        itemDao.updateItem(item.copy(isPaused = false, pauseTimestamp = null, startEpochMillis = newStart, lastModifiedEpochMillis = now))
    }

    suspend fun putAwayItem(itemId: Long) {
        val item = itemDao.getItemById(itemId) ?: return
        itemDao.updateItem(item.copy(isArchived = true, lastModifiedEpochMillis = System.currentTimeMillis()))
    }

    suspend fun bringBackItem(itemId: Long) {
        val item = itemDao.getItemById(itemId) ?: return
        itemDao.updateItem(item.copy(isArchived = false, lastModifiedEpochMillis = System.currentTimeMillis()))
    }

    suspend fun deleteItem(itemId: Long) {
        itemDao.deleteItemById(itemId)
    }

    suspend fun updateLap(lap: LapEntity) {
        itemDao.updateLap(lap)
    }

    suspend fun deleteLap(lapId: Long) {
        val lap = itemDao.getLapById(lapId)
        if (lap != null) {
            val item = itemDao.getItemById(lap.itemId)
            if (item != null) {
                val newRestarts = (item.lifetimeRestarts - 1).coerceAtLeast(0)
                itemDao.updateItem(item.copy(lifetimeRestarts = newRestarts))
            }
            itemDao.deleteLapById(lapId)
        }
    }

    suspend fun getTopRestartedItems(limit: Int = 3): List<ItemEntity> {
        return itemDao.getTopRestartedItems(limit)
    }

    suspend fun getAllActiveItems(): List<ItemEntity> {
        return itemDao.getAllActiveItems()
    }

    // Decrypt helper for private item
    fun decryptTitle(item: ItemEntity): String {
        if (item.title != "Locked Item" && item.title.isNotBlank()) {
            return item.title
        }
        if (!item.encryptedTitle.isNullOrBlank()) {
            val decrypted = KeystoreCrypto.decrypt(item.encryptedTitle)
            if (decrypted.isNotBlank() && decrypted != item.encryptedTitle) {
                return decrypted
            }
        }
        return item.title
    }

    fun decryptNotes(item: ItemEntity): String {
        if (item.notes.isNotBlank()) {
            return item.notes
        }
        if (!item.encryptedNotes.isNullOrBlank()) {
            val decrypted = KeystoreCrypto.decrypt(item.encryptedNotes)
            if (decrypted != item.encryptedNotes) {
                return decrypted
            }
        }
        return item.notes
    }

    // Atomic Backup Export (FR-DAT-003)
    suspend fun exportBackupJson(): String {
        val items = itemDao.getAllItems()
        val laps = itemDao.getAllLaps()
        return BackupSerializer.serialize(items, laps)
    }

    // Atomic Backup Restore (FR-DAT-004)
    suspend fun restoreBackupJson(jsonString: String, replaceAll: Boolean): Boolean {
        val parsedBackup = BackupSerializer.deserialize(jsonString) ?: return false

        return try {
            if (replaceAll) {
                itemDao.clearAllLaps()
                itemDao.clearAllItems()
            }

            val idMap = mutableMapOf<Long, Long>()

            for (rawItem in parsedBackup.items) {
                val newId = itemDao.insertItem(rawItem.entity)
                idMap[rawItem.oldId] = newId
            }

            for (rawLap in parsedBackup.laps) {
                val newItemId = idMap[rawLap.oldItemId] ?: continue
                val updatedLap = rawLap.entity.copy(itemId = newItemId)
                itemDao.insertLap(updatedLap)
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("BirtuRepository", "Error saving restored backup to database", e)
            false
        }
    }
}
