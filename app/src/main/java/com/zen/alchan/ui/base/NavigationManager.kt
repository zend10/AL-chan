package com.zen.alchan.ui.base


interface NavigationManager {

    fun navigateToSplash()
    fun navigateToLanding()
    fun navigateToLogin(bearerToken: String? = null)
    fun navigateToMain()
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

    fun openWebView(url: String)
    fun openWebView(url: Url)
    fun openEmailClient()

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