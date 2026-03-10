package com.magictorch.stackoverflowtest.data.repository

import com.magictorch.stackoverflowtest.data.api.StackOverflowApiService
import com.magictorch.stackoverflowtest.data.mapper.toDetailDomain
import com.magictorch.stackoverflowtest.data.mapper.toDomain
import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.model.UserDetail
import com.magictorch.stackoverflowtest.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.zip

class UserRepositoryImpl(
    private val apiService: StackOverflowApiService,
) : UserRepository {
    override fun getUsers(): Flow<List<User>> = flow {
        val users = apiService.getUsers()
        emit(users.items.map { it.toDomain() })
    }

    override fun getUserDetail(userId: Int): Flow<UserDetail> = flow {
        emit(apiService.getUserById(userId))
    }.zip(flow { emit(apiService.getTopTags(userId)) }) { users, tags ->
        val user = users.items.first()
        user.toDetailDomain().copy(
            topTags = tags.items.take(3).map { it.tagName }
        )
    }
}
