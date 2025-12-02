package com.magictorch.stackoverflowtest.presentation.userlist.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.magictorch.stackoverflowtest.R
import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.presentation.userlist.image.ImageLoader
import com.magictorch.stackoverflowtest.ui.theme.StackoverflowtestTheme
import org.koin.compose.koinInject

@Composable
fun UserListItem(
    user: User,
    onFollowClick: () -> Unit,
    imageLoader: ImageLoader = koinInject(),
) {
    Row(
        modifier = Modifier
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        imageLoader.Load(
            model = user.profileImageUrl,
            contentDescription = stringResource(R.string.user_profile_image_for, user.name),
            modifier = Modifier
                .padding(10.dp)
                .width(64.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                fontSize = 22.sp,
                text = user.name
            )
            Text(
                fontSize = 14.sp,
                text = stringResource(R.string.user_reputation, user.reputation)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            modifier = Modifier
                .padding(10.dp),
            onClick = onFollowClick,
        ) {
            Text(if (user.isFollowing) stringResource(R.string.user_unfollow) else stringResource(R.string.user_follow))
        }
    }
}

class FakeImageLoader : ImageLoader {
    @Composable
    override fun Load(model: Any?, contentDescription: String?, modifier: Modifier) {
        Box(modifier = modifier)
    }
}

@Preview(showBackground = true)
@Composable
private fun UserListItemPreviewFollowing() {
    StackoverflowtestTheme {
        UserListItem(
            user = User(
                id = 1,
                name = "Test",
                reputation = 100,
                profileImageUrl = null
            ),
            onFollowClick = {},
            imageLoader = FakeImageLoader()
        )
    }
}
