package com.zen.alchan.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.zen.alchan.ui.browse.user.UserFragment

class ProfileViewPagerAdapter(fm: FragmentManager,
                              private val list: List<Fragment>,
                              private val userId: Int? = null
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val fragment = list[position]
        if (userId != null) {
            val bundle = Bundle()
            bundle.putInt(UserFragment.USER_ID, userId)
            fragment.arguments = bundle
        }
        return fragment
    }

    override fun getCount(): Int {
        return list.size
    }
}