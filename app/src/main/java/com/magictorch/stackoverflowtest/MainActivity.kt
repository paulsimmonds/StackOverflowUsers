package com.magictorch.stackoverflowtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.magictorch.stackoverflowtest.presentation.navigation.UserDetailRoute
import com.magictorch.stackoverflowtest.presentation.navigation.UserListRoute
import com.magictorch.stackoverflowtest.presentation.userdetail.UserDetailViewModel
import com.magictorch.stackoverflowtest.presentation.userdetail.composable.UserDetailScreen
import com.magictorch.stackoverflowtest.presentation.userlist.UserListViewModel
import com.magictorch.stackoverflowtest.presentation.userlist.composable.UserListScreen
import com.magictorch.stackoverflowtest.ui.theme.StackoverflowtestTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StackoverflowtestTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = UserListRoute
                ) {
                    composable<UserListRoute> {
                        val viewModel: UserListViewModel = koinViewModel()
                        UserListScreen(
                            viewModel = viewModel,
                            onNavigateToUserDetail = { userId ->
                                navController.navigate(UserDetailRoute(userId))
                            }
                        )
                    }
                    composable<UserDetailRoute> { backStackEntry ->
                        val route: UserDetailRoute = backStackEntry.toRoute()
                        val viewModel: UserDetailViewModel = koinViewModel { parametersOf(route.userId) }
                        UserDetailScreen(
                            viewModel = viewModel,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
