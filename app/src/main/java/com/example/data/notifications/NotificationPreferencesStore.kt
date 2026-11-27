package com.example.data.notifications

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.notificationsDataStore by preferencesDataStore(name = "saydaliyati_notifications_prefs")

interface NotificationPreferencesReader {
    val readIdsFlow: Flow<Set<String>>
    suspend fun markRead(notificationId: String)
    suspend fun markAllRead(notificationIds: Collection<String>)
}

class NotificationPreferencesStore(
    private val context: Context
) : NotificationPreferencesReader {

    private val readIdsKey = stringSetPreferencesKey("read_notification_ids")

    override val readIdsFlow: Flow<Set<String>> = context.notificationsDataStore.data.map { prefs ->
        prefs[readIdsKey] ?: emptySet()
    }

    override suspend fun markRead(notificationId: String) {
        if (notificationId.isBlank()) return
        context.notificationsDataStore.edit { prefs ->
            val current = prefs[readIdsKey] ?: emptySet()
            prefs[readIdsKey] = current + notificationId
        }
    }

    override suspend fun markAllRead(notificationIds: Collection<String>) {
        if (notificationIds.isEmpty()) return
        context.notificationsDataStore.edit { prefs ->
            val current = prefs[readIdsKey] ?: emptySet()
            prefs[readIdsKey] = current + notificationIds
        }
    }
}
