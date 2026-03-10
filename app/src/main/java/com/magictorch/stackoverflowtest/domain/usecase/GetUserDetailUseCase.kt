package com.magictorch.stackoverflowtest.domain.usecase

import com.magictorch.stackoverflowtest.domain.model.UserDetail
import com.magictorch.stackoverflowtest.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserDetailUseCase(private val userRepository: UserRepository) {
    operator fun invoke(userId: Int): Flow<UserDetail> = userRepository.getUserDetail(userId)
}
