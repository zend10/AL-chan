package com.zen.alchan.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationsFragment : BaseFragment<LayoutInfiniteScrollingBinding, NotificationsViewModel>() {

    override val viewModel: NotificationsViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {

    }

    override fun setUpObserver() {
        viewModel.loadData(Unit)
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationsFragment()
    }
}