package com.zen.alchan.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {

    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val handler = Handler()
        handler.postDelayed({
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }, 2000)
    }
}
