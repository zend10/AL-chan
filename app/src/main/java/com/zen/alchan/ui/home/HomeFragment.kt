package com.zen.alchan.ui.home

import com.zen.alchan.R
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.base.NavigationManager
import com.zen.alchan.ui.main.SharedMainViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val viewModel by viewModel<HomeViewModel>()
    private val sharedViewModel by sharedViewModel<SharedMainViewModel>()

    private val sharedDisposables = CompositeDisposable()
    private lateinit var homeAdapter: HomeRvAdapter

    override fun setupLayout() {
        homeAdapter = HomeRvAdapter(requireContext(), listOf(), screenWidth, getHomeListener())
        homeRecyclerView.adapter = homeAdapter

        homeSwipeRefresh.setOnRefreshListener { viewModel.getHomeData(true) }
    }

    override fun setupInsets() {
        homeRecyclerView.applyTopPaddingInsets()
    }

    override fun setupObserver() {
        disposables.add(
            viewModel.error.subscribe {

            }
        )

        disposables.add(
            viewModel.loading.subscribe {
                homeSwipeRefresh.isRefreshing = it
            }
        )

        disposables.add(
            viewModel.homeItemList.subscribe {
                homeAdapter.updateData(it)
            }
        )

//        sharedDisposables.add(
//            sharedViewModel.getScrollToTopObservable(NavigationManager.Page.HOME).subscribe {
//                homeRecyclerView.smoothScrollToPosition(0)
//            }
//        )

        viewModel.getHomeData()
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
            override fun navigateToMedia() {

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

    override fun onDestroy() {
        super.onDestroy()
        sharedDisposables.clear()
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}