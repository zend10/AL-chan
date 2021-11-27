package com.zen.alchan.ui.follow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentFollowBinding
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FollowFragment : BaseFragment<FragmentFollowBinding, FollowViewModel>() {

    override val viewModel: FollowViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFollowBinding {
        return FragmentFollowBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {

    }

    override fun setUpObserver() {

    }

    companion object {
        private const val USER_ID = "userId"
        private const val IS_FOLLOWING = "isFollowing"
        @JvmStatic
        fun newInstance(userId: Int, isFollowing: Boolean) =
            FollowFragment().apply {
                arguments = Bundle().apply {
                    putInt(USER_ID, userId)
                    putBoolean(IS_FOLLOWING, isFollowing)
                }
            }
    }
}