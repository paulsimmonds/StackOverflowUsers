package com.magictorch.stackoverflowtest.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FollowLocalDataSource(private val dataStore: DataStore<Preferences>) {

    private val key = stringSetPreferencesKey("followed_ids")

    val followedIds: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[key] ?: emptySet()
    }

    suspend fun toggleFollow(id: String) {
        dataStore.edit { preferences ->
            val set = preferences[key]?.toMutableSet() ?: mutableSetOf()
            if (set.contains(id)) set.remove(id) else set.add(id)
            preferences[key] = set
        }
    }
}
