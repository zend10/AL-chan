package com.zen.alchan.ui.root

import android.content.Intent
import com.zen.alchan.databinding.ActivityRootBinding
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.ui.base.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RootActivity : BaseActivity<ActivityRootBinding>() {

    lateinit var navigationManager: NavigationManager
        private set

    lateinit var dialogManager: DialogManager
        private set

    private val _incomingDeepLink = PublishSubject.create<DeepLink>()
    val incomingDeepLink: Observable<DeepLink>
        get() = _incomingDeepLink

    override fun generateViewBinding(): ActivityRootBinding {
        return ActivityRootBinding.inflate(layoutInflater)
    }

    override fun setUpLayout() {
        navigationManager = DefaultNavigationManager(this, supportFragmentManager, binding.rootLayout)
        dialogManager = DefaultDialogManager(this)

        handleDeepLink(intent)
    }

    override fun setUpObserver() {
        // do nothing
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.data != null) {
            handleDeepLink(intent)
        }
    }

    private fun handleDeepLink(intent: Intent) {
        val deepLink = DeepLink(intent.data)

        when {
            supportFragmentManager.fragments.isEmpty() -> {
                navigationManager.navigateToSplash(deepLink, intent.getBooleanExtra("RESTART", false))
            }
            navigationManager.isAtPreLoginScreen() -> {
                if (deepLink.isLogin()) {
                    val fullDeepLink = deepLink.uri?.encodedFragment
                    val accessToken = fullDeepLink?.substring("access_token=".length, fullDeepLink.indexOf("&"))
                    navigationManager.navigateToLogin(accessToken, true)
                } else {
                    navigationManager.navigateToMain(deepLink)
                }
            }
            deepLink.uri != null -> {
                _incomingDeepLink.onNext(deepLink)
            }
        }

        intent.data = null
    }

    override fun onBackPressed() {
        if (navigationManager.isAtBrowseScreen()) {
            navigationManager.popBrowseScreenPage()
            return
        }
        super.onBackPressed()
    }
}