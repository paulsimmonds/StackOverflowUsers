package com.magictorch.stackoverflowtest.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.magictorch.stackoverflowtest.platform.datasource.DataStoreFollowLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class FollowLocalDataSourceTest {

    private fun  createTestDataStore(): DataStore<Preferences> {
        val file = File.createTempFile("follow-test-", ".preferences_pb")
        file.deleteOnExit()
        return PreferenceDataStoreFactory.create {
            file
        }
    }

    @Test
    fun `followedIds starts empty`() = runTest {
        val dataStore = createTestDataStore()
        val local = DataStoreFollowLocalDataSource(dataStore)

        val result = local.followedIds.first()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `toggleFollow adds an ID when not present`() = runTest {
        val dataStore = createTestDataStore()
        val local = DataStoreFollowLocalDataSource(dataStore)

        local.toggleFollow(1)

        val result = local.followedIds.first()
        assertEquals(setOf("1"), result)
    }

    @Test
    fun `toggleFollow removes an ID when already present`() = runTest {
        val dataStore = createTestDataStore()
        val local = DataStoreFollowLocalDataSource(dataStore)

        // Add first
        local.toggleFollow(1)
        assertEquals(setOf("1"), local.followedIds.first())

        // Then remove
        local.toggleFollow(1)
        assertTrue(local.followedIds.first().isEmpty())
    }

    @Test
    fun `toggleFollow multiple IDs keeps correct state`() = runTest {
        val dataStore = createTestDataStore()
        val local = DataStoreFollowLocalDataSource(dataStore)

        local.toggleFollow(1)
        local.toggleFollow(2)

        assertEquals(setOf("1", "2"), local.followedIds.first())

        // Remove one
        local.toggleFollow(1)

        assertEquals(setOf("2"), local.followedIds.first())
    }
}
