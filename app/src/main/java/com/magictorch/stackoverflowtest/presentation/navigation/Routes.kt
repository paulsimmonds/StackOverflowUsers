package com.magictorch.stackoverflowtest.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object UserListRoute

@Serializable
data class UserDetailRoute(val userId: Int)
