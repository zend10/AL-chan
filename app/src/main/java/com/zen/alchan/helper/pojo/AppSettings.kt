package com.zen.alchan.helper.pojo

import com.zen.alchan.helper.enums.AppColorTheme
import type.StaffLanguage

class AppSettings(
    var appTheme: AppColorTheme? = AppColorTheme.YELLOW,
    var circularAvatar: Boolean? = true,
    var whiteBackgroundAvatar: Boolean? = true,
    var voiceActorLanguage: StaffLanguage? = StaffLanguage.JAPANESE
)