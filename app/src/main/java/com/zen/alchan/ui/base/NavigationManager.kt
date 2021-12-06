package com.zen.alchan.ui.base

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.utils.DeepLink


interface NavigationManager {

    fun navigateToSplash(deepLink: DeepLink? = null, bypassSplash: Boolean = false)
    fun navigateToLanding()
    fun navigateToLogin(bearerToken: String? = null, disableAnimation: Boolean = false)
    fun navigateToMain(deepLink: DeepLink? = null)

    fun navigateToActivities()
    fun navigateToNotifications()
    fun navigateToSettings()
    fun navigateToAppSettings()
    fun navigateToAniListSettings()
    fun navigateToListSettings()
    fun navigateToNotificationsSettings()
    fun navigateToAccountSettings()
    fun navigateToAbout()

    fun navigateToReorder(itemList: List<String>, action: (reorderResult: List<String>) -> Unit)
    fun navigateToFilter(mediaFilter: MediaFilter?, mediaType: MediaType, isUserList: Boolean, action: (filterResult: MediaFilter) -> Unit)
    fun navigateToCustomise(mediaType: MediaType, action: (customiseResult: ListStyle) -> Unit)

    fun navigateToEditor(mediaType: MediaType, mediaId: Int)

    fun navigateToMedia(id: Int)
    fun navigateToCharacter(id: Int)
    fun navigateToStaff(id: Int)
    fun navigateToUser(id: Int)

    fun navigateToAnimeMediaList(id: Int)
    fun navigateToMangaMediaList(id: Int)
    fun navigateToFollowing(id: Int)
    fun navigateToFollowers(id: Int)

    fun openWebView(url: String)
    fun openWebView(url: Url)
    fun openEmailClient()
    fun openGallery(launcher: ActivityResultLauncher<Intent>)

    fun isAtPreLoginScreen(): Boolean
    fun isAtBrowseScreen(): Boolean
    fun pushBrowseScreenPage(page: BrowseNavigationManager.Page, id: Int?)
    fun popBrowseScreenPage()
    fun shouldPopFromBrowseScreen(): Boolean

    enum class Url {
        ANILIST_WEBSITE,
        ANILIST_LOGIN,
        ANILIST_REGISTER,
        ANILIST_PROFILE_SETTINGS,
        ANILIST_ACCOUNT_SETTINGS,
        ANILIST_LISTS_SETTINGS,
        ANILIST_IMPORT_LISTS,
        ANILIST_CONNECT_WITH_TWITTER,
        ALCHAN_FORUM_THREAD,
        ALCHAN_GITHUB,
        ALCHAN_PLAY_STORE,
        ALCHAN_TWITTER,
        ALCHAN_PRIVACY_POLICY
    }
}