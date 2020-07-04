package com.zen.alchan.ui.browse.media

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import type.MediaType

class MediaViewPagerAdapter(fm: FragmentManager,
                            private val mediaId: Int,
                            private val mediaType: MediaType,
                            private val list: List<Fragment>
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val fragment = list[position]
        val bundle = Bundle()
        bundle.putInt(MediaFragment.MEDIA_ID, mediaId)
        bundle.putString(MediaFragment.MEDIA_TYPE, mediaType.name)
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int {
        return list.size
    }
}