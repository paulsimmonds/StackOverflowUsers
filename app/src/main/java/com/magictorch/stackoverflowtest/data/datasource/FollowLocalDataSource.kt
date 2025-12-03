package com.magictorch.stackoverflowtest.data.datasource

import kotlinx.coroutines.flow.Flow

interface FollowLocalDataSource {
    val followedIds: Flow<Set<String>>
    suspend fun toggleFollow(userId: Int)
}
