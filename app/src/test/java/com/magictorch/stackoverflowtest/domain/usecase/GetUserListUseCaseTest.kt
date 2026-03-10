package com.magictorch.stackoverflowtest.domain.usecase

import app.cash.turbine.test
import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class GetUserListUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private val getUserListUseCase = GetUserListUseCase(userRepository)

    private val sampleUsers = listOf(
        User(id = 2, name = "Bob", reputation = 20, profileImageUrl = null),
        User(id = 1, name = "Alice", reputation = 10, profileImageUrl = null),
        User(id = 3, name = "Charlie", reputation = 30, profileImageUrl = null)
    )

    @Test
    fun `invoke with empty query calls repository with null and returns users sorted alphabetically by name`() = runTest {
        every { userRepository.getUsers(null) } returns flowOf(sampleUsers)

        getUserListUseCase("").test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals("Alice", result[0].name)
            assertEquals("Bob", result[1].name)
            assertEquals("Charlie", result[2].name)
            awaitComplete()
        }

        verify { userRepository.getUsers(null) }
    }

    @Test
    fun `invoke with search query calls repository with query`() = runTest {
        every { userRepository.getUsers("Alice") } returns flowOf(listOf(sampleUsers[1]))

        getUserListUseCase("Alice").test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("Alice", result[0].name)
            awaitComplete()
        }

        verify { userRepository.getUsers("Alice") }
    }
}
