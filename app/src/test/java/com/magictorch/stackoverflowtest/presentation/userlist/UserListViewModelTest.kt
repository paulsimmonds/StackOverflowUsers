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
    fun `initialization triggers search and emits Loading then Success`() = runTest {
        coEvery { mockGetUserListUseCase("") } returns flowOf(sampleUsers)

        val viewModel = UserListViewModel(mockGetUserListUseCase)

        viewModel.uiState.test {
            // Initial value from stateIn is Loading
            assertIs<UserListUiState.Loading>(awaitItem())
            
            // Allow the flatMapLatest flow to run
            advanceUntilIdle()
            
            val successItem = awaitItem()
            assertIs<UserListUiState.Success>(successItem)
            assertEquals(sampleUsers, successItem.users)
        }
    }

    @Test
    fun `QueryChanged event updates search query but does not trigger search`() = runTest {
        coEvery { mockGetUserListUseCase("") } returns flowOf(sampleUsers)

        val viewModel = UserListViewModel(mockGetUserListUseCase)

        viewModel.uiState.test {
            assertIs<UserListUiState.Loading>(awaitItem())
            assertIs<UserListUiState.Success>(awaitItem())

            viewModel.onEvent(UserListEvent.QueryChanged("Alice"))
            assertEquals("Alice", viewModel.searchQuery.value)
            
            // Should NOT emit a new loading state because flatMapLatest is triggered by _searchTrigger
            expectNoEvents()
        }
    }

    @Test
    fun `SearchClicked event triggers new use case call with current query`() = runTest {
        coEvery { mockGetUserListUseCase("") } returns flowOf(sampleUsers)
        coEvery { mockGetUserListUseCase("Alice") } returns flowOf(listOf(sampleUsers[0]))

        val viewModel = UserListViewModel(mockGetUserListUseCase)

        viewModel.uiState.test {
            assertIs<UserListUiState.Loading>(awaitItem())
            assertIs<UserListUiState.Success>(awaitItem())

            viewModel.onEvent(UserListEvent.QueryChanged("Alice"))
            viewModel.onEvent(UserListEvent.SearchClicked)
            
            advanceUntilIdle()
            assertIs<UserListUiState.Loading>(awaitItem())
            val filteredSuccess = awaitItem() as UserListUiState.Success
            assertEquals(1, filteredSuccess.users.size)
            assertEquals("Alice", filteredSuccess.users[0].name)
        }
    }

    @Test
    fun `ClearClicked event resets query and triggers search`() = runTest {
        coEvery { mockGetUserListUseCase("") } returns flowOf(sampleUsers)
        coEvery { mockGetUserListUseCase("Alice") } returns flowOf(listOf(sampleUsers[0]))

        val viewModel = UserListViewModel(mockGetUserListUseCase)

        viewModel.uiState.test {
            assertIs<UserListUiState.Loading>(awaitItem())
            assertIs<UserListUiState.Success>(awaitItem())

            viewModel.onEvent(UserListEvent.QueryChanged("Alice"))
            viewModel.onEvent(UserListEvent.SearchClicked)
            advanceUntilIdle()
            assertIs<UserListUiState.Loading>(awaitItem())
            assertIs<UserListUiState.Success>(awaitItem())

            viewModel.onEvent(UserListEvent.ClearClicked)
            assertEquals("", viewModel.searchQuery.value)
            
            advanceUntilIdle()
            assertIs<UserListUiState.Loading>(awaitItem())
            val clearedSuccess = awaitItem() as UserListUiState.Success
            assertEquals(2, clearedSuccess.users.size)
        }
    }
}
