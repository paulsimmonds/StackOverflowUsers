package com.magictorch.stackoverflowtest.presentation.userlist.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.magictorch.stackoverflowtest.R
import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.presentation.image.ImageLoader
import com.magictorch.stackoverflowtest.presentation.userlist.UserListEvent
import com.magictorch.stackoverflowtest.presentation.userlist.UserListUiState
import com.magictorch.stackoverflowtest.presentation.userlist.UserListViewModel
import com.magictorch.stackoverflowtest.ui.theme.StackoverflowtestTheme
import org.koin.compose.koinInject

@Composable
fun UserListScreen(
    viewModel: UserListViewModel,
    onNavigateToUserDetail: (Int) -> Unit,
    imageLoader: ImageLoader = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    UserListContent(
        uiState = uiState,
        searchQuery = searchQuery,
        onEvent = viewModel::onEvent,
        onNavigateToUserDetail = onNavigateToUserDetail,
        imageLoader = imageLoader
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListContent(
    uiState: UserListUiState,
    searchQuery: String,
    onEvent: (UserListEvent) -> Unit,
    onNavigateToUserDetail: (Int) -> Unit,
    imageLoader: ImageLoader,
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { onEvent(UserListEvent.QueryChanged(it)) },
                    modifier = Modifier.weight(1f),
                    label = { Text(stringResource(R.string.search_by_name)) },
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                focusManager.clearFocus()
                                onEvent(UserListEvent.ClearClicked)
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            onEvent(UserListEvent.SearchClicked)
                        }
                    )
                )
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onEvent(UserListEvent.SearchClicked)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("SEARCH")
                }
            }

            when (uiState) {
                is UserListUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UserListUiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.users, key = { it.id }) { user ->
                            UserListItem(
                                user = user,
                                onNavigateToUserDetail = onNavigateToUserDetail,
                                imageLoader = imageLoader
                            )
                        }
                    }
                }

                is UserListUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = uiState.message)
                    }
                }
            }
        }
    }
}

class UiStatePreviewProvider : PreviewParameterProvider<UserListUiState> {
    override val values = sequenceOf(
        UserListUiState.Success(
            users = List(5) {
                User(
                    id = it,
                    name = "User $it",
                    reputation = 100 * it,
                    profileImageUrl = null
                )
            }
        ),
        UserListUiState.Error("Failed to load content"),
        UserListUiState.Loading
    )
}

@Preview(showBackground = true)
@Composable
private fun UserListScreenSuccessPreview(
    @PreviewParameter(UiStatePreviewProvider::class) uiState: UserListUiState,
) {
    StackoverflowtestTheme {
        UserListContent(
            uiState = uiState,
            searchQuery = "",
            onEvent = {},
            onNavigateToUserDetail = {},
            imageLoader = FakeImageLoader()
        )
    }
}
