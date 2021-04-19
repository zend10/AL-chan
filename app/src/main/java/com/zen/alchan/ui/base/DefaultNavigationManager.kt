package com.zen.alchan.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.zen.alchan.ui.browse.BrowseFragment
import com.zen.alchan.ui.main.MainFragment

class DefaultNavigationManager(private val fragmentManager: FragmentManager, private val layout: Int) : NavigationManager {

    override fun navigate(page: NavigationManager.Page) {
        when (page) {
            NavigationManager.Page.PAGE_MAIN -> navigateToMain()
            NavigationManager.Page.PAGE_BROWSE -> navigateToBrowse()
        }
    }

    private fun swapPage(fragment: Fragment, page: NavigationManager.Page) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(layout, fragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.addToBackStack(page.name)
        fragmentTransaction.commit()
    }

    private fun navigateToMain() {
        swapPage(MainFragment.newInstance(), NavigationManager.Page.PAGE_MAIN)
    }

    private fun navigateToBrowse() {
        swapPage(BrowseFragment.newInstance(), NavigationManager.Page.PAGE_BROWSE)
    }
}