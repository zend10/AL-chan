package com.zen.alchan.ui.browse

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.FollowPage
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.base.BaseListener
import com.zen.alchan.ui.browse.activity.ActivityDetailFragment
import com.zen.alchan.ui.browse.character.CharacterFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.browse.staff.StaffFragment
import com.zen.alchan.ui.browse.studio.StudioFragment
import com.zen.alchan.ui.browse.user.UserFragment
import com.zen.alchan.ui.browse.user.follows.UserFollowsFragment
import com.zen.alchan.ui.browse.user.list.UserMediaListFragment
import com.zen.alchan.ui.browse.user.stats.UserStatsDetailFragment
import kotlinx.android.synthetic.main.activity_browse.*
import type.MediaType

class BrowseActivity : BaseActivity(), BaseListener {

    companion object {
        const val TARGET_PAGE = "targetPage"
        const val LOAD_ID = "loadId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)

        if (intent?.data != null) {
            try {
                val appLinkData = intent?.data.toString()
                val splitLink = appLinkData.substring(appLinkData.indexOf("anilist.co")).split("/")
                val target = splitLink[1]
                val id = splitLink[2]
                changeFragment(BrowsePage.valueOf(target.toUpperCase()), id.toInt(), supportFragmentManager.backStackEntryCount != 0)
            } catch (e: Exception) {
                DialogUtility.showToast(this, R.string.invalid_link)
                finish()
            } finally {
                return
            }
        }

        try {
            if (supportFragmentManager.backStackEntryCount == 0) {
                changeFragment(BrowsePage.valueOf(intent.getStringExtra(TARGET_PAGE)), intent.getIntExtra(LOAD_ID, 0), false)
            }
        } catch (e: Exception) {
            DialogUtility.showToast(this, R.string.invalid_link)
            finish()
        }
    }

    override fun changeFragment(targetFragment: Fragment, addToBackStack: Boolean) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(browseFrameLayout.id, targetFragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }

    override fun changeFragment(browsePage: BrowsePage, id: Int, addToBackStack: Boolean) {
        lateinit var targetFragment: Fragment
        val bundle = Bundle()

        when (browsePage) {
            BrowsePage.ANIME -> {
                targetFragment = MediaFragment()
                bundle.putInt(MediaFragment.MEDIA_ID, id)
                bundle.putString(MediaFragment.MEDIA_TYPE, MediaType.ANIME.name)
            }
            BrowsePage.MANGA -> {
                targetFragment = MediaFragment()
                bundle.putInt(MediaFragment.MEDIA_ID, id)
                bundle.putString(MediaFragment.MEDIA_TYPE, MediaType.MANGA.name)
            }
            BrowsePage.CHARACTER -> {
                targetFragment = CharacterFragment()
                bundle.putInt(CharacterFragment.CHARACTER_ID, id)
            }
            BrowsePage.STAFF -> {
                targetFragment = StaffFragment()
                bundle.putInt(StaffFragment.STAFF_ID, id)
            }
            BrowsePage.STUDIO -> {
                targetFragment = StudioFragment()
                bundle.putInt(StudioFragment.STUDIO_ID, id)
            }
            BrowsePage.USER -> {
                targetFragment = UserFragment()
                bundle.putInt(UserFragment.USER_ID, id)
            }
            BrowsePage.USER_STATS_DETAIL -> {
                targetFragment = UserStatsDetailFragment()
                bundle.putInt(UserStatsDetailFragment.USER_ID, id)
            }
            BrowsePage.USER_ANIME_LIST -> {
                targetFragment = UserMediaListFragment()
                bundle.putInt(UserMediaListFragment.USER_ID, id)
                bundle.putString(UserMediaListFragment.MEDIA_TYPE, MediaType.ANIME.name)
            }
            BrowsePage.USER_MANGA_LIST -> {
                targetFragment = UserMediaListFragment()
                bundle.putInt(UserMediaListFragment.USER_ID, id)
                bundle.putString(UserMediaListFragment.MEDIA_TYPE, MediaType.MANGA.name)
            }
            BrowsePage.USER_FOLLOWING_LIST -> {
                targetFragment = UserFollowsFragment()
                bundle.putInt(UserFollowsFragment.USER_ID, id)
                bundle.putInt(UserFollowsFragment.START_POSITION, FollowPage.FOLLOWING.ordinal)
            }
            BrowsePage.USER_FOLLOWERS_LIST -> {
                targetFragment = UserFollowsFragment()
                bundle.putInt(UserFollowsFragment.USER_ID, id)
                bundle.putInt(UserFollowsFragment.START_POSITION, FollowPage.FOLLOWERS.ordinal)
            }
            BrowsePage.ACTIVITY_DETAIL -> {
                targetFragment = ActivityDetailFragment()
                bundle.putInt(ActivityDetailFragment.ACTIVITY_ID, id)
            }
        }

        targetFragment.arguments = bundle
        changeFragment(targetFragment, addToBackStack)
    }
}
