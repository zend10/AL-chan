package com.zen.alchan.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.FragmentHomeBinding
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.main.SharedMainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private var homeAdapter: HomeRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            homeAdapter = HomeRvAdapter(requireContext(), listOf(), screenWidth, getHomeListener())
            homeRecyclerView.adapter = homeAdapter

            homeSwipeRefresh.setOnRefreshListener { viewModel.reloadData() }
        }
    }

    override fun setUpInsets() {
        binding.homeRecyclerView.applyTopPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.error.subscribe {
                dialog.showToast(R.string.app_name)
            }
        )

        disposables.add(
            viewModel.loading.subscribe {
                binding.homeSwipeRefresh.isRefreshing = it
            }
        )

        disposables.add(
            viewModel.homeItemList.subscribe {
                homeAdapter?.updateData(it)
            }
        )

        sharedDisposables.add(
            sharedViewModel.getScrollToTopObservable(SharedMainViewModel.Page.HOME).subscribe {
                binding.homeRecyclerView.smoothScrollToPosition(0)
            }
        )

        viewModel.loadData(Unit)
    }

    private fun getHomeListener(): HomeListener {
        return object : HomeListener {
            override val headerListener: HomeListener.HeaderListener = getHeaderListener()
            override val menuListener: HomeListener.MenuListener = getHomeMenuListener()
            override val releasingTodayListener: HomeListener.ReleasingTodayListener = getReleasingTodayListener()
            override val trendingMediaListener: HomeListener.TrendingMediaListener = getTrendingMediaListener()
            override val newMediaListener: HomeListener.NewMediaListener = getNewMediaListener()
            override val recentReviewsListener: HomeListener.RecentReviewsListener = getRecentReviewsListener()
        }
    }

    private fun getHeaderListener(): HomeListener.HeaderListener {
        return object : HomeListener.HeaderListener {
            override fun navigateToSearch() {

            }
        }
    }

    private fun getHomeMenuListener(): HomeListener.MenuListener {
        return object : HomeListener.MenuListener {
            override fun navigateToSeasonal() {

            }

            override fun showExploreDialog() {

            }

            override fun navigateToReviews() {

            }

            override fun navigateToCalendar() {

            }
        }
    }

    private fun getReleasingTodayListener(): HomeListener.ReleasingTodayListener {
        return object : HomeListener.ReleasingTodayListener {

        }
    }

    private fun getTrendingMediaListener(): HomeListener.TrendingMediaListener {
        return object : HomeListener.TrendingMediaListener {
            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }
        }
    }

    private fun getNewMediaListener(): HomeListener.NewMediaListener {
        return object : HomeListener.NewMediaListener {

        }
    }

    private fun getRecentReviewsListener(): HomeListener.RecentReviewsListener {
        return object : HomeListener.RecentReviewsListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}