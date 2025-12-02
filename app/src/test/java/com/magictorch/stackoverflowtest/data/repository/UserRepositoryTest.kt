package com.magictorch.stackoverflowtest.data.repository

import com.magictorch.stackoverflowtest.data.api.StackOverflowApiService
import com.magictorch.stackoverflowtest.data.datasource.FollowLocalDataSource
import com.magictorch.stackoverflowtest.data.dto.user.BadgeCounts
import com.magictorch.stackoverflowtest.data.dto.user.UserResponse
import com.magictorch.stackoverflowtest.data.dto.user.UsersResponse
import com.magictorch.stackoverflowtest.domain.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Test
    fun `getUsers maps API users and follow state correctly`() = runTest(testDispatcher) {
        // Arrange
        val api = mockk<StackOverflowApiService>()
        val local = mockk<FollowLocalDataSource>()

        val followState = MutableStateFlow<Set<String>>(emptySet())
        every { local.followedIds } returns followState
        coEvery { local.toggleFollow(any()) } answers {
            val id = arg<String>(0)
            val newSet = followState.value.toMutableSet()
            if (newSet.contains(id)) newSet.remove(id) else newSet.add(id)
            followState.value = newSet
        }

        val apiUsers = UsersResponse(
            items = listOf(
                mockUserResponse,
                mockUserResponse.copy(userId = 2, displayName = "Bob", reputation = 20)
            )
        )

        coEvery { api.getUsers() } returns apiUsers

        val repo = UserRepositoryImpl(api, local)

        // Act
        val users = repo.getUsers().first()

        // Assert
        assertEquals(2, users.size)
        assertEquals("Alice", users[0].name)
        assertEquals("Bob", users[1].name)
        assertTrue(users.all { !it.isFollowing }) // initial state

        // Toggle follow for Alice
        repo.toggleFollow(1)

        val updatedUsers = repo.getUsers().first()
        val alice = updatedUsers.first { it.id == 1 }
        val bob = updatedUsers.first { it.id == 2 }

        assertTrue(alice.isFollowing)
        assertTrue(!bob.isFollowing)
    }


    @Test
    fun `getUsers Flow emits updates when follow state changes`() = runTest(testDispatcher) {
        val api = mockk<StackOverflowApiService>()
        val local = mockk<FollowLocalDataSource>()

        val followState = MutableStateFlow<Set<String>>(emptySet())
        every { local.followedIds } returns followState
        coEvery { local.toggleFollow(any()) } answers {
            val id = arg<String>(0)
            val newSet = followState.value.toMutableSet()
            if (newSet.contains(id)) newSet.remove(id) else newSet.add(id)
            followState.value = newSet
        }

        val apiUsers = UsersResponse(items = listOf(mockUserResponse))
        coEvery { api.getUsers() } returns apiUsers

        val repo = UserRepositoryImpl(api, local)

        val collected = mutableListOf<List<User>>()

        val job = launch {
            repo.getUsers().take(2).toList(collected)
        }

        // Trigger change
        repo.toggleFollow(1)

        job.join()

        assertEquals(2, collected.size)
        assertTrue(!collected[0][0].isFollowing)
        assertTrue(collected[1][0].isFollowing)
    }


    @Test
    fun `toggleFollow delegates to FollowLocalDataSource`() = runTest {
        val api = mockk<StackOverflowApiService>()
        val local = mockk<FollowLocalDataSource>(relaxed = true)

        val repo = UserRepositoryImpl(api, local)

        repo.toggleFollow(42)

        coVerify { local.toggleFollow("42") }
    }
}

val mockUserResponse = UserResponse(
    userId = 1,
    displayName = "Alice",
    reputation = 10,
    profileImage = null,
    badgeCounts = BadgeCounts(
        bronze = 1,
        silver = 1,
        gold = 1
    ),
    accountId = 1,
    isEmployee = false,
    lastModifiedDate = 0,
    lastAccessDate = 0,
    reputationChangeYear = 1,
    reputationChangeQuarter = 1,
    reputationChangeMonth = 1,
    reputationChangeWeek = 1,
    reputationChangeDay = 1,
    creationDate = 1,
    userType = "user",
    acceptRate = 1,
    location = "",
    websiteUrl = "",
    link = "",
)
