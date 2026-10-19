package com.birtucansoftware.birtusince.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.birtucansoftware.birtusince.BirtuApp
import com.birtucansoftware.birtusince.data.ItemEntity
import com.birtucansoftware.birtusince.data.LapEntity
import com.birtucansoftware.birtusince.notifications.AlarmScheduler
import com.birtucansoftware.birtusince.shortcuts.DynamicShortcutsManager
import com.birtucansoftware.birtusince.domain.FilterSelection
import com.birtucansoftware.birtusince.ui.theme.ThemePreset
import com.birtucansoftware.birtusince.widget.BirtuAppWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as BirtuApp).repository
    private val preferences = (application as BirtuApp).preferences

    private val _isLoadingActiveItems = MutableStateFlow(true)
    val isLoadingActiveItems: StateFlow<Boolean> = _isLoadingActiveItems.asStateFlow()

    val activeItems: StateFlow<List<ItemEntity>> = repository.observeActiveItems()
        .onEach { _isLoadingActiveItems.value = false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedItems: StateFlow<List<ItemEntity>> = repository.observeArchivedItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val themePreset: StateFlow<ThemePreset> = preferences.themePresetFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemePreset.Tangelo)

    val isDarkMode: StateFlow<Boolean> = preferences.isDarkModeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val filterSelection: StateFlow<FilterSelection> = preferences.filterSelectionFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, FilterSelection())

    val selectedFilter: StateFlow<String> = preferences.selectedFilterFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "All")

    val sortOption: StateFlow<com.birtucansoftware.birtusince.domain.SortOption> = preferences.sortOptionFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, com.birtucansoftware.birtusince.domain.SortOption.CREATED_AT)

    val isSortAscending: StateFlow<Boolean> = preferences.isSortAscendingFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val appLockEnabled: StateFlow<Boolean> = preferences.appLockEnabledFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val draftTitle: StateFlow<String> = preferences.draftTitleFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    // Set of item IDs unlocked during this session
    private val _unlockedItemIds = MutableStateFlow<Set<Long>>(emptySet())
    val unlockedItemIds: StateFlow<Set<Long>> = _unlockedItemIds.asStateFlow()

    // App-level lock state for current session
    private val _isAppUnlocked = MutableStateFlow(false)
    val isAppUnlocked: StateFlow<Boolean> = _isAppUnlocked.asStateFlow()

    // Snackbar state: message, actionLabel, action callback
    private val _snackbarEvent = MutableStateFlow<SnackbarEvent?>(null)
    val snackbarEvent: StateFlow<SnackbarEvent?> = _snackbarEvent.asStateFlow()

    data class SnackbarEvent(
        val message: String,
        val actionLabel: String? = null,
        val onAction: (() -> Unit)? = null
    )

    fun unlockApp() {
        _isAppUnlocked.value = true
    }

    fun lockApp() {
        _isAppUnlocked.value = false
        _unlockedItemIds.value = emptySet()
    }

    fun unlockItem(itemId: Long) {
        _unlockedItemIds.value = _unlockedItemIds.value + itemId
    }

    fun lockItem(itemId: Long) {
        _unlockedItemIds.value = _unlockedItemIds.value - itemId
    }

    fun setThemePreset(preset: ThemePreset) {
        viewModelScope.launch {
            preferences.setThemePreset(preset)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setDarkMode(enabled)
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            preferences.toggleDarkMode()
        }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setAppLockEnabled(enabled)
            if (enabled) {
                _isAppUnlocked.value = false
            }
        }
    }

    fun setDraftTitle(draft: String) {
        viewModelScope.launch {
            preferences.setDraftTitle(draft)
        }
    }

    fun setFilterSelection(selection: FilterSelection) {
        viewModelScope.launch {
            preferences.setFilterSelection(selection)
        }
    }

    fun setSelectedFilter(filter: String) {
        viewModelScope.launch {
            preferences.setSelectedFilter(filter)
        }
    }

    fun setSortOption(option: com.birtucansoftware.birtusince.domain.SortOption) {
        viewModelScope.launch {
            preferences.setSortOption(option)
        }
    }

    fun setSortAscending(ascending: Boolean) {
        viewModelScope.launch {
            preferences.setSortAscending(ascending)
        }
    }

    fun toggleSortAscending() {
        viewModelScope.launch {
            preferences.toggleSortAscending()
        }
    }

    fun createItem(
        title: String,
        startEpochMillis: Long = System.currentTimeMillis(),
        category: String = "",
        categoryIcon: String = "Timer",
        notes: String = "",
        targetIntervalMillis: Long? = null,
        isPrivate: Boolean = false,
        isOneTime: Boolean = false
    ) {
        viewModelScope.launch {
            val id = repository.createItem(
                title = title,
                startEpochMillis = startEpochMillis,
                category = category,
                categoryIcon = categoryIcon,
                notes = notes,
                targetIntervalMillis = targetIntervalMillis,
                isPrivate = isPrivate,
                isOneTime = isOneTime
            )
            setDraftTitle("") // Clear draft

            val item = repository.getItem(id)
            if (item != null) {
                AlarmScheduler.scheduleItemReminder(getApplication(), item)
            }

            refreshWidgetsAndShortcuts()
        }
    }

    fun startOver(itemId: Long, pastEpochMillis: Long? = null) {
        viewModelScope.launch {
            val effectiveTime = pastEpochMillis ?: System.currentTimeMillis()
            val lap = repository.startOver(itemId, effectiveTime)

            val item = repository.getItem(itemId)
            if (item != null) {
                AlarmScheduler.scheduleItemReminder(getApplication(), item)
            }

            refreshWidgetsAndShortcuts()

            if (lap != null) {
                _snackbarEvent.value = SnackbarEvent(
                    message = "Started over.",
                    actionLabel = "Undo",
                    onAction = {
                        viewModelScope.launch {
                            repository.undoStartOver(itemId, lap.id)
                            refreshWidgetsAndShortcuts()
                        }
                    }
                )
            }
        }
    }

    fun pauseItem(itemId: Long) {
        viewModelScope.launch {
            repository.pauseItem(itemId)
            AlarmScheduler.cancelItemReminder(getApplication(), itemId)
            refreshWidgetsAndShortcuts()
        }
    }

    fun resumeItem(itemId: Long) {
        viewModelScope.launch {
            repository.resumeItem(itemId)
            val item = repository.getItem(itemId)
            if (item != null) {
                AlarmScheduler.scheduleItemReminder(getApplication(), item)
            }
            refreshWidgetsAndShortcuts()
        }
    }

    fun putAwayItem(itemId: Long) {
        viewModelScope.launch {
            repository.putAwayItem(itemId)
            AlarmScheduler.cancelItemReminder(getApplication(), itemId)
            refreshWidgetsAndShortcuts()

            _snackbarEvent.value = SnackbarEvent(
                message = "Put away.",
                actionLabel = "Undo",
                onAction = {
                    viewModelScope.launch {
                        repository.bringBackItem(itemId)
                        refreshWidgetsAndShortcuts()
                    }
                }
            )
        }
    }

    fun bringBackItem(itemId: Long) {
        viewModelScope.launch {
            repository.bringBackItem(itemId)
            val item = repository.getItem(itemId)
            if (item != null) {
                AlarmScheduler.scheduleItemReminder(getApplication(), item)
            }
            refreshWidgetsAndShortcuts()
        }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            AlarmScheduler.cancelItemReminder(getApplication(), itemId)
            repository.deleteItem(itemId)
            refreshWidgetsAndShortcuts()
        }
    }

    fun updateItem(item: ItemEntity) {
        viewModelScope.launch {
            repository.updateItem(item)
            if (item.targetIntervalMillis != null && !item.isPaused && !item.isArchived) {
                AlarmScheduler.scheduleItemReminder(getApplication(), item)
            } else {
                AlarmScheduler.cancelItemReminder(getApplication(), item.id)
            }
            refreshWidgetsAndShortcuts()
        }
    }

    fun togglePinItem(itemId: Long) {
        viewModelScope.launch {
            repository.togglePinItem(itemId)
            val item = repository.getItem(itemId)
            val isPinned = item?.isPinned == true
            _snackbarEvent.value = SnackbarEvent(
                message = if (isPinned) "Pinned to top" else "Unpinned"
            )
        }
    }

    fun updateLap(lap: LapEntity) {
        viewModelScope.launch {
            repository.updateLap(lap)
        }
    }

    fun deleteLap(lapId: Long) {
        viewModelScope.launch {
            repository.deleteLap(lapId)
        }
    }

    fun observeLaps(itemId: Long) = repository.observeLaps(itemId)

    fun decryptTitle(item: ItemEntity) = repository.decryptTitle(item)

    fun decryptNotes(item: ItemEntity) = repository.decryptNotes(item)

    suspend fun exportBackupJson(): String = repository.exportBackupJson()

    suspend fun restoreBackupJson(jsonString: String, replaceAll: Boolean): Boolean {
        val success = repository.restoreBackupJson(jsonString, replaceAll)
        if (success) {
            refreshWidgetsAndShortcuts()
        }
        return success
    }

    fun clearSnackbar() {
        _snackbarEvent.value = null
    }

    fun showSnackbar(message: String, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
        _snackbarEvent.value = SnackbarEvent(message, actionLabel, onAction)
    }

    private fun refreshWidgetsAndShortcuts() {
        viewModelScope.launch {
            BirtuAppWidgetProvider.updateAllWidgets(getApplication())
            val topItems = repository.getTopRestartedItems(3)
            DynamicShortcutsManager.updateShortcuts(getApplication(), topItems)
        }
    }
}
