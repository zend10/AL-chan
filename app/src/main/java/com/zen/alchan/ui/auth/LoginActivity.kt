package com.zen.alchan.ui.auth

import android.content.Intent
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
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.Utility
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.ui.MainActivity
import com.zen.alchan.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity(), LoginListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        if (supportFragmentManager.backStackEntryCount == 0) {
            changeFragment(WelcomeFragment(), false)
        }

        if (intent?.data?.encodedFragment != null) {
            val loginFragment = LoginFragment()

            val appLinkData = intent?.data?.encodedFragment!!
            val accessToken = appLinkData.substring("access_token=".length, appLinkData.indexOf("&"))

            val bundle = Bundle()
            bundle.putString(LoginFragment.BUNDLE_ACCESS_TOKEN, accessToken)
            loginFragment.arguments = bundle

            changeFragment(loginFragment)
        }
    }

    override fun changeFragment(targetFragment: Fragment, addToBackStack: Boolean) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(loginFrameLayout.id, targetFragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.commit()
    }
}
