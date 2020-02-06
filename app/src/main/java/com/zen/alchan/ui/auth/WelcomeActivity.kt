package com.zen.alchan.ui.auth


import android.content.Intent
import android.os.Bundle
import android.view.View
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        GlideApp.with(this).load(R.drawable.welcome_background).into(welcomeBackgroundImage)

        getStartedLayout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
