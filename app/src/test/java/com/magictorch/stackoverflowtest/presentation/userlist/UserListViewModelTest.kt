package com.magictorch.stackoverflowtest.presentation.userlist

import app.cash.turbine.test
import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.usecase.GetUserListUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModelTest {

    private val mockGetUserListUseCase = mockk<GetUserListUseCase>()
    private val testDispatcher = StandardTestDispatcher()

    private val sampleUsers = listOf(
        User(id = 1, name = "Alice", reputation = 10, profileImageUrl = null),
        User(id = 2, name = "Bob", reputation = 20, profileImageUrl = null),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState initially emits Idle then Loading then Success`() = runTest {
        coEvery { mockGetUserListUseCase("") } returns flowOf(sampleUsers)

        val viewModel = UserListViewModel(mockGetUserListUseCase)

        viewModel.uiState.test {
            // Initial value from stateIn
            assertEquals(UserListUiState.Idle, awaitItem())
            
            // Allow the flatMapLatest flow to run
            advanceUntilIdle()
            
            assertIs<UserListUiState.Loading>(awaitItem())
            val successItem = awaitItem()
            assertIs<UserListUiState.Success>(successItem)
            assertEquals(sampleUsers, successItem.users)
        }
    }

    @Test
    fun `search query change triggers new use case call`() = runTest {
        coEvery { mockGetUserListUseCase("") } returns flowOf(sampleUsers)
        coEvery { mockGetUserListUseCase("al") } returns flowOf(listOf(sampleUsers[0]))

        val viewModel = UserListViewModel(mockGetUserListUseCase)

        viewModel.uiState.test {
            assertEquals(UserListUiState.Idle, awaitItem())
            
            advanceUntilIdle()
            assertIs<UserListUiState.Loading>(awaitItem())
            assertIs<UserListUiState.Success>(awaitItem()) // initial ""

            viewModel.onSearchQueryChange("al")
            
            advanceUntilIdle()
            assertIs<UserListUiState.Loading>(awaitItem())
            val filteredSuccess = awaitItem() as UserListUiState.Success
            assertEquals(1, filteredSuccess.users.size)
            assertEquals("Alice", filteredSuccess.users[0].name)
        }
    }
}
