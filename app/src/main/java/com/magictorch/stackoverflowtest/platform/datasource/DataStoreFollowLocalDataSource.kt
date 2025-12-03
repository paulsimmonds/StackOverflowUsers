package com.magictorch.stackoverflowtest.platform.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.magictorch.stackoverflowtest.data.datasource.FollowLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreFollowLocalDataSource(private val dataStore: DataStore<Preferences>) : FollowLocalDataSource {

    private val key = stringSetPreferencesKey("followed_ids")

    override val followedIds: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[key] ?: emptySet()
    }

    override suspend fun toggleFollow(userId: Int) {
        dataStore.edit { preferences ->
            val currentIds = preferences[key]?.toMutableSet() ?: mutableSetOf()
            val id = userId.toString()
            if (currentIds.contains(id)) {
                currentIds.remove(id)
            } else {
                currentIds.add(id)
            }
            preferences[key] = currentIds
        }
    }
}
