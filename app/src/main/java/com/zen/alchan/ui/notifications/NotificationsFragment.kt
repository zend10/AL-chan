package com.zen.alchan.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Thread
import com.zen.alchan.data.response.anilist.ThreadComment
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationsFragment : BaseFragment<LayoutInfiniteScrollingBinding, NotificationsViewModel>() {

    private var adapter: NotificationsAdapter? = null

    override val viewModel: NotificationsViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            defaultToolbar.defaultToolbar.apply {
                title = getString(R.string.notifications)
            }

            adapter = NotificationsAdapter(requireContext(), listOf(), AppSetting(), getNotificationsListener())
            infiniteScrollingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            infiniteScrollingRecyclerView.adapter = adapter

            infiniteScrollingSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            infiniteScrollingRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.infiniteScrollingSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.appSetting.subscribe {
                adapter = NotificationsAdapter(requireContext(), listOf(), it, getNotificationsListener())
                binding.infiniteScrollingRecyclerView.adapter = adapter
            },
            viewModel.notifications.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            }
        )

        viewModel.loadData(Unit)
    }

    private fun getNotificationsListener(): NotificationsAdapter.NotificationsListener {
        return object : NotificationsAdapter.NotificationsListener {
            override fun navigateToUser(user: User) {
                navigation.navigateToUser(user.id)
            }

            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }

            override fun navigateToActivity() {

            }

            override fun navigateToThreadComment(threadComment: ThreadComment) {
                navigation.openWebView(threadComment.siteUrl)
            }

            override fun navigateToThread(thread: Thread) {
                navigation.openWebView(thread.siteUrl)
            }

            override fun showDetail(text: String) {
                dialog.showSpoilerDialog(text)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotificationsFragment()
    }
}