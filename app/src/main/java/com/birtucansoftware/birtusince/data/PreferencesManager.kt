package com.birtucansoftware.birtusince.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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

    val selectedFilterFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[filterChipKey] ?: "All"
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

    suspend fun setSelectedFilter(filter: String) {
        context.dataStore.edit { prefs ->
            prefs[filterChipKey] = filter
        }
    }
}
