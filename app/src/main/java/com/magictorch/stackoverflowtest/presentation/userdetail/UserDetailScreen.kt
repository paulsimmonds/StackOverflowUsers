package com.magictorch.stackoverflowtest.presentation.userdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.magictorch.stackoverflowtest.R
import com.magictorch.stackoverflowtest.domain.model.BadgeCounts
import com.magictorch.stackoverflowtest.domain.model.UserDetail
import com.magictorch.stackoverflowtest.presentation.image.ImageLoader
import com.magictorch.stackoverflowtest.presentation.userlist.composable.FakeImageLoader
import com.magictorch.stackoverflowtest.ui.theme.StackoverflowtestTheme
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UserDetailScreen(
    viewModel: UserDetailViewModel,
    onBackClick: () -> Unit,
    imageLoader: ImageLoader = koinInject(),
) {
    UserDetailContent(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onBackClick = onBackClick,
        imageLoader = imageLoader
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailContent(
    uiState: UserDetailUiState,
    onBackClick: () -> Unit,
    imageLoader: ImageLoader,
) {
    val title = when (uiState) {
        is UserDetailUiState.Success -> uiState.user.name
        else -> "User Details"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is UserDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UserDetailUiState.Success -> {
                    val user = uiState.user
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        imageLoader.Load(
                            model = user.profileImageUrl,
                            contentDescription = stringResource(R.string.user_profile_image_for, user.name),
                            modifier = Modifier
                                .size(128.dp)
                                .padding(bottom = 16.dp)
                        )
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = stringResource(R.string.user_reputation, user.reputation),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        if (user.topTags.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Top Tags: ${user.topTags.joinToString()}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Text(text = "Badges: ", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Gold: ${user.badgeCounts.gold} ", color = MaterialTheme.colorScheme.primary)
                            Text(text = "Silver: ${user.badgeCounts.silver} ", color = MaterialTheme.colorScheme.secondary)
                            Text(text = "Bronze: ${user.badgeCounts.bronze}", color = MaterialTheme.colorScheme.tertiary)
                        }

                        user.location?.let {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Location: $it",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        val creationDate = dateFormat.format(Date(user.creationDate))
                        Text(
                            text = "Creation Date: $creationDate",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                is UserDetailUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = uiState.message)
                    }
                }

                else -> {}
            }
        }
    }
}

class UserDetailUiStateProvider : PreviewParameterProvider<UserDetailUiState> {
    override val values = sequenceOf(
        UserDetailUiState.Success(
            user = UserDetail(
                id = 1,
                name = "Jon Skeet",
                reputation = 1234567,
                profileImageUrl = null,
                location = "Reading, United Kingdom",
                websiteUrl = "http://csharpindepth.com",
                badgeCounts = BadgeCounts(gold = 1000, silver = 5000, bronze = 10000),
                creationDate = 1222430705000L,
                topTags = listOf("c#", "java", "android")
            )
        ),
        UserDetailUiState.Loading,
        UserDetailUiState.Error("An error occurred")
    )
}

@Preview(showBackground = true)
@Composable
fun UserDetailPreview(
    @PreviewParameter(UserDetailUiStateProvider::class) uiState: UserDetailUiState
) {
    StackoverflowtestTheme {
        UserDetailContent(
            uiState = uiState,
            onBackClick = {},
            imageLoader = FakeImageLoader()
        )
    }
}
