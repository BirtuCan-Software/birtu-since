package com.birtucansoftware.birtusince.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.birtucansoftware.birtusince.domain.FilterSelection
import com.birtucansoftware.birtusince.ui.theme.ThemePreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "birtusince_prefs")

class PreferencesManager(private val context: Context) {

    private val themeKey = stringPreferencesKey("theme_preset")
    private val darkModeKey = booleanPreferencesKey("dark_mode_enabled")
    private val appLockKey = booleanPreferencesKey("app_lock_enabled")
    private val draftTitleKey = stringPreferencesKey("quick_add_draft_title")
    private val filterChipKey = stringPreferencesKey("selected_filter_chip")
    private val includedCategoriesKey = stringSetPreferencesKey("filter_included_categories")
    private val excludedCategoriesKey = stringSetPreferencesKey("filter_excluded_categories")
    private val isLateIncludedKey = booleanPreferencesKey("filter_is_late_included")
    private val isLateExcludedKey = booleanPreferencesKey("filter_is_late_excluded")
    private val sortOptionKey = stringPreferencesKey("sort_option")
    private val sortAscendingKey = booleanPreferencesKey("sort_ascending")

    val themePresetFlow: Flow<ThemePreset> = context.dataStore.data.map { prefs ->
        when (prefs[themeKey]) {
            ThemePreset.Arctic.name -> ThemePreset.Arctic
            ThemePreset.Obsidian.name -> ThemePreset.Obsidian
            ThemePreset.Amber.name -> ThemePreset.Amber
            ThemePreset.Emerald.name -> ThemePreset.Emerald
            ThemePreset.Bubblegum.name -> ThemePreset.Bubblegum
            else -> ThemePreset.Tangelo
        }
    }

    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[darkModeKey] ?: false
    }

    val appLockEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[appLockKey] ?: false
    }

    val draftTitleFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[draftTitleKey] ?: ""
    }

    val filterSelectionFlow: Flow<FilterSelection> = context.dataStore.data.map { prefs ->
        val hasIncluded = prefs.contains(includedCategoriesKey)
        val hasExcluded = prefs.contains(excludedCategoriesKey)
        val hasLateInc = prefs.contains(isLateIncludedKey)
        val hasLateExc = prefs.contains(isLateExcludedKey)

        if (hasIncluded || hasExcluded || hasLateInc || hasLateExc) {
            FilterSelection(
                includedCategories = prefs[includedCategoriesKey] ?: emptySet(),
                excludedCategories = prefs[excludedCategoriesKey] ?: emptySet(),
                isLateIncluded = prefs[isLateIncludedKey] ?: false,
                isLateExcluded = prefs[isLateExcludedKey] ?: false
            )
        } else {
            FilterSelection.fromLegacyString(prefs[filterChipKey])
        }
    }

    val selectedFilterFlow: Flow<String> = filterSelectionFlow.map { selection ->
        when {
            selection.isEmpty -> "All"
            selection.isLateIncluded -> "Late"
            selection.includedCategories.size == 1 && selection.excludedCategories.isEmpty() -> selection.includedCategories.first()
            else -> "Custom"
        }
    }

    val sortOptionFlow: Flow<com.birtucansoftware.birtusince.domain.SortOption> = context.dataStore.data.map { prefs ->
        com.birtucansoftware.birtusince.domain.SortOption.fromName(prefs[sortOptionKey])
    }

    val isSortAscendingFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[sortAscendingKey] ?: false
    }

    suspend fun setThemePreset(preset: ThemePreset) {
        context.dataStore.edit { prefs ->
            prefs[themeKey] = preset.name
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[darkModeKey] = enabled
        }
    }

    suspend fun toggleDarkMode() {
        context.dataStore.edit { prefs ->
            val current = prefs[darkModeKey] ?: false
            prefs[darkModeKey] = !current
        }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[appLockKey] = enabled
        }
    }

    suspend fun setDraftTitle(draft: String) {
        context.dataStore.edit { prefs ->
            prefs[draftTitleKey] = draft
        }
    }

    suspend fun setFilterSelection(selection: FilterSelection) {
        context.dataStore.edit { prefs ->
            prefs[includedCategoriesKey] = selection.includedCategories
            prefs[excludedCategoriesKey] = selection.excludedCategories
            prefs[isLateIncludedKey] = selection.isLateIncluded
            prefs[isLateExcludedKey] = selection.isLateExcluded
            prefs[filterChipKey] = when {
                selection.isEmpty -> "All"
                selection.isLateIncluded -> "Late"
                selection.includedCategories.size == 1 && selection.excludedCategories.isEmpty() -> selection.includedCategories.first()
                else -> "Custom"
            }
        }
    }

    suspend fun setSelectedFilter(filter: String) {
        setFilterSelection(FilterSelection.fromLegacyString(filter))
    }

    suspend fun setSortOption(option: com.birtucansoftware.birtusince.domain.SortOption) {
        context.dataStore.edit { prefs ->
            prefs[sortOptionKey] = option.name
        }
    }

    suspend fun setSortAscending(ascending: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[sortAscendingKey] = ascending
        }
    }

    suspend fun toggleSortAscending() {
        context.dataStore.edit { prefs ->
            val current = prefs[sortAscendingKey] ?: false
            prefs[sortAscendingKey] = !current
        }
    }
}
