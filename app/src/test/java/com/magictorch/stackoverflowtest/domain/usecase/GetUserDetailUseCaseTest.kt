package com.magictorch.stackoverflowtest.domain.usecase

import com.magictorch.stackoverflowtest.domain.model.UserDetail
import com.magictorch.stackoverflowtest.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class GetUserDetailUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private val getUserDetailUseCase = GetUserDetailUseCase(userRepository)

    @Test
    fun `invoke calls repository and returns user detail`() = runTest {
        val userId = 123
        val expectedDetail = UserDetail(
            id = userId,
            name = "Test User",
            reputation = 1000,
            profileImageUrl = null,
            location = "London",
            websiteUrl = "https://test.com"
        )
        coEvery { userRepository.getUserDetail(userId) } returns flowOf(expectedDetail)

        val result = getUserDetailUseCase(userId).first()

        assertEquals(expectedDetail, result)
    }
}
