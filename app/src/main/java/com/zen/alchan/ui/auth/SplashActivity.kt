package com.zen.alchan.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.zen.alchan.R
import com.zen.alchan.ui.main.MainActivity
import com.zen.alchan.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {

    private val viewModel by viewModel<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // TODO: implement feature to remind people to update app or even force them to update if needed

        val handler = Handler()
        handler.postDelayed({
            if (viewModel.isLoggedIn) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 500)
    }
}
