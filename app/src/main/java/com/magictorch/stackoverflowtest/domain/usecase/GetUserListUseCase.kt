package com.magictorch.stackoverflowtest.domain.usecase

import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetUserListUseCase(private val userRepository: UserRepository) {
    operator fun invoke(searchQuery: String = ""): Flow<List<User>> =
        userRepository.getUsers(searchQuery.ifBlank { null }).map { users ->
            users.sortedBy { it.name }
        }
}
