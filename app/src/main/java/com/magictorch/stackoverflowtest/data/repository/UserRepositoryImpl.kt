package com.magictorch.stackoverflowtest.data.repository

import com.magictorch.stackoverflowtest.data.api.StackOverflowApiService
import com.magictorch.stackoverflowtest.data.mapper.toDetailDomain
import com.magictorch.stackoverflowtest.data.mapper.toDomain
import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.model.UserDetail
import com.magictorch.stackoverflowtest.domain.repository.UserRepository
import com.magictorch.stackoverflowtest.domain.util.StringDecoder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val apiService: StackOverflowApiService,
    private val stringDecoder: StringDecoder,
) : UserRepository {
    override fun getUsers(searchQuery: String?): Flow<List<User>> = flow {
        val response = if (searchQuery.isNullOrBlank()) {
            apiService.getUsers()
        } else {
            apiService.getUsers(inname = searchQuery)
        }
        emit(response.items.map { it.toDomain(stringDecoder) })
    }

    override fun getUserDetail(userId: Int): Flow<UserDetail> = flow {
        val userResponse = apiService.getUserById(userId)
        val tagsResponse = apiService.getTopTags(userId)
        val user = userResponse.items.first()
        emit(
            user.toDetailDomain(stringDecoder).copy(
                topTags = tagsResponse.items.take(3).map { it.tagName }
            )
        )
    }
}
