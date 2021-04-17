package com.zen.alchan.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.NavigationManager
import com.zen.alchan.ui.home.HomeFragment
import com.zen.alchan.ui.medialist.MediaListFragment
import com.zen.alchan.ui.profile.ProfileFragment
import com.zen.alchan.ui.social.SocialFragment
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType


class MainFragment : BaseFragment(R.layout.fragment_main) {

    private val viewModel by viewModel<MainViewModel>()

    override fun setupObserver() {

    }

    override fun setupLayout() {
        val fragmentList = listOf(
            HomeFragment.newInstance(),
            SocialFragment.newInstance(),
            MediaListFragment.newInstance(MediaType.ANIME),
            MediaListFragment.newInstance(MediaType.MANGA),
            ProfileFragment.newInstance()
        )

        mainViewPager.adapter = MainViewPagerAdapter(this, fragmentList)

        mainViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mainBottomNavigation.menu[position].isChecked = true
            }
        })

        mainBottomNavigation.setOnNavigationItemSelectedListener {
            mainViewPager.setCurrentItem(it.order, true)
            true
        }

        mainBottomNavigation.setOnNavigationItemReselectedListener {
            mainViewPager.setCurrentItem(it.order, true)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}