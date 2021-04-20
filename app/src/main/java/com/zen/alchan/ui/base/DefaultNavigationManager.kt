package com.zen.alchan.ui.base

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.zen.alchan.helper.Constant
import com.zen.alchan.ui.browse.BrowseFragment
import com.zen.alchan.ui.landing.LandingFragment
import com.zen.alchan.ui.login.LoginFragment
import com.zen.alchan.ui.main.MainFragment
import com.zen.alchan.ui.splash.SplashFragment

class DefaultNavigationManager(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val layout: Int
) : NavigationManager {

    override fun navigate(page: NavigationManager.Page, params: List<String>) {
        when (page) {
            NavigationManager.Page.SPLASH -> navigateToSplash()
            NavigationManager.Page.LANDING -> navigateToLanding()
            NavigationManager.Page.LOGIN -> if (params.isNotEmpty()) navigateToLogin(params[0]) else navigateToLogin()
            NavigationManager.Page.MAIN -> navigateToMain()
            NavigationManager.Page.BROWSE -> navigateToBrowse()
        }
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
                }
            )
        )
    }

    private fun swapPage(fragment: Fragment, page: NavigationManager.Page, skipBackStack: Boolean = false) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(layout, fragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (!skipBackStack)
            fragmentTransaction.addToBackStack(page.name)
        fragmentTransaction.commit()
    }

    private fun launchWebView(uri: Uri) {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(context, uri)
    }

    private fun navigateToSplash() {
        swapPage(SplashFragment.newInstance(), NavigationManager.Page.SPLASH, true)
    }

    private fun navigateToLanding() {
        swapPage(LandingFragment.newInstance(), NavigationManager.Page.LANDING, true)
    }

    private fun navigateToLogin(bearerToken: String? = null) {
        swapPage(LoginFragment.newInstance(bearerToken), NavigationManager.Page.LOGIN)
    }

    private fun navigateToMain() {
        swapPage(MainFragment.newInstance(), NavigationManager.Page.MAIN, true)
    }

    private fun navigateToBrowse() {
        swapPage(BrowseFragment.newInstance(), NavigationManager.Page.BROWSE, true)
    }
}