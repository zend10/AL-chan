package com.zen.alchan.ui.activity

import com.zen.alchan.helper.enums.ActivityListPage

data class ActivityListParam(
    val activityListPage: ActivityListPage,
    val userId: Int
)