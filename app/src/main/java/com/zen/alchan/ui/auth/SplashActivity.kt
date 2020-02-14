package com.zen.alchan.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.R
import com.zen.alchan.helper.DialogUtility
import com.zen.alchan.helper.Utility
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.ui.MainActivity
import com.zen.alchan.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {

    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // TODO: implement feature to remind people to update app or even force them to update if needed

        if (viewModel.isLoggedIn && !viewModel.shouldRetrieveViewerData) {
            val handler = Handler()
            handler.postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 2000)
        }

        if (viewModel.isLoggedIn && viewModel.shouldRetrieveViewerData) {
            setupObserver()
            viewModel.getViewerData()
        }

        if (!viewModel.isLoggedIn) {
            val handler = Handler()
            handler.postDelayed({
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, 2000)
        }
    }

    private fun setupObserver() {
        viewModel.viewerDataResponse.observe(this, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> {}
                ResponseStatus.SUCCESS -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                ResponseStatus.ERROR -> {
                    // if error, enter anyway and show old stored data
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        })
    }
}
