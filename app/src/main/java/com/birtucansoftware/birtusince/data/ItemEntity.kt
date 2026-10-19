package com.birtucansoftware.birtusince.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Item Entity representing a tracked elapsed-time record.
 * All timestamps stored as UTC epoch milliseconds.
 */
@Entity(
    tableName = "items",
    indices = [
        Index(value = ["isArchived"]),
        Index(value = ["startEpochMillis"])
    ]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val startEpochMillis: Long,
    val category: String = "",
    val categoryIcon: String = "Timer",
    val notes: String = "",
    val targetIntervalMillis: Long? = null,
    val isPrivate: Boolean = false,
    val isPaused: Boolean = false,
    val pauseTimestamp: Long? = null,
    val isArchived: Boolean = false,
    val lifetimeRestarts: Int = 0,
    val preferredUnit: String = "auto",
    val encryptedTitle: String? = null,
    val encryptedNotes: String? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val lastModifiedEpochMillis: Long = System.currentTimeMillis(),
    val isOneTime: Boolean = false
)

/**
 * Lap Entity (Past run) - immutable record of prior runs.
 */
@Entity(
    tableName = "laps",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["itemId"]),
        Index(value = ["startEpochMillis"])
    ]
)
data class LapEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: Long,
    val runNumber: Int,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val durationMillis: Long,
    val note: String = ""
)
