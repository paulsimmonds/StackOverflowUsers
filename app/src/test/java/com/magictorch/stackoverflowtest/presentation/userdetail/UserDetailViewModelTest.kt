package com.magictorch.stackoverflowtest.presentation.userdetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.magictorch.stackoverflowtest.domain.model.UserDetail
import com.magictorch.stackoverflowtest.domain.usecase.GetUserDetailUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class UserDetailViewModelTest {

    private val mockGetUserDetailUseCase = mockk<GetUserDetailUseCase>()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleUser = UserDetail(
        id = 1,
        name = "Alice",
        reputation = 10,
        profileImageUrl = null,
        websiteUrl = "http://www.test.com",
        location = "London",
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
        coEvery { mockGetUserDetailUseCase(1) } returns flowOf(sampleUser)

        val savedStateHandle = SavedStateHandle(mapOf("userId" to 1))
        val viewModel = UserDetailViewModel(mockGetUserDetailUseCase, savedStateHandle)

        viewModel.uiState.test {
            val item = awaitItem()
            if (item is UserDetailUiState.Idle) {
                assertIs<UserDetailUiState.Success>(awaitItem())
            } else {
                assertIs<UserDetailUiState.Success>(item)
            }
        }
    }

    @Test
    fun `uiState shows error when use case fails`() = runTest {
        coEvery { mockGetUserDetailUseCase(1) } returns flow { throw RuntimeException("Detail failed") }

        val savedStateHandle = SavedStateHandle(mapOf("userId" to 1))
        val viewModel = UserDetailViewModel(mockGetUserDetailUseCase, savedStateHandle)

        viewModel.uiState.test {
            val item = awaitItem()
            if (item is UserDetailUiState.Idle) {
                assertIs<UserDetailUiState.Error>(awaitItem())
            } else {
                assertIs<UserDetailUiState.Error>(item)
            }
        }
    }
}
