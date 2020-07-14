package com.zen.alchan.helper.pojo

import com.zen.alchan.helper.enums.AppColorTheme
import type.StaffLanguage

class AppSettings(
    var appTheme: AppColorTheme? = AppColorTheme.YELLOW,
    var circularAvatar: Boolean? = true,
    var whiteBackgroundAvatar: Boolean? = true,
    var voiceActorLanguage: StaffLanguage? = StaffLanguage.JAPANESE,
    var showRecentReviews: Boolean? = true,
    var showSocialTabAutomatically: Boolean? = null,
    var showBioAutomatically: Boolean? = null,
    var showStatsAutomatically: Boolean? = null
)