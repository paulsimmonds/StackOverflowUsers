package com.magictorch.stackoverflowtest.data.dto.user

import com.google.gson.annotations.SerializedName

data class UsersResponse(
    @SerializedName("items")
    val items: List<UserResponse>
)
