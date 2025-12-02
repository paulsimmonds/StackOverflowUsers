package com.magictorch.stackoverflowtest.domain.model

data class User(
    val id: Int,
    val name: String,
    val reputation: Int,
    val profileImageUrl: String?,
    val isFollowing: Boolean = false
)
