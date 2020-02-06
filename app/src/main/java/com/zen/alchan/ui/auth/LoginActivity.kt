package com.zen.alchan.ui.auth

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.Utility
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity() {

    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initLayout()
        setupObserver()
    }

    private fun setupObserver() {

    }

    private fun initLayout() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        GlideApp.with(this).load(R.drawable.welcome_background).into(loginBackgroundImage)

        val anilistText = "anilist.co"
        val explanationText = SpannableString(getString(R.string.you_ll_be_redirected_to_anilist_co_to_login_register_make_sure_the_url_is_anilist_co_before_entering_your_email_and_password))
        val startIndex = explanationText.indexOf(anilistText)
        val endIndex = startIndex + anilistText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                launchBrowser(Constant.ANILIST_URL)
            }
        }

        explanationText.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        loginExplanationText.movementMethod = LinkMovementMethod.getInstance()
        loginExplanationText.text = explanationText

        loginBackLayout.setOnClickListener {
            finish()
        }

        registerButton.setOnClickListener {
            launchBrowser(Constant.ANILIST_REGISTER_URL)
        }

        loginButton.setOnClickListener {
            launchBrowser(Constant.ANILIST_LOGIN_URL)
        }
    }

    private fun launchBrowser(url: String) {
        CustomTabsIntent.Builder()
            .build()
            .launchUrl(this@LoginActivity, Uri.parse(url))
    }
}
