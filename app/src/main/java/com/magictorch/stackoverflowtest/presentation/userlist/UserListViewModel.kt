package com.magictorch.stackoverflowtest.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magictorch.stackoverflowtest.domain.usecase.GetUserListUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModel(
    private val getUserListUseCase: GetUserListUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _refreshTrigger = MutableStateFlow(Unit)

    val uiState: StateFlow<UserListUiState> = _refreshTrigger.flatMapLatest {
        _searchQuery.flatMapLatest { query ->
            getUserListUseCase(query)
                .map { users -> UserListUiState.Success(users) as UserListUiState }
                .onStart { emit(UserListUiState.Loading) }
                .catch { e -> emit(UserListUiState.Error(e.message ?: "An unknown error occurred")) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserListUiState.Idle
    )

    fun loadUsers() {
        _refreshTrigger.value = Unit
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
