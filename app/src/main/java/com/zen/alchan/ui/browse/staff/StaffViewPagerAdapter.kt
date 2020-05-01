package com.zen.alchan.ui.browse.staff

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class StaffViewPagerAdapter(fm: FragmentManager,
                            private val staffId: Int,
                            private val list: List<Fragment>
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val fragment = list[position]
        val bundle = Bundle()
        bundle.putInt(StaffFragment.STAFF_ID, staffId)
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int {
        return list.size
    }
}