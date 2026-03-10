package com.magictorch.stackoverflowtest.data.repository

import com.magictorch.stackoverflowtest.data.api.StackOverflowApiService
import com.magictorch.stackoverflowtest.data.dto.tag.TagResponse
import com.magictorch.stackoverflowtest.data.dto.tag.TagsResponse
import com.magictorch.stackoverflowtest.data.dto.user.BadgeCounts
import com.magictorch.stackoverflowtest.data.dto.user.UserResponse
import com.magictorch.stackoverflowtest.data.dto.user.UsersResponse
import com.magictorch.stackoverflowtest.domain.util.StringDecoder
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockStringDecoder = mockk<StringDecoder>()

    @Before
    fun setup() {
        every { mockStringDecoder.decodeHtml(any()) } answers { firstArg() }
    }

    @Test
    fun `getUsers maps API users correctly`() = runTest(testDispatcher) {
        // Arrange
        val api = mockk<StackOverflowApiService>()

        val apiUsers = UsersResponse(
            items = listOf(
                mockUserResponse,
                mockUserResponse.copy(userId = 2, displayName = "Bob", reputation = 20)
            )
        )

        coEvery { api.getUsers() } returns apiUsers

        val repo = UserRepositoryImpl(api, mockStringDecoder)

        // Act
        val users = repo.getUsers().first()

        // Assert
        assertEquals(2, users.size)
        assertEquals("Alice", users[0].name)
        assertEquals("Bob", users[1].name)
    }

    @Test
    fun `getUserDetail maps API user and tags correctly`() = runTest(testDispatcher) {
        // Arrange
        val api = mockk<StackOverflowApiService>()
        val userId = 1
        val apiUsers = UsersResponse(items = listOf(mockUserResponse))
        val apiTags = TagsResponse(items = listOf(TagResponse("kotlin", userId)))

        coEvery { api.getUserById(userId) } returns apiUsers
        coEvery { api.getTopTags(userId) } returns apiTags

        val repo = UserRepositoryImpl(api, mockStringDecoder)

        // Act
        val detail = repo.getUserDetail(userId).first()

        // Assert
        assertEquals("Alice", detail.name)
        assertEquals(listOf("kotlin"), detail.topTags)
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
