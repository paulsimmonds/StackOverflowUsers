package com.magictorch.stackoverflowtest.domain.repository

import com.magictorch.stackoverflowtest.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<List<User>>
    suspend fun toggleFollow(userId: Int)
}
