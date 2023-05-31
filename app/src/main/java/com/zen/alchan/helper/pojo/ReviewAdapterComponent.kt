package com.zen.alchan.helper.pojo

import com.zen.alchan.data.entity.AppSetting

data class ReviewAdapterComponent(
    val appSetting: AppSetting = AppSetting(),
    val isMediaReview: Boolean = true,
    val isUserReview: Boolean = true
)
