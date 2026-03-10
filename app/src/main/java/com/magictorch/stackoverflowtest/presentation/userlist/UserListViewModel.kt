package com.magictorch.stackoverflowtest.presentation.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magictorch.stackoverflowtest.domain.usecase.GetUserListUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

sealed class UserListEvent {
    data class QueryChanged(val query: String) : UserListEvent()
    data object SearchClicked : UserListEvent()
    data object ClearClicked : UserListEvent()
}

@OptIn(ExperimentalCoroutinesApi::class)
class UserListViewModel(
    private val getUserListUseCase: GetUserListUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchTrigger = MutableSharedFlow<Unit>(replay = 1)

    val uiState: StateFlow<UserListUiState> = _searchTrigger.flatMapLatest {
        getUserListUseCase(_searchQuery.value)
            .map { users -> UserListUiState.Success(users) as UserListUiState }
            .onStart { emit(UserListUiState.Loading) }
            .catch { e -> emit(UserListUiState.Error(e.message ?: "An unknown error occurred")) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserListUiState.Loading
    )

    init {
        onEvent(UserListEvent.SearchClicked)
    }

    fun onEvent(event: UserListEvent) {
        when (event) {
            is UserListEvent.QueryChanged -> {
                _searchQuery.value = event.query
            }
            UserListEvent.SearchClicked -> {
                _searchTrigger.tryEmit(Unit)
            }
            UserListEvent.ClearClicked -> {
                _searchQuery.value = ""
                _searchTrigger.tryEmit(Unit)
            }
        }
    }
}
