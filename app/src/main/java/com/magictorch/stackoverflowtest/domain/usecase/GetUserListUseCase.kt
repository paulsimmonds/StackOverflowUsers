package com.magictorch.stackoverflowtest.domain.usecase

import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserListUseCase(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<List<User>> = userRepository.getUsers()
}
