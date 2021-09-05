package com.zen.alchan.data.entitiy

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
    var japaneseStaffNaming: StaffNaming = StaffNaming.FOLLOW_ANILIST,
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
    var showPushNotificationsInterval: Int = 1,
    var useHighestQualityImage: Boolean = false,
    var enableSocialFeature: Boolean = true,
    var showBioAutomatically: Boolean = true,
    var showStatsChartAutomatically: Boolean = true,
)