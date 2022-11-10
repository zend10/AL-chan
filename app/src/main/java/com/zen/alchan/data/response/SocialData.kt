package com.zen.alchan.data.response

import com.zen.alchan.data.response.anilist.Activity

data class SocialData(
    val friendsActivities: List<Activity>,
    val globalActivities: List<Activity>
)