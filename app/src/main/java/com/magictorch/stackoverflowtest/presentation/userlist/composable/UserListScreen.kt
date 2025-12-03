package com.magictorch.stackoverflowtest.presentation.userlist.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.presentation.userlist.UserListUiState
import com.magictorch.stackoverflowtest.presentation.userlist.UserListViewModel
import com.magictorch.stackoverflowtest.presentation.image.ImageLoader
import com.magictorch.stackoverflowtest.ui.theme.StackoverflowtestTheme
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Composable
fun UserListScreen(
    viewModel: UserListViewModel,
    onFollowClick: (User) -> Unit,
    imageLoader: ImageLoader = koinInject(),
) {

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    UserListContent(
        uiState = viewModel.uiState.collectAsState().value,
        onFollowClick = onFollowClick,
        imageLoader = imageLoader
    )
}

@Composable
fun UserListContent(
    uiState: UserListUiState,
    onFollowClick: (User) -> Unit,
    imageLoader: ImageLoader,
) {
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
                items(uiState.users) { user ->
                    UserListItem(
                        user = user,
                        onFollowClick = { onFollowClick(user) },
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

@Serializable
object UserListRoute

class UiStatePreviewProvider : PreviewParameterProvider<UserListUiState> {
    override val values = sequenceOf(
        UserListUiState.Success(
            users = List(5) {
                User(
                    id = 1,
                    name = "Test",
                    reputation = 100,
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
            onFollowClick = {},
            imageLoader = FakeImageLoader()
        )
    }
}
