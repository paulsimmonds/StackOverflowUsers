package com.magictorch.stackoverflowtest.data.mapper

import com.magictorch.stackoverflowtest.data.dto.user.UserResponse
import com.magictorch.stackoverflowtest.domain.model.User
import com.magictorch.stackoverflowtest.domain.util.StringDecoder

fun UserResponse.toDomain(stringDecoder: StringDecoder): User {
    return User(
        id = this.userId,
        name = stringDecoder.decodeHtml(this.displayName),
        reputation = this.reputation,
        profileImageUrl = this.profileImage,
    )
}
