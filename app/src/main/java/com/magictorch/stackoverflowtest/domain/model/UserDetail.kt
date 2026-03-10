package com.magictorch.stackoverflowtest.domain.model

data class UserDetail(
    val id: Int,
    val name: String,
    val reputation: Int,
    val profileImageUrl: String?,
    val location: String?,
    val websiteUrl: String?,
    val topTags: List<String> = emptyList(),
    val badgeCounts: BadgeCounts = BadgeCounts(0, 0, 0),
    val creationDate: Long = 0L,
)

data class BadgeCounts(
    val gold: Int,
    val silver: Int,
    val bronze: Int,
)
