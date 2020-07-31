package com.zen.alchan.ui.main

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.zen.alchan.R
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.helper.utils.Utility
import com.zen.alchan.ui.animelist.AnimeListFragment
import com.zen.alchan.ui.auth.SplashActivity
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
            if (Utility.isLightTheme(viewModel.appColorTheme)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            recreate()
        })

        viewModel.listOrAniListSettingsChanged.observe(this, Observer {
            recreate()
        })

        viewModel.notificationCount.observe(this, Observer {
            if (it != 0) {
                mainBottomNavigation.getOrCreateBadge(R.id.itemProfile).number = it
            } else {
                mainBottomNavigation.removeBadge(R.id.itemProfile)
            }
        })

        viewModel.sessionResponse.observe(this, Observer {
            if (!it) {
                DialogUtility.showForceActionDialog(
                    this,
                    R.string.you_are_logged_out,
                    R.string.your_session_has_ended,
                    R.string.logout
                ) {
                    viewModel.clearStorage()
                    startActivity(Intent(this, SplashActivity::class.java))
                    finish()
                }
            }
        })

        viewModel.checkSession()
        viewModel.getNotificationCount()
    }

    private fun initLayout() {
        val fragmentList = listOf(HomeFragment(), AnimeListFragment(), MangaListFragment(), SocialFragment(), ProfileFragment())

        mainViewPager.setPagingEnabled(true)
        mainViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) { mainBottomNavigation.menu[position].isChecked = true }
        })
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
