package com.magictorch.stackoverflowtest.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestScope
import org.junit.rules.TemporaryFolder

@ExperimentalCoroutinesApi
fun TestScope.testDataStore(): DataStore<Preferences> {
    val aTemporaryFolder = TemporaryFolder()
    aTemporaryFolder.create()
    return PreferenceDataStoreFactory.create(
        scope = this,
        produceFile = { aTemporaryFolder.newFile("test.preferences_pb") }
    )
}
