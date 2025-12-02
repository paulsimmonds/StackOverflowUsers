package com.magictorch.stackoverflowtest.data.repository

import com.magictorch.stackoverflowtest.data.api.StackOverflowApiService
import com.magictorch.stackoverflowtest.data.datasource.FollowLocalDataSource
import com.magictorch.stackoverflowtest.data.mapper.toDomain
import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val apiService: StackOverflowApiService,
    private val followLocalDataSource: FollowLocalDataSource
) : UserRepository {
    override fun getUsers(): Flow<List<User>> =
        combine(
            flow { emit(apiService.getUsers()) },
            followLocalDataSource.followedIds
        ) { users, follows ->
            users.items.map {
                it.toDomain(
                    isFollowing = it.userId.toString() in follows
                )
            }

        }

    override suspend fun toggleFollow(userId: Int) {
        followLocalDataSource.toggleFollow(userId.toString())
    }
}
