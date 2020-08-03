package com.zen.alchan.ui.browse.user.follows


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.profile.follows.FollowsViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_user_follows.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class UserFollowsFragment : BaseFragment() {

    companion object {
        const val USER_ID = "userId"
        const val START_POSITION = "startPosition"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_follows, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        followsToolbar.apply {
            title = getString(R.string.friends)
            setNavigationOnClickListener { activity?.onBackPressed() }
            navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_arrow_back)
        }

        followsViewPager.adapter = FollowsViewPagerAdapter(childFragmentManager, arguments?.getInt(USER_ID))
        followsTabLayout.setupWithViewPager(followsViewPager)
        followsViewPager.currentItem = arguments?.getInt(START_POSITION, 0)!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        followsViewPager.adapter = null
    }
}
