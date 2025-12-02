package com.magictorch.stackoverflowtest.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.usecase.GetUserListUseCase
import com.magictorch.stackoverflowtest.domain.usecase.ToggleFollowUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.delayFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class UserListViewModel(
    private val getUserListUseCase: GetUserListUseCase,
    private val toggleFollowUseCase: ToggleFollowUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserListUiState>(UserListUiState.Loading)
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    fun loadUsers() {
        if (_uiState.value !is UserListUiState.Loading) return

        viewModelScope.launch {
            getUserListUseCase()
                .onStart { _uiState.value = UserListUiState.Loading }
                .catch { e -> _uiState.value = UserListUiState.Error(e.message ?: "An unknown error occurred") }
                .collect { users -> _uiState.value = UserListUiState.Success(users) }
        }
    }

    fun toggleFollow(user: User) {
        viewModelScope.launch {
            toggleFollowUseCase(user.id)
        }
    }
}
