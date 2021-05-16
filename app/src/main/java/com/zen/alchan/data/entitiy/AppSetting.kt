package com.zen.alchan.data.entitiy

import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.CharacterNaming
import com.zen.alchan.helper.enums.MediaNaming
import com.zen.alchan.helper.enums.StaffNaming

data class AppSetting(
    var appTheme: AppTheme = AppTheme.DEFAULT_THEME_YELLOW,
    var useCircularAvatarForProfile: Boolean = true,
    var showRecentReviewsAtHome: Boolean = true,
    var allAnimeListPosition: Int = 0,
    var allMangaListPosition: Int = 0,
    var useRelativeDateForNextAiringEpisode: Boolean = false,
    var characterNaming: CharacterNaming = CharacterNaming.FOLLOW_ANILIST,
    var staffNaming: StaffNaming = StaffNaming.FOLLOW_ANILIST,
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
) {
    companion object {
        val EMPTY_APP_SETTING = AppSetting()
    }
}