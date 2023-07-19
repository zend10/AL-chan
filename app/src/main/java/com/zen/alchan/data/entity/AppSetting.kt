package com.zen.alchan.data.entity

import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.MediaNaming
import com.zen.alchan.helper.enums.StaffNaming

data class AppSetting(
    var appTheme: AppTheme = AppTheme.DEFAULT_THEME_YELLOW,
    var useCircularAvatarForProfile: Boolean = true,
    var showRecentReviewsAtHome: Boolean = true,
    var isAllAnimeListPositionAtTop: Boolean = true,
    var isAllMangaListPositionAtTop: Boolean = true,
    var useRelativeDateForNextAiringEpisode: Boolean = false,
    var japaneseStaffNaming: StaffNaming = StaffNaming.FOLLOW_ANILIST, // not used anymore
    var japaneseMediaNaming: MediaNaming = MediaNaming.FOLLOW_ANILIST,
    var koreanMediaNaming: MediaNaming = MediaNaming.FOLLOW_ANILIST,
    var chineseMediaNaming: MediaNaming = MediaNaming.FOLLOW_ANILIST,
    var taiwaneseMediaNaming: MediaNaming = MediaNaming.FOLLOW_ANILIST,
    var sendAiringPushNotifications: Boolean = true,
    var sendActivityPushNotifications: Boolean = true,
    var sendForumPushNotifications: Boolean = true,
    var sendFollowsPushNotifications: Boolean = true,
    var sendRelationsPushNotifications: Boolean = true,
    var mergePushNotifications: Boolean = false,
    var showPushNotificationsInterval: Int = 1, // not used anymore
    var useHighestQualityImage: Boolean = false,
    var enableSocialFeature: Boolean = true, // not used anymore
    var showBioAutomatically: Boolean = true, // not used anymore
    var showStatsChartAutomatically: Boolean = true, // not used anymore
)