package com.magictorch.stackoverflowtest.presentation.userdetail

import com.magictorch.stackoverflowtest.domain.model.UserDetail

sealed interface UserDetailUiState {
    data object Loading : UserDetailUiState
    data class Success(val user: UserDetail) : UserDetailUiState
    data class Error(val message: String) : UserDetailUiState
}
