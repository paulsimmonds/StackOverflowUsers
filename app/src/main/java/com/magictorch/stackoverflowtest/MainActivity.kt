package com.magictorch.stackoverflowtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.magictorch.stackoverflowtest.presentation.userlist.UserListViewModel
import com.magictorch.stackoverflowtest.presentation.userlist.composable.UserListRoute
import com.magictorch.stackoverflowtest.presentation.userlist.composable.UserListScreen
import com.magictorch.stackoverflowtest.ui.theme.StackoverflowtestTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StackoverflowtestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = UserListRoute,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<UserListRoute> {
                            val viewModel: UserListViewModel = koinViewModel()
                            UserListScreen(
                                viewModel = viewModel,
                                onFollowClick = { user ->
                                    viewModel.toggleFollow(user)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
