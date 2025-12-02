package com.magictorch.stackoverflowtest.presentation.userlist

import app.cash.turbine.test
import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.usecase.GetUserListUseCase
import com.magictorch.stackoverflowtest.domain.usecase.ToggleFollowUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelTest {

    private val mockGetUserListUseCase = mockk<GetUserListUseCase>()
    private val mockToggleFollowUseCase = mockk<ToggleFollowUseCase>(relaxed = true)

    private val sampleUsers = listOf(
        User(id = 1, name = "Alice", reputation = 10, profileImageUrl = null, isFollowing = false),
        User(id = 2, name = "Bob", reputation = 20, profileImageUrl = null, isFollowing = true)
    )

    @Test
    fun `loadUsers sets uiState to Success when use case emits users`() = runTest {
        coEvery { mockGetUserListUseCase() } returns flow { emit(sampleUsers) }

        val viewModel = UserListViewModel(mockGetUserListUseCase, mockToggleFollowUseCase)

        viewModel.uiState.test {
            // Trigger loading
            viewModel.loadUsers()

            val first = awaitItem()
            assertTrue(first is UserListUiState.Loading)

            val second = awaitItem()
            assertTrue(second is UserListUiState.Success)
            assertEquals(sampleUsers, second.users)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadUsers sets uiState to Error when use case throws`() = runTest {
        val exception = RuntimeException("API failed")
        coEvery { mockGetUserListUseCase() } returns flow { throw exception }

        val viewModel = UserListViewModel(mockGetUserListUseCase, mockToggleFollowUseCase)

        viewModel.uiState.test {
            // Trigger loading
            viewModel.loadUsers()

            val first = awaitItem()
            assertTrue(first is UserListUiState.Loading)

            val second = awaitItem()
            assertTrue(second is UserListUiState.Error)
            assertEquals("API failed", second.message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadUsers does not reload if already Success`() = runTest {
        coEvery { mockGetUserListUseCase() } returns flow { emit(sampleUsers) }

        val viewModel = UserListViewModel(mockGetUserListUseCase, mockToggleFollowUseCase)

        viewModel.uiState.test {
            viewModel.loadUsers()
            awaitItem() // Loading
            awaitItem() // Success
        }

        // Call again — should not re-trigger
        viewModel.loadUsers()

        verify(exactly = 1) { mockGetUserListUseCase() }
    }

    @Test
    fun `toggleFollow calls ToggleFollowUseCase with correct id`() = runTest {
        coEvery { mockGetUserListUseCase() } returns flow { emit(emptyList()) }

        val viewModel = UserListViewModel(mockGetUserListUseCase, mockToggleFollowUseCase)
        val user = User(id = 42, name = "Charlie", reputation = 0, profileImageUrl = null, isFollowing = false)

        viewModel.toggleFollow(user)

        coVerify { mockToggleFollowUseCase(42) }
    }
}
