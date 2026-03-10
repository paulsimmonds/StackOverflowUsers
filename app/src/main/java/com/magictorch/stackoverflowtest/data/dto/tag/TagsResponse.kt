package com.magictorch.stackoverflowtest.data.dto.tag

import com.google.gson.annotations.SerializedName

data class TagsResponse(
    @SerializedName("items")
    val items: List<TagResponse>,
)

data class TagResponse(
    @SerializedName("tag_name")
    val tagName: String,
    @SerializedName("user_id")
    val userId: Int,
)
