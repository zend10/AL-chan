package com.zen.alchan.helper.pojo

import type.ActivityType

class SocialFilter(
    var bestFriends: ArrayList<BestFriend>,
    var selectedBestFriend: BestFriend?,
    var selectedActivityType: ArrayList<ActivityType>?,
    var bannerUrl: String?
)