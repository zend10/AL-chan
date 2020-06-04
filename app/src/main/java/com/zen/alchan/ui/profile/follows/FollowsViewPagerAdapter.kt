package com.zen.alchan.ui.profile.follows

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.zen.alchan.helper.enums.FollowPage
import com.zen.alchan.ui.browse.user.follows.UserFollowsFragment

class FollowsViewPagerAdapter(fm: FragmentManager, private val userId: Int? = null) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val fragment = FollowsFragment()
        val bundle = Bundle()
        bundle.putString(FollowsFragment.FOLLOW_PAGE, FollowPage.values()[position].name)
        if (userId != null) {
            bundle.putInt(UserFollowsFragment.USER_ID, userId)
        }
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int {
        return FollowPage.values().size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return FollowPage.values()[position].name.toLowerCase().capitalize()
    }
}