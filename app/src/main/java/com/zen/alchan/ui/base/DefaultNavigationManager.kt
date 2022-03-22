package com.zen.alchan.ui.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.zen.alchan.R
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.entity.MediaFilter
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.Favorite
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.ui.activity.ActivityFragment
import com.zen.alchan.ui.browse.BrowseFragment
import com.zen.alchan.ui.customise.CustomiseFragment
import com.zen.alchan.ui.editor.EditorFragment
import com.zen.alchan.ui.filter.FilterFragment
import com.zen.alchan.ui.landing.LandingFragment
import com.zen.alchan.ui.login.LoginFragment
import com.zen.alchan.ui.main.MainFragment
import com.zen.alchan.ui.reorder.ReorderFragment
import com.zen.alchan.ui.settings.SettingsFragment
import com.zen.alchan.ui.settings.about.AboutFragment
import com.zen.alchan.ui.settings.account.AccountSettingsFragment
import com.zen.alchan.ui.settings.anilist.AniListSettingsFragment
import com.zen.alchan.ui.settings.app.AppSettingsFragment
import com.zen.alchan.ui.settings.list.ListSettingsFragment
import com.zen.alchan.ui.settings.notifications.NotificationsSettingsFragment
import com.zen.alchan.ui.splash.SplashFragment
import type.ScoreFormat

class DefaultNavigationManager(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val layout: FragmentContainerView
) : NavigationManager {

    override fun navigateToSplash(deepLink: DeepLink?, bypassSplash: Boolean) {
        swapPage(SplashFragment.newInstance(deepLink, bypassSplash), true)
    }

    override fun navigateToLanding() {
        swapPage(LandingFragment.newInstance(), true)
    }

    override fun navigateToLogin(bearerToken: String?, disableAnimation: Boolean) {
        swapPage(LoginFragment.newInstance(bearerToken), true, disableAnimation)
    }

    override fun navigateToMain(deepLink: DeepLink?) {
        swapPage(MainFragment.newInstance(deepLink), true)
    }

    override fun navigateToActivities() {
        swapPage(ActivityFragment.newInstance())
    }

    override fun navigateToSettings() {
        stackPage(SettingsFragment.newInstance())
    }

    override fun navigateToAppSettings() {
        stackPage(AppSettingsFragment.newInstance())
    }

    override fun navigateToAniListSettings() {
        stackPage(AniListSettingsFragment.newInstance())
    }

    override fun navigateToListSettings() {
        stackPage(ListSettingsFragment.newInstance())
    }

    override fun navigateToNotificationsSettings() {
        stackPage(NotificationsSettingsFragment.newInstance())
    }

    override fun navigateToAccountSettings() {
        stackPage(AccountSettingsFragment.newInstance())
    }

    override fun navigateToAbout() {
        stackPage(AboutFragment.newInstance())
    }

    override fun navigateToReorder(itemList: List<String>, action: (reorderResult: List<String>) -> Unit) {
        stackPage(ReorderFragment.newInstance(itemList, object : ReorderFragment.ReorderListener {
            override fun getReorderResult(reorderResult: List<String>) {
                action(reorderResult)
            }
        }))
    }

    override fun navigateToFilter(mediaFilter: MediaFilter?, mediaType: MediaType, scoreFormat: ScoreFormat, isUserList: Boolean, isCurrentUser: Boolean, action: (filterResult: MediaFilter) -> Unit) {
        stackPage(FilterFragment.newInstance(mediaFilter, mediaType, scoreFormat, isUserList, isCurrentUser, object : FilterFragment.FilterListener {
            override fun getFilterResult(filterResult: MediaFilter) {
                action(filterResult)
            }
        }))
    }

    override fun navigateToCustomise(mediaType: MediaType, action: (customiseResult: ListStyle) -> Unit) {
        stackPage(CustomiseFragment.newInstance(mediaType, object : CustomiseFragment.CustomiseListener {
            override fun getCustomiseResult(customiseResult: ListStyle) {
                action(customiseResult)
            }
        }))
    }

    override fun navigateToEditor(mediaId: Int, fromMediaList: Boolean, action: (() -> Unit)?) {
        stackPage(EditorFragment.newInstance(mediaId, fromMediaList, object : EditorFragment.EditorListener {
            override fun onEntryEdited() {
                action?.invoke()
            }
        }))
    }

    override fun navigateToMedia(id: Int) {
        pushBrowseScreenPage(BrowseNavigationManager.Page.MEDIA, id)
    }

    override fun navigateToCharacter(id: Int) {
        pushBrowseScreenPage(BrowseNavigationManager.Page.CHARACTER, id)
    }

    override fun navigateToStaff(id: Int) {
        pushBrowseScreenPage(BrowseNavigationManager.Page.STAFF, id)
    }

    override fun navigateToUser(id: Int) {
        pushBrowseScreenPage(BrowseNavigationManager.Page.USER, id)
    }

    override fun navigateToStudio(id: Int) {
        pushBrowseScreenPage(BrowseNavigationManager.Page.STUDIO, id)
    }

    override fun navigateToAnimeMediaList(id: Int) {
        pushBrowseScreenPage(BrowseNavigationManager.Page.ANIME_MEDIA_LIST, id)
    }

    override fun navigateToMangaMediaList(id: Int) {
        pushBrowseScreenPage(BrowseNavigationManager.Page.MANGA_MEDIA_LIST, id)
    }

    override fun navigateToFollowing(id: Int) {
        pushBrowseScreenPage(BrowseNavigationManager.Page.FOLLOWING, id)
    }

    override fun navigateToFollowers(id: Int) {
        pushBrowseScreenPage(BrowseNavigationManager.Page.FOLLOWERS, id)
    }

    override fun navigateToUserStats(id: Int) {
        pushBrowseScreenPage(BrowseNavigationManager.Page.USER_STATS, id)
    }

    override fun navigateToFavorite(id: Int, favorite: Favorite) {
        val browseScreenPage = when (favorite) {
            Favorite.ANIME -> BrowseNavigationManager.Page.FAVORITE_ANIME
            Favorite.MANGA -> BrowseNavigationManager.Page.FAVORITE_MANGA
            Favorite.CHARACTERS -> BrowseNavigationManager.Page.FAVORITE_CHARACTERS
            Favorite.STAFF -> BrowseNavigationManager.Page.FAVORITE_STAFF
            Favorite.STUDIOS -> BrowseNavigationManager.Page.FAVORITE_STUDIOS
        }
        pushBrowseScreenPage(browseScreenPage, id)
    }

    override fun openWebView(url: String) {
        launchWebView(Uri.parse(url))
    }

    override fun openWebView(url: NavigationManager.Url) {
        launchWebView(
            Uri.parse(
                when (url) {
                    NavigationManager.Url.ANILIST_WEBSITE -> Constant.ANILIST_WEBSITE_URL
                    NavigationManager.Url.ANILIST_LOGIN -> Constant.ANILIST_LOGIN_URL
                    NavigationManager.Url.ANILIST_REGISTER -> Constant.ANILIST_REGISTER_URL
                    NavigationManager.Url.ANILIST_PROFILE_SETTINGS -> Constant.ANILIST_PROFILE_SETTINGS_URL
                    NavigationManager.Url.ANILIST_ACCOUNT_SETTINGS -> Constant.ANILIST_ACCOUNT_SETTINGS_URL
                    NavigationManager.Url.ANILIST_LISTS_SETTINGS -> Constant.ANILIST_LISTS_SETTINGS_URL
                    NavigationManager.Url.ANILIST_IMPORT_LISTS -> Constant.ANILIST_IMPORT_LISTS_URL
                    NavigationManager.Url.ANILIST_CONNECT_WITH_TWITTER -> Constant.ANILIST_CONNECT_WITH_TWITTER_URL
                    NavigationManager.Url.ALCHAN_FORUM_THREAD -> Constant.ALCHAN_FORUM_THREAD_URL
                    NavigationManager.Url.ALCHAN_GITHUB -> Constant.ALCHAN_GITHUB_URL
                    NavigationManager.Url.ALCHAN_PLAY_STORE -> Constant.ALCHAN_PLAY_STORE_URL
                    NavigationManager.Url.ALCHAN_TWITTER -> Constant.ALCHAN_TWITTER_URL
                    NavigationManager.Url.ALCHAN_PRIVACY_POLICY -> Constant.ALCHAN_PRIVACY_POLICY_URL
                }
            )
        )
    }

    override fun openEmailClient() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", Constant.ALCHAN_EMAIL_ADDRESS, null))
        context.startActivity(intent)
    }

    override fun openGallery(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        launcher.launch(intent)
    }

    override fun isAtPreLoginScreen(): Boolean {
        val fragments = fragmentManager.fragments.filterIsInstance<BaseFragment<*, *>>()
        if (fragments.isEmpty()) return true
        val lastFragment = fragments.last()
        return lastFragment is SplashFragment || lastFragment is LandingFragment || lastFragment is LoginFragment
    }

    override fun isAtBrowseScreen(): Boolean {
        val fragments = fragmentManager.fragments.filterIsInstance<BaseFragment<*, *>>()
        if (fragments.isEmpty()) return false
        val lastFragment = fragments.last()
        return lastFragment is BrowseFragment
    }

    override fun pushBrowseScreenPage(page: BrowseNavigationManager.Page, id: Int?) {
        if (isAtBrowseScreen()) {
            val browseFragment = fragmentManager.fragments.filterIsInstance<BrowseFragment>().last()
            browseFragment.browseNavigationManager.pushBrowseScreenPage(page, id)
        } else {
            stackPage(BrowseFragment.newInstance(page, id))
        }
    }

    override fun popBrowseScreenPage() {
        if (isAtBrowseScreen()) {
            val browseFragment = fragmentManager.fragments.filterIsInstance<BrowseFragment>().last()
            browseFragment.browseNavigationManager.popBrowseScreenPage()
        }
    }

    override fun shouldPopFromBrowseScreen(): Boolean {
        if (isAtBrowseScreen()) {
            val browseFragment = fragmentManager.fragments.filterIsInstance<BrowseFragment>().last()
            return browseFragment.browseNavigationManager.backStackCount() > 1
        } else {
            return false
        }
    }

    override fun closeBrowseScreen() {
        if (isAtBrowseScreen()) {
            fragmentManager.popBackStack()
        }
    }

    private fun swapPage(fragment: Fragment, skipBackStack: Boolean = false, disableAnimation: Boolean = false) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (!disableAnimation) {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
        }
        fragmentTransaction.replace(layout.id, fragment)
        if (!skipBackStack) {
            fragmentTransaction.addToBackStack(fragment.toString())
        }
        fragmentTransaction.commit()
    }

    private fun stackPage(fragment: Fragment, disableAnimation: Boolean = false) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (!disableAnimation) {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
        }
        fragmentTransaction.add(layout.id, fragment)
        fragmentTransaction.addToBackStack(fragment.toString())
        fragmentTransaction.commit()
    }

    private fun launchWebView(uri: Uri) {
        // TODO: fix crash issue
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(context, uri)
    }
}