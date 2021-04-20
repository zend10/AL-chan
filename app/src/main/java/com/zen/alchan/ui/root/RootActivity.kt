package com.zen.alchan.ui.root

import android.content.Intent
import com.zen.alchan.R
import com.zen.alchan.ui.base.*

class RootActivity : BaseActivity(R.layout.activity_root), ViewContract {

    val navigationManager: NavigationManager = DefaultNavigationManager(this, supportFragmentManager, R.id.rootLayout)
    val dialogManager: DialogManager = DefaultDialogManager(this)

    override fun setupLayout() {
        val appLinkData = intent?.data?.encodedFragment
        val accessToken = appLinkData?.substring("access_token=".length, appLinkData.indexOf("&"))

        if (!accessToken.isNullOrBlank())
            navigationManager.navigate(NavigationManager.Page.LOGIN, listOf(accessToken))
        else
            navigationManager.navigate(NavigationManager.Page.SPLASH)
    }

    override fun setupObserver() {

    }
}