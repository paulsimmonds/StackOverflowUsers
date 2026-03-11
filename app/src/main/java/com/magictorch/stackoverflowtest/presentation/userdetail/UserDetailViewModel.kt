package com.magictorch.stackoverflowtest.presentation.userdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magictorch.stackoverflowtest.domain.usecase.GetUserDetailUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class UserDetailViewModel(
    getUserDetailUseCase: GetUserDetailUseCase,
    userId: Int,
) : ViewModel() {

    val uiState: StateFlow<UserDetailUiState> = getUserDetailUseCase(userId)
        .map { userDetail -> UserDetailUiState.Success(userDetail) as UserDetailUiState }
        .onStart { emit(UserDetailUiState.Loading) }
        .catch { e -> emit(UserDetailUiState.Error(e.message ?: "An unknown error occurred")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserDetailUiState.Loading
        )
}
