package com.zen.alchan.ui.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
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
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType


class MainFragment : BaseFragment(R.layout.fragment_main) {

    private val viewModel by viewModel<MainViewModel>()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    override fun setupLayout() {
        val fragments = listOf(
            HomeFragment.newInstance(),
            SocialFragment.newInstance(),
            MediaListFragment.newInstance(MediaType.ANIME),
            MediaListFragment.newInstance(MediaType.MANGA),
            ProfileFragment.newInstance()
        )

        mainViewPager.isUserInputEnabled = false
        mainViewPager.adapter = MainViewPagerAdapter(this, fragments)

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
            sharedViewModel.scrollToTop(it.order)
        }
    }

    override fun setupObserver() {
        disposables.add(
            viewModel.isAuthenticated.subscribe {
                mainBottomNavigation.menu.findItem(R.id.menuAnime).isVisible = it
                mainBottomNavigation.menu.findItem(R.id.menuManga).isVisible = it
            }
        )

        viewModel.checkIsAuthenticated()
        viewModel.getViewerData()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}