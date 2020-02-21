package com.zen.alchan.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.zen.alchan.R
import com.zen.alchan.ui.animelist.AnimeListFragment
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.base.BaseMainFragmentListener
import com.zen.alchan.ui.home.HomeFragment
import com.zen.alchan.ui.main.MainViewPagerAdapter
import com.zen.alchan.ui.mangalist.MangaListFragment
import com.zen.alchan.ui.profile.ProfileFragment
import com.zen.alchan.ui.social.SocialFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), BaseMainFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLayout()
    }

    private fun initLayout() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

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
