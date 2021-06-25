package com.zen.alchan.ui.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zen.alchan.R
import com.zen.alchan.databinding.ActivityRootBinding
import com.zen.alchan.ui.base.*
import io.reactivex.disposables.CompositeDisposable

class RootActivity : BaseActivity<ActivityRootBinding>() {

    lateinit var navigationManager: NavigationManager
        private set

    lateinit var dialogManager: DialogManager
        private set

    override fun generateViewBinding(): ActivityRootBinding {
        return ActivityRootBinding.inflate(layoutInflater)
    }

    override fun setUpLayout() {
        navigationManager = DefaultNavigationManager(this, supportFragmentManager, binding.rootLayout)
        dialogManager = DefaultDialogManager(this)

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