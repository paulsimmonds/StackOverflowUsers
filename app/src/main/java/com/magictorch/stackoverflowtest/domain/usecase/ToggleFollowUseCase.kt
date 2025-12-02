package com.magictorch.stackoverflowtest.domain.usecase

import com.magictorch.stackoverflowtest.domain.repository.UserRepository

class ToggleFollowUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(userId: Int) = userRepository.toggleFollow(userId)
}
