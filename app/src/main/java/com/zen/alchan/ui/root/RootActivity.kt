package com.zen.alchan.ui.root

import android.content.Intent
import com.zen.alchan.R
import com.zen.alchan.ui.base.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RootActivity : BaseActivity(R.layout.activity_root), ViewContract {

    val navigationManager: NavigationManager = DefaultNavigationManager(this, supportFragmentManager, R.id.rootLayout)
    val dialogManager: DialogManager = DefaultDialogManager(this)

    override fun setupLayout() {
        navigationManager.navigateToSplash()
    }

    override fun setupObserver() {
        val deepLink = intent.data?.encodedFragment
        val accessToken = deepLink?.substring("access_token=".length, deepLink.indexOf("&"))

        if (!accessToken.isNullOrBlank()) {
            intent.data = null
            navigationManager.navigateToLogin(accessToken)
        }
    }
}