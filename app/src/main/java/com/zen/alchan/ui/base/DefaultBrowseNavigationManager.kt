package com.zen.alchan.ui.base

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.zen.alchan.R
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.media.MediaFragment
import com.zen.alchan.ui.profile.ProfileFragment

class DefaultBrowseNavigationManager(
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val layout: FragmentContainerView
) : BrowseNavigationManager {

    private fun navigateToMedia(mediaId: Int) {
        swapPage(MediaFragment.newInstance(mediaId))
    }

    private fun navigateToUser(userId: Int) {
        swapPage(ProfileFragment.newInstance(userId))
    }

    override fun pushBrowseScreenPage(page: BrowseNavigationManager.Page, id: Int?) {
        when (page) {
            BrowseNavigationManager.Page.MEDIA -> id?.let { navigateToMedia(id) }
            BrowseNavigationManager.Page.USER -> id?.let { navigateToUser(id) }
        }
    }

    override fun popBrowseScreenPage() {
        fragmentManager.popBackStack()
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
}