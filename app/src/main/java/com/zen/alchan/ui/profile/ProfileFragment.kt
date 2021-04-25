package com.zen.alchan.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private val viewModel by viewModel<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.userId = it.getInt(USER_ID)
        }
    }

    override fun setupObserver() {
        goToLoginButton.setOnClickListener {
            viewModel.logoutAsGuest()
            navigation.navigateToLanding()
        }
    }

    override fun setupLayout() {
        disposables.add(
            viewModel.isAuthenticated.subscribe {
                notLoggedInLayout.show(!it)
            }
        )

        disposables.add(
            viewModel.userData.subscribe {
                usernameText.text = it.name
            }
        )

        viewModel.checkIsAuthenticated()
    }

    companion object {
        private const val USER_ID = "userId"

        @JvmStatic
        fun newInstance(userId: Int = 0) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                }
            }
    }
}