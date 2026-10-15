package com.birtucansoftware.birtusince.data

import org.json.JSONArray
import org.json.JSONObject

/**
 * Handles JSON serialization and parsing for backup and restore operations.
 * Single Responsibility: Backup data format transformation.
 */
object BackupSerializer {

    data class RawItem(
        val oldId: Long,
        val entity: ItemEntity
    )

    data class RawLap(
        val oldItemId: Long,
        val entity: LapEntity
    )

    data class ParsedBackup(
        val items: List<RawItem>,
        val laps: List<RawLap>
    )

    fun serialize(items: List<ItemEntity>, laps: List<LapEntity>): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())

        val itemsArray = JSONArray()
        for (item in items) {
            val itemObj = JSONObject()
            itemObj.put("id", item.id)
            itemObj.put("title", item.title)
            itemObj.put("startEpochMillis", item.startEpochMillis)
            itemObj.put("category", item.category)
            itemObj.put("categoryIcon", item.categoryIcon)
            itemObj.put("notes", item.notes)
            if (item.targetIntervalMillis != null) {
                itemObj.put("targetIntervalMillis", item.targetIntervalMillis)
            }
            itemObj.put("isPrivate", item.isPrivate)
            itemObj.put("isPaused", item.isPaused)
            if (item.pauseTimestamp != null) {
                itemObj.put("pauseTimestamp", item.pauseTimestamp)
            }
            itemObj.put("isArchived", item.isArchived)
            itemObj.put("lifetimeRestarts", item.lifetimeRestarts)
            itemObj.put("preferredUnit", item.preferredUnit)
            itemObj.put("encryptedTitle", item.encryptedTitle ?: "")
            itemObj.put("encryptedNotes", item.encryptedNotes ?: "")
            itemObj.put("createdAtEpochMillis", item.createdAtEpochMillis)
            itemsArray.put(itemObj)
        }
        root.put("items", itemsArray)

        val lapsArray = JSONArray()
        for (lap in laps) {
            val lapObj = JSONObject()
            lapObj.put("id", lap.id)
            lapObj.put("itemId", lap.itemId)
            lapObj.put("runNumber", lap.runNumber)
            lapObj.put("startEpochMillis", lap.startEpochMillis)
            lapObj.put("endEpochMillis", lap.endEpochMillis)
            lapObj.put("durationMillis", lap.durationMillis)
            lapObj.put("note", lap.note)
            lapsArray.put(lapObj)
        }
        root.put("laps", lapsArray)

        return root.toString(2)
    }

    fun deserialize(jsonString: String): ParsedBackup? {
        return try {
            val root = JSONObject(jsonString)
            val itemsArray = root.optJSONArray("items") ?: return null
            val lapsArray = root.optJSONArray("laps") ?: JSONArray()

            val items = mutableListOf<RawItem>()
            for (i in 0 until itemsArray.length()) {
                val obj = itemsArray.getJSONObject(i)
                val oldId = if (obj.has("id") && !obj.isNull("id")) obj.optLong("id", (i + 1).toLong()) else (i + 1).toLong()

                val isPrivate = obj.optBoolean("isPrivate", false)
                val rawTitle = obj.optString("title", "Untitled")
                val rawNotes = obj.optString("notes", "")
                val rawEncTitle = if (obj.has("encryptedTitle") && !obj.isNull("encryptedTitle")) obj.optString("encryptedTitle").takeIf { it.isNotEmpty() } else null
                val rawEncNotes = if (obj.has("encryptedNotes") && !obj.isNull("encryptedNotes")) obj.optString("encryptedNotes").takeIf { it.isNotEmpty() } else null

                val finalEncTitle = if (isPrivate) rawEncTitle ?: KeystoreCrypto.encrypt(rawTitle) else null
                val finalEncNotes = if (isPrivate) rawEncNotes ?: KeystoreCrypto.encrypt(rawNotes) else null
                val finalTitle = if (rawTitle == "Locked Item" && rawEncTitle != null) {
                    val dec = KeystoreCrypto.decrypt(rawEncTitle)
                    if (dec.isNotBlank() && dec != rawEncTitle) dec else rawTitle
                } else rawTitle
                val finalNotes = if (rawNotes.isEmpty() && rawEncNotes != null) {
                    val dec = KeystoreCrypto.decrypt(rawEncNotes)
                    if (dec != rawEncNotes) dec else rawNotes
                } else rawNotes

                val item = ItemEntity(
                    id = 0L,
                    title = finalTitle,
                    startEpochMillis = obj.optLong("startEpochMillis", System.currentTimeMillis()),
                    category = obj.optString("category", ""),
                    categoryIcon = obj.optString("categoryIcon", "Timer"),
                    notes = finalNotes,
                    targetIntervalMillis = if (obj.has("targetIntervalMillis") && !obj.isNull("targetIntervalMillis")) obj.optLong("targetIntervalMillis") else null,
                    isPrivate = isPrivate,
                    isPaused = obj.optBoolean("isPaused", false),
                    pauseTimestamp = if (obj.has("pauseTimestamp") && !obj.isNull("pauseTimestamp")) obj.optLong("pauseTimestamp") else null,
                    isArchived = obj.optBoolean("isArchived", false),
                    lifetimeRestarts = obj.optInt("lifetimeRestarts", 0),
                    preferredUnit = obj.optString("preferredUnit", "auto"),
                    encryptedTitle = finalEncTitle,
                    encryptedNotes = finalEncNotes,
                    createdAtEpochMillis = obj.optLong("createdAtEpochMillis", System.currentTimeMillis())
                )
                items.add(RawItem(oldId, item))
            }

            val laps = mutableListOf<RawLap>()
            for (i in 0 until lapsArray.length()) {
                val obj = lapsArray.getJSONObject(i)
                val oldItemId = if (obj.has("itemId") && !obj.isNull("itemId")) obj.optLong("itemId", -1L) else -1L

                val lap = LapEntity(
                    id = 0L,
                    itemId = 0L,
                    runNumber = obj.optInt("runNumber", 1),
                    startEpochMillis = obj.optLong("startEpochMillis", System.currentTimeMillis()),
                    endEpochMillis = obj.optLong("endEpochMillis", System.currentTimeMillis()),
                    durationMillis = obj.optLong("durationMillis", 0L),
                    note = obj.optString("note", "")
                )
                laps.add(RawLap(oldItemId, lap))
            }

            ParsedBackup(items, laps)
        } catch (e: Exception) {
            android.util.Log.e("BackupSerializer", "Error deserializing backup JSON", e)
            null
        }
    }
}
