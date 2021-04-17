package com.zen.alchan.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


private const val USER_ID = "userId"

class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private val viewModel by viewModel<ProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.userId = it.getInt(USER_ID)
        }
    }

    override fun setupObserver() {

    }

    override fun setupLayout() {

    }

    companion object {
        @JvmStatic
        fun newInstance(userId: Int = 0) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                }
            }
    }
}