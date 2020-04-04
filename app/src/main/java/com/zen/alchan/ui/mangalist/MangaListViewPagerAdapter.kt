package com.zen.alchan.ui.mangalist

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.zen.alchan.helper.pojo.MediaListTabItem
import com.zen.alchan.ui.mangalist.list.MangaListItemFragment

class MangaListViewPagerAdapter(fm: FragmentManager,
                                private val list: List<MediaListTabItem>
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val fragment = MangaListItemFragment()
        val bundle = Bundle()
        bundle.putString(MangaListItemFragment.BUNDLE_ANIME_LIST_STATUS, list[position].status)
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return "${list[position].status} (${list[position].count})"
    }
}