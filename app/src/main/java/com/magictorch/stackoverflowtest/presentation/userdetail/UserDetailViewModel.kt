package com.magictorch.stackoverflowtest.presentation.userdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magictorch.stackoverflowtest.domain.usecase.GetUserDetailUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class UserDetailViewModel(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val userId: Int = savedStateHandle.get<Int>("userId")
        ?: throw IllegalArgumentException("userId required")

    val uiState: StateFlow<UserDetailUiState> = flow {
        // why not .onStart { emit(UserDetailUiState.Loading)? }
        emit(UserDetailUiState.Loading)
        getUserDetailUseCase(userId)
            .catch { e ->
                emit(UserDetailUiState.Error(e.message ?: "An unknown error occurred"))
            }
            .collect { userDetail ->
                emit(UserDetailUiState.Success(userDetail))
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserDetailUiState.Idle
    )
}
