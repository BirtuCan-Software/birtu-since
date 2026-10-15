package com.birtucansoftware.birtusince.domain

sealed interface ItemState {
    data object Running : ItemState
    data object Paused : ItemState
    data class Late(val lateByMillis: Long) : ItemState
    data object Locked : ItemState
    data object ClockLooksOff : ItemState
    data object PutAway : ItemState
}

data class ItemDerivedStatus(
    val state: ItemState,
    val elapsedMillis: Long,
    val progressFraction: Float, // 0.0f..1.0f (or > 1.0f if late)
    val formattedDuration: TimeEngine.FormattedDuration
)

object ItemStateResolver {
    fun resolve(
        startEpochMillis: Long,
        isPaused: Boolean,
        pauseEpochMillis: Long?,
        isArchived: Boolean,
        isPrivate: Boolean,
        isUnlockedThisSession: Boolean,
        targetIntervalMillis: Long?,
        unit: String = TimeEngine.UNIT_AUTO,
        nowEpochMillis: Long = System.currentTimeMillis()
    ): ItemDerivedStatus {
        if (isArchived) {
            val (elapsed, isClockOff) = TimeEngine.computeElapsedMillis(
                startEpochMillis = startEpochMillis,
                nowEpochMillis = nowEpochMillis,
                isPaused = true,
                pauseEpochMillis = pauseEpochMillis ?: startEpochMillis
            )
            return ItemDerivedStatus(
                state = ItemState.PutAway,
                elapsedMillis = elapsed,
                progressFraction = 0f,
                formattedDuration = TimeEngine.formatDuration(elapsed, unit, isClockOff)
            )
        }

        if (isPrivate && !isUnlockedThisSession) {
            return ItemDerivedStatus(
                state = ItemState.Locked,
                elapsedMillis = 0L,
                progressFraction = 0f,
                formattedDuration = TimeEngine.FormattedDuration(
                    primaryText = "Locked",
                    spokenWords = "Locked item"
                )
            )
        }

        val (elapsed, isClockOff) = TimeEngine.computeElapsedMillis(
            startEpochMillis = startEpochMillis,
            nowEpochMillis = nowEpochMillis,
            isPaused = isPaused,
            pauseEpochMillis = pauseEpochMillis
        )

        if (isClockOff) {
            return ItemDerivedStatus(
                state = ItemState.ClockLooksOff,
                elapsedMillis = 0L,
                progressFraction = 0f,
                formattedDuration = TimeEngine.formatDuration(0L, unit, isClockAnomaly = true)
            )
        }

        val fraction = if (targetIntervalMillis != null && targetIntervalMillis > 0) {
            (elapsed.toFloat() / targetIntervalMillis.toFloat()).coerceAtLeast(0f)
        } else {
            0f
        }

        val state: ItemState = when {
            isPaused -> ItemState.Paused
            targetIntervalMillis != null && elapsed >= targetIntervalMillis -> {
                ItemState.Late(lateByMillis = elapsed - targetIntervalMillis)
            }
            else -> ItemState.Running
        }

        return ItemDerivedStatus(
            state = state,
            elapsedMillis = elapsed,
            progressFraction = fraction,
            formattedDuration = TimeEngine.formatDuration(elapsed, unit, false)
        )
    }
}
