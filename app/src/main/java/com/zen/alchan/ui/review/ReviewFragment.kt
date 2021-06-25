package com.zen.alchan.ui.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentReviewBinding
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewFragment : BaseFragment<FragmentReviewBinding, ReviewViewModel>() {

    override val viewModel: ReviewViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReviewBinding {
        return FragmentReviewBinding.inflate(inflater, container, false)
    }

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