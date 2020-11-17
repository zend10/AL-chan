package com.zen.alchan.ui.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.base.BaseListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), BaseListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
