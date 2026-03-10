package com.magictorch.stackoverflowtest.domain.repository

import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.model.UserDetail
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(searchQuery: String? = null): Flow<List<User>>

    fun getUserDetail(userId: Int): Flow<UserDetail>
}
