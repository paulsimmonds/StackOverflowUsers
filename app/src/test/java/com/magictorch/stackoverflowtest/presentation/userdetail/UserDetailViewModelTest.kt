package com.magictorch.stackoverflowtest.presentation.userdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.magictorch.stackoverflowtest.domain.model.BadgeCounts
import com.magictorch.stackoverflowtest.domain.model.UserDetail
import com.magictorch.stackoverflowtest.domain.usecase.GetUserDetailUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
class UserDetailViewModelTest {

    private val mockGetUserDetailUseCase = mockk<GetUserDetailUseCase>()
    private val testDispatcher = StandardTestDispatcher()

    private val sampleUser = UserDetail(
        id = 1,
        name = "Alice",
        reputation = 10,
        profileImageUrl = null,
        websiteUrl = "http://www.test.com",
        location = "London",
        badgeCounts = BadgeCounts(1, 1, 1),
        creationDate = 0L,
        topTags = emptyList()
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
    fun `uiState initially emits Loading then Success`() = runTest {
        coEvery { mockGetUserDetailUseCase(1) } returns flowOf(sampleUser)

        val savedStateHandle = SavedStateHandle(mapOf("userId" to 1))
        val viewModel = UserDetailViewModel(mockGetUserDetailUseCase, savedStateHandle)

        viewModel.uiState.test {
            // Initial value from stateIn is now Loading
            assertIs<UserDetailUiState.Loading>(awaitItem())
            
            advanceUntilIdle()
            
            val successItem = awaitItem()
            assertIs<UserDetailUiState.Success>(successItem)
            assertEquals(sampleUser, successItem.user)
        }
    }

    @Test
    fun `uiState shows error when use case fails`() = runTest {
        coEvery { mockGetUserDetailUseCase(1) } returns flow { throw RuntimeException("Detail failed") }

        val savedStateHandle = SavedStateHandle(mapOf("userId" to 1))
        val viewModel = UserDetailViewModel(mockGetUserDetailUseCase, savedStateHandle)

        viewModel.uiState.test {
            assertIs<UserDetailUiState.Loading>(awaitItem())
            
            advanceUntilIdle()
            
            val errorItem = awaitItem()
            assertIs<UserDetailUiState.Error>(errorItem)
            assertEquals("Detail failed", errorItem.message)
        }
    }
}
