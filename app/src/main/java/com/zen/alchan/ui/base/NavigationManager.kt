package com.zen.alchan.ui.base

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.utils.DeepLink


interface NavigationManager {

    fun navigateToSplash(deepLink: DeepLink? = null, bypassSplash: Boolean = false)
    fun navigateToLanding()
    fun navigateToLogin(bearerToken: String? = null, disableAnimation: Boolean = false)
    fun navigateToMain(deepLink: DeepLink? = null)
    fun navigateToBrowse()

    fun navigateToActivities()
    fun navigateToNotifications()
    fun navigateToSettings()
    fun navigateToAppSettings()
    fun navigateToAniListSettings()
    fun navigateToListSettings()
    fun navigateToNotificationsSettings()
    fun navigateToAccountSettings()
    fun navigateToAbout()

    fun navigateToReorder()
    fun navigateToFilter(mediaType: MediaType, isUserList: Boolean)
    fun navigateToCustomise(mediaType: MediaType)

    fun navigateToEditor(mediaType: MediaType, mediaId: Int)

    fun openWebView(url: String)
    fun openWebView(url: Url)
    fun openEmailClient()
    fun openGallery(launcher: ActivityResultLauncher<Intent>)

    fun isAtPreLoginScreen(): Boolean

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