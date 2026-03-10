package com.magictorch.stackoverflowtest.data.mapper

import com.magictorch.stackoverflowtest.data.dto.user.UserResponse
import com.magictorch.stackoverflowtest.domain.model.BadgeCounts
import com.magictorch.stackoverflowtest.domain.model.UserDetail
import com.magictorch.stackoverflowtest.domain.util.StringDecoder
import com.magictorch.stackoverflowtest.util.asEpochMilliseconds

fun UserResponse.toDetailDomain(stringDecoder: StringDecoder): UserDetail {
    return UserDetail(
        id = this.userId,
        name = stringDecoder.decodeHtml(this.displayName),
        reputation = this.reputation,
        profileImageUrl = this.profileImage,
        location = this.location,
        websiteUrl = this.websiteUrl,
        badgeCounts = BadgeCounts(
            gold = this.badgeCounts.gold,
            silver = this.badgeCounts.silver,
            bronze = this.badgeCounts.bronze
        ),
        creationDate = this.creationDate.asEpochMilliseconds
    )
}
