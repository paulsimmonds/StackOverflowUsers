package com.magictorch.stackoverflowtest.data.mapper

import com.magictorch.stackoverflowtest.data.dto.user.UserResponse
import com.magictorch.stackoverflowtest.domain.model.User

fun UserResponse.toDomain(): User {
    return User(
        id = this.userId,
        name = this.displayName,
        reputation = this.reputation,
        profileImageUrl = this.profileImage,
    )
}
