package com.zen.alchan.ui.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseFragment

class ReviewFragment : BaseFragment(R.layout.fragment_review) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun setUpLayout() {

    }

    override fun setUpObserver() {

    }

    companion object {
        private const val IS_USER_PROFILE = "isUserProfile"
        private const val USER_ID = "userId"

        @JvmStatic
        fun newInstance(isUserProfile: Boolean, userId: Int = 0) =
            ReviewFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_USER_PROFILE, isUserProfile)
                    putInt(USER_ID, userId)
                }
            }
    }
}