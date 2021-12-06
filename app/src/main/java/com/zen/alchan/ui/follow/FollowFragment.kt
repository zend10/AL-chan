package com.zen.alchan.ui.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.LayoutInfiniteScrollingBinding
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FollowFragment : BaseFragment<LayoutInfiniteScrollingBinding, FollowViewModel>() {

    override val viewModel: FollowViewModel by viewModel()

    private var adapter: FollowRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutInfiniteScrollingBinding {
        return LayoutInfiniteScrollingBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(
                defaultToolbar.defaultToolbar,
                if (arguments?.getBoolean(IS_FOLLOWING) == true) getString(R.string.following) else getString(R.string.followers)
            )

            adapter = FollowRvAdapter(requireContext(), listOf(), AppSetting(), getFollowListener())
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
            viewModel.followAdapterComponent.subscribe {
                adapter = FollowRvAdapter(requireContext(), listOf(), it, getFollowListener())
                binding.infiniteScrollingRecyclerView.adapter = adapter
            },
            viewModel.users.subscribe {
                adapter?.updateData(it)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            }
        )

        viewModel.loadData(arguments?.getInt(USER_ID) ?: 0, arguments?.getBoolean(IS_FOLLOWING) ?: false)
    }

    private fun getFollowListener(): FollowRvAdapter.FollowListener {
        return object : FollowRvAdapter.FollowListener {
            override fun navigateToUser(user: User) {
                navigation.navigateToUser(user.id)
            }

            override fun toggleFollow(user: User) {

            }

            override fun toggleUnfollow(user: User) {

            }

            override fun viewOnAniList(user: User) {
                navigation.openWebView(user.siteUrl)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
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