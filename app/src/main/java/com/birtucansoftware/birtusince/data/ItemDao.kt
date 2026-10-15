package com.birtucansoftware.birtusince.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM items WHERE isArchived = 0 ORDER BY id DESC")
    fun observeActiveItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE isArchived = 1 ORDER BY id DESC")
    fun observeArchivedItems(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id")
    fun observeItemById(id: Long): Flow<ItemEntity?>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Long): ItemEntity?

    @Query("SELECT * FROM items WHERE isArchived = 0 ORDER BY lifetimeRestarts DESC LIMIT :limit")
    suspend fun getTopRestartedItems(limit: Int): List<ItemEntity>

    @Query("SELECT * FROM items WHERE isArchived = 0")
    suspend fun getAllActiveItems(): List<ItemEntity>

    @Query("SELECT * FROM items")
    suspend fun getAllItems(): List<ItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity): Long

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    // Laps (Past runs)
    @Query("SELECT * FROM laps WHERE itemId = :itemId ORDER BY runNumber DESC")
    fun observeLapsForItem(itemId: Long): Flow<List<LapEntity>>

    @Query("SELECT * FROM laps WHERE itemId = :itemId ORDER BY runNumber ASC")
    suspend fun getLapsForItemAsc(itemId: Long): List<LapEntity>

    @Query("SELECT * FROM laps WHERE id = :id")
    suspend fun getLapById(id: Long): LapEntity?

    @Query("SELECT * FROM laps")
    suspend fun getAllLaps(): List<LapEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLap(lap: LapEntity): Long

    @Update
    suspend fun updateLap(lap: LapEntity)

    @Delete
    suspend fun deleteLap(lap: LapEntity)

    @Query("DELETE FROM laps WHERE id = :id")
    suspend fun deleteLapById(id: Long)

    // Bulk / Backup transactions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllItems(items: List<ItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLaps(laps: List<LapEntity>)

    @Query("DELETE FROM items")
    suspend fun clearAllItems()

    @Query("DELETE FROM laps")
    suspend fun clearAllLaps()

    @Transaction
    suspend fun replaceAll(items: List<ItemEntity>, laps: List<LapEntity>) {
        clearAllLaps()
        clearAllItems()
        insertAllItems(items)
        insertAllLaps(laps)
    }
}
