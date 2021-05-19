package com.zen.alchan.ui.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zen.alchan.R
import com.zen.alchan.ui.base.*
import io.reactivex.disposables.CompositeDisposable

class RootActivity : BaseActivity(R.layout.activity_root) {

    val navigationManager: NavigationManager = DefaultNavigationManager(this, supportFragmentManager, R.id.rootLayout)
    val dialogManager: DialogManager = DefaultDialogManager(this)

    override fun setUpLayout() {
        navigationManager.navigateToSplash()
    }

    override fun setUpObserver() {
        val deepLink = intent.data?.encodedFragment
        val accessToken = deepLink?.substring("access_token=".length, deepLink.indexOf("&"))

        if (!accessToken.isNullOrBlank()) {
            intent.data = null
            navigationManager.navigateToLogin(accessToken)
        }
    }
}