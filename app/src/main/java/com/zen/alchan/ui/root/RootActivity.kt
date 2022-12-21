package com.zen.alchan.ui.root

import android.content.Intent
import androidx.lifecycle.Lifecycle
import com.zen.alchan.databinding.ActivityRootBinding
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class RootActivity : BaseActivity<ActivityRootBinding>() {

    override lateinit var dialogManager: DialogManager

    override lateinit var navigationManager: NavigationManager

    private val _incomingDeepLink = PublishSubject.create<DeepLink>()
    override val incomingDeepLink: Observable<DeepLink>
        get() = _incomingDeepLink

    private var newIntent: Intent? = null

    override fun generateViewBinding(): ActivityRootBinding {
        return ActivityRootBinding.inflate(layoutInflater)
    }

    override fun setUpLayout() {
        navigationManager = DefaultNavigationManager(this, supportFragmentManager, binding.rootLayout)
        dialogManager = DefaultDialogManager(this)

        ImageUtil.init(this)

        handleDeepLink(intent)
    }

    override fun setUpObserver() {
        // do nothing
    }

    override fun onResume() {
        super.onResume()
        newIntent?.let {
            handleDeepLink(it)
            newIntent = null
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.data != null) {
            newIntent = intent
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
        if (navigationManager.shouldPopFromBrowseScreen()) {
            navigationManager.popBrowseScreenPage()
            return
        }
        super.onBackPressed()
    }
}