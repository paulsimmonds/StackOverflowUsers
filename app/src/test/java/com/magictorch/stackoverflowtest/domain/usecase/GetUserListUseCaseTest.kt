package com.magictorch.stackoverflowtest.domain.usecase

import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
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
    fun `invoke with empty query returns all users sorted alphabetically`() = runTest {
        coEvery { userRepository.getUsers() } returns flowOf(sampleUsers)

        val result = getUserListUseCase().first()

        assertEquals(3, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Bob", result[1].name)
        assertEquals("Charlie", result[2].name)
    }

    @Test
    fun `invoke with search query filters users case-insensitively`() = runTest {
        coEvery { userRepository.getUsers() } returns flowOf(sampleUsers)

        val result = getUserListUseCase("al").first()

        assertEquals(1, result.size)
        assertEquals("Alice", result[0].name)

        val result2 = getUserListUseCase("BOB").first()
        assertEquals(1, result2.size)
        assertEquals("Bob", result2[0].name)
    }

    @Test
    fun `invoke limits results to 20 users`() = runTest {
        val manyUsers = List(30) { i ->
            User(id = i, name = "User ${String.format("%02d", i)}", reputation = i, profileImageUrl = null)
        }
        coEvery { userRepository.getUsers() } returns flowOf(manyUsers)

        val result = getUserListUseCase().first()

        assertEquals(20, result.size)
    }
}
