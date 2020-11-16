package com.zen.alchan.ui.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.get
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.firebase.messaging.FirebaseMessaging
import com.zen.alchan.R
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.setFullScreen
import com.zen.alchan.helper.updateAllPadding
import com.zen.alchan.helper.updateBottomPadding
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.helper.utils.Utility
import com.zen.alchan.ui.animelist.AnimeListFragment
import com.zen.alchan.ui.auth.SplashActivity
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.base.BaseMainFragmentListener
import com.zen.alchan.ui.home.HomeFragment
import com.zen.alchan.ui.mangalist.MangaListFragment
import com.zen.alchan.ui.notification.NotificationActivity
import com.zen.alchan.ui.profile.ProfileFragment
import com.zen.alchan.ui.social.SocialFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity(), BaseMainFragmentListener {

    private val viewModel by viewModel<MainViewModel>()

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    companion object {
        const val GO_TO_NOTIFICATION = "goToNotification"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setFullScreen()

        mainBottomNavigation.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateBottomPadding(windowInsets, initialPadding)
        }

        if (intent.getBooleanExtra(GO_TO_NOTIFICATION, false)) {
            // hate it but necessary because of SingleLiveEvent
            val handler = Handler()
            val runnable = { initPage() }
            handler.postDelayed(runnable, 100)
        } else {
            initPage()
        }
    }

    private fun initPage() {
        setupObserver()
        initLayout()

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                return@addOnCompleteListener
            }

            viewModel.sendFirebaseToken(it.result)
        }

        if (intent.getBooleanExtra(GO_TO_NOTIFICATION, false)) {
            intent.removeExtra(GO_TO_NOTIFICATION)
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
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
    }

    override fun onStart() {
        super.onStart()
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

    override fun onDestroy() {
        if (this::handler.isInitialized && this::runnable.isInitialized) {
            handler.removeCallbacks(runnable)
        }
        super.onDestroy()
    }
}
