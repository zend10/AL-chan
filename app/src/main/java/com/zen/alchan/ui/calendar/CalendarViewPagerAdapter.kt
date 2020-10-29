package com.zen.alchan.ui.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.zen.alchan.helper.pojo.DateItem

class CalendarViewPagerAdapter(fm: FragmentManager,
                               private val list: List<DateItem>
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val fragment = CalendarFragment()
        val bundle = Bundle()
        bundle.putLong(CalendarFragment.START_DATE, list[position].dateTimestamp)
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int {
        return list.size
    }
}