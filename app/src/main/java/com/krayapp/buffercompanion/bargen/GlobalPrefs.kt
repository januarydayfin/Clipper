package com.krayapp.buffercompanion.bargen

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.krayapp.buffercompanion.bargen.domain.type.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private const val PREFS_NAME = "mainSettings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFS_NAME,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, PREFS_NAME))
    }
)

class GlobalPrefs(private val context: Context) {

    private object Keys {
        val SORT_TYPE = stringPreferencesKey("SORT_TYPE")
        val KEY_THEME_MODE = intPreferencesKey("KEY_THEME_MODE")
        val OPEN_AFTER_SCAN = booleanPreferencesKey("OPEN_AFTER_SCAN")
        val VOLUME_BUTTON_SCAN = booleanPreferencesKey("VOLUME_BUTTON_SCAN ")
        val MAX_BRIGHT_ON_CODE = booleanPreferencesKey("MAX_BRIGHT_ON_CODE")
        val SWIPE_TO_DELETE = booleanPreferencesKey("SWIPE_TO_DELETE")
        val OPEN_LANDSCAPE_ON_BS_OPEN = booleanPreferencesKey("OPEN_LANDSCAPE_ON_BS_OPEN")
        val HIDE_BS_AFTER_LANDSCAPE_CLOSE = booleanPreferencesKey("HIDE_BS_AFTER_LANDSCAPE_CLOSE")
    }

    val sortTypeFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.SORT_TYPE] ?: SortType.DATE_DESC.toString()
        }

    var sortType: String
        get() = runBlocking { sortTypeFlow.first() }
        set(value) {
            runBlocking {
                context.dataStore.edit { preferences ->
                    preferences[Keys.SORT_TYPE] = value
                }
            }
        }

    val themeFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.KEY_THEME_MODE] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

    var theme: Int
        get() = runBlocking { themeFlow.first() }
        set(value) {
            runBlocking {
                context.dataStore.edit { preferences ->
                    preferences[Keys.KEY_THEME_MODE] = value
                }
            }
        }

    val openCardAfterScanFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.OPEN_AFTER_SCAN] ?: true
        }

    var openCardAfterScan: Boolean
        get() = runBlocking { openCardAfterScanFlow.first() }
        set(value) {
            runBlocking {
                context.dataStore.edit { preferences ->
                    preferences[Keys.OPEN_AFTER_SCAN] = value
                }
            }
        }

    val scanOnVolumeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.VOLUME_BUTTON_SCAN] ?: true
        }

    var scanOnVolume: Boolean
        get() = runBlocking { scanOnVolumeFlow.first() }
        set(value) {
            runBlocking {
                context.dataStore.edit { preferences ->
                    preferences[Keys.VOLUME_BUTTON_SCAN] = value
                }
            }
        }

    val maxBrightOnCodeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.MAX_BRIGHT_ON_CODE] ?: false
        }

    var maxBrightOnCode: Boolean
        get() = runBlocking { maxBrightOnCodeFlow.first() }
        set(value) {
            runBlocking {
                context.dataStore.edit { preferences ->
                    preferences[Keys.MAX_BRIGHT_ON_CODE] = value
                }
            }
        }

    val swipeToDeleteFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.SWIPE_TO_DELETE] ?: true
        }

    var swipeToDelete: Boolean
        get() = runBlocking { swipeToDeleteFlow.first() }
        set(value) {
            runBlocking {
                context.dataStore.edit { preferences ->
                    preferences[Keys.SWIPE_TO_DELETE] = value
                }
            }
        }

    val openLandscapeOnBsOpenFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.OPEN_LANDSCAPE_ON_BS_OPEN] ?: false
        }

    var openLandscapeOnBsOpen: Boolean
        get() = runBlocking { openLandscapeOnBsOpenFlow.first() }
        set(value) {
            runBlocking {
                context.dataStore.edit { preferences ->
                    preferences[Keys.OPEN_LANDSCAPE_ON_BS_OPEN] = value
                }
            }
        }

    val hideBsAfterLandscapeCloseFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.HIDE_BS_AFTER_LANDSCAPE_CLOSE] ?: false
        }

    var hideBsAfterLandscapeClose: Boolean
        get() = runBlocking { hideBsAfterLandscapeCloseFlow.first() }
        set(value) {
            runBlocking {
                context.dataStore.edit { preferences ->
                    preferences[Keys.HIDE_BS_AFTER_LANDSCAPE_CLOSE] = value
                }
            }
        }
}
