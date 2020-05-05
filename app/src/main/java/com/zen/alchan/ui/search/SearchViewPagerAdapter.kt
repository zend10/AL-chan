package com.zen.alchan.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.zen.alchan.helper.enums.BrowsePage

class SearchViewPagerAdapter(fm: FragmentManager,
                             private val list: List<BrowsePage>
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val fragment = SearchListFragment()
        val bundle = Bundle()
        bundle.putString(SearchListFragment.SEARCH_PAGE, list[position].name)
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int {
        return list.size
    }
}