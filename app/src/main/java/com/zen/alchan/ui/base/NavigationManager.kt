package com.zen.alchan.ui.base

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.data.response.anilist.Activity
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Review
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.type.ScoreFormat


interface NavigationManager {

    fun navigateToSplash(deepLink: DeepLink? = null, bypassSplash: Boolean = false) {}
    fun navigateToLanding() {}
    fun navigateToLogin(bearerToken: String? = null, disableAnimation: Boolean = false) {}
    fun navigateToMain(deepLink: DeepLink? = null) {}

    fun navigateToSearch(searchCategory: SearchCategory) {}
    fun navigateToSeasonal() {}
    fun navigateToExplore(searchCategory: SearchCategory, mediaFilter: MediaFilter? = null, action: ((() -> Unit) -> Unit)? = null) {}
    fun navigateToSocial() {}
    fun navigateToCalendar() {}
    fun navigateToReview() {}
    fun navigateToReader(review: Review, action: ((review: Review) -> Unit)) {}
    fun navigateToActivityDetail(id: Int, action: (activity: Activity, isDeleted: Boolean) -> Unit) {}
    fun navigateToActivityList(activityListPage: ActivityListPage, id: Int? = null) {}
    fun navigateToTextEditor(
        textEditorType: TextEditorType,
        activityId: Int? = null,
        activityReplyId: Int? = null,
        recipientId: Int? = null,
        username: String? = null
    ) {}

    fun navigateToSettings() {}
    fun navigateToAppSettings() {}
    fun navigateToAniListSettings() {}
    fun navigateToListSettings() {}
    fun navigateToNotificationsSettings() {}
    fun navigateToAccountSettings() {}
    fun navigateToAbout() {}

    fun navigateToReorder(itemList: List<String>, action: (reorderResult: List<String>) -> Unit) {}
    fun navigateToFilter(
        mediaFilter: MediaFilter?,
        mediaType: MediaType,
        scoreFormat: ScoreFormat,
        isUserList: Boolean,
        hasBigList: Boolean,
        isCurrentUser: Boolean,
        action: (filterResult: MediaFilter) -> Unit
    ) {}
    fun navigateToCustomise(mediaType: MediaType, action: (customiseResult: ListStyle) -> Unit) {}

    fun navigateToEditor(mediaId: Int, fromMediaList: Boolean, action: (() -> Unit)? = null) {}

    fun navigateToMedia(id: Int) {}
    fun navigateToMediaStats(media: Media) {}
    fun navigateToMediaSocial(media: Media) {}
    fun navigateToMediaReview(media: Media) {}
    fun navigateToMediaCharacters(id: Int) {}
    fun navigateToMediaStaff(id: Int) {}
    fun navigateToCharacter(id: Int) {}
    fun navigateToCharacterMedia(id: Int) {}
    fun navigateToStaff(id: Int) {}
    fun navigateToStaffCharacter(id: Int) {}
    fun navigateToStaffMedia(id: Int) {}
    fun navigateToUser(id: Int? = null, username: String? = null) {}
    fun navigateToUserReview(id: Int) {}
    fun navigateToStudio(id: Int) {}
    fun navigateToStudioMedia(id: Int) {}

    fun navigateToAnimeMediaList(id: Int) {}
    fun navigateToMangaMediaList(id: Int) {}
    fun navigateToFollowing(id: Int) {}
    fun navigateToFollowers(id: Int) {}
    fun navigateToUserStats(id: Int) {}
    fun navigateToFavorite(id: Int, favorite: Favorite) {}

    fun openWebView(url: String) {}
    fun openWebView(url: Url, id: Int? = null) {}
    fun openEmailClient() {}
    fun openGallery(launcher: ActivityResultLauncher<Intent>) {}
    fun openOnYouTube(videoId: String) {}
    fun openOnSpotify(url: String) {}

    fun isAtPreLoginScreen(): Boolean { return false }
    fun isAtBrowseScreen(): Boolean { return false }
    fun popBrowseScreenPage() {}
    fun shouldPopFromBrowseScreen(): Boolean { return false }
    fun closeBrowseScreen() {}
    fun hasBackStack(): Boolean { return false }
    fun popBackStack() {}

    enum class Url {
        ANILIST_WEBSITE,
        ANILIST_LOGIN,
        ANILIST_REGISTER,
        ANILIST_PROFILE_SETTINGS,
        ANILIST_ACCOUNT_SETTINGS,
        ANILIST_LISTS_SETTINGS,
        ANILIST_IMPORT_LISTS,
        ANILIST_CONNECT_WITH_TWITTER,
        ANLIST_ACTIVITY,
        ALCHAN_FORUM_THREAD,
        ALCHAN_GITHUB,
        ALCHAN_PLAY_STORE,
        ALCHAN_TWITTER,
        ALCHAN_PRIVACY_POLICY
    }
}