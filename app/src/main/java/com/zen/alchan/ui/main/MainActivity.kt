package com.zen.alchan.ui.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.zen.alchan.R
import com.zen.alchan.ui.animelist.AnimeListFragment
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.base.BaseMainFragmentListener
import com.zen.alchan.ui.home.HomeFragment
import com.zen.alchan.ui.mangalist.MangaListFragment
import com.zen.alchan.ui.profile.ProfileFragment
import com.zen.alchan.ui.social.SocialFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity(), BaseMainFragmentListener {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.appColorThemeLiveData.observe(this, Observer {
            recreate()
        })

        viewModel.listOrAniListSettingsChanged.observe(this, Observer {
            recreate()
        })

        viewModel.shouldLoading.observe(this, Observer {
            if (it) {
                loadingLayout.visibility = View.VISIBLE
            } else {
                loadingLayout.visibility = View.GONE
            }
        })
    }

    private fun initLayout() {
        val fragmentList = listOf(HomeFragment(), AnimeListFragment(), MangaListFragment(), SocialFragment(), ProfileFragment())

        mainViewPager.setPagingEnabled(false)
        mainViewPager.offscreenPageLimit = fragmentList.size
        mainViewPager.adapter = MainViewPagerAdapter(supportFragmentManager, fragmentList)

        mainBottomNavigation.setOnNavigationItemSelectedListener {
            mainViewPager.currentItem = mainBottomNavigation.menu.findItem(it.itemId).order
            true
        }
    }

    override fun changeMenu(targetMenuId: Int) {
        mainBottomNavigation.selectedItemId = targetMenuId
    }
}
