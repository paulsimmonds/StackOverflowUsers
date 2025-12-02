package com.magictorch.stackoverflowtest.presentation.userlist

import com.magictorch.stackoverflowtest.domain.model.User

sealed interface UserListUiState {
    data object Loading : UserListUiState
    data class Success(val users: List<User>) : UserListUiState
    data class Error(val message: String) : UserListUiState
}
