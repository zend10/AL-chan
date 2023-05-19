package com.zen.alchan.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.databinding.FragmentHomeBinding
import com.zen.alchan.helper.enums.MediaType
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
            homeAdapter = HomeRvAdapter(requireContext(), listOf(), null, AppSetting(), screenWidth, getHomeListener())
            homeRecyclerView.adapter = homeAdapter

            homeSwipeRefresh.setOnRefreshListener { viewModel.reloadData() }
        }
    }

    override fun setUpInsets() {

    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.loading.subscribe {
                binding.homeSwipeRefresh.isRefreshing = it
            },
            viewModel.adapterComponent.subscribe {
                homeAdapter = HomeRvAdapter(requireContext(), listOf(), it.user, it.appSetting, screenWidth, getHomeListener())
                binding.homeRecyclerView.adapter = homeAdapter
            },
            viewModel.homeItemList.subscribe {
                homeAdapter?.updateData(it)
            },
            viewModel.searchCategoryList.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    navigation.navigateToExplore(data)
                }
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
            override val socialListener: HomeListener.SocialListener = getSocialListener()
            override val trendingMediaListener: HomeListener.TrendingMediaListener = getTrendingMediaListener()
            override val newMediaListener: HomeListener.NewMediaListener = getNewMediaListener()
            override val recentReviewsListener: HomeListener.RecentReviewsListener = getRecentReviewsListener()
        }
    }

    private fun getHeaderListener(): HomeListener.HeaderListener {
        return object : HomeListener.HeaderListener {
            override fun navigateToSearch() {
                navigation.navigateToSearch()
            }
        }
    }

    private fun getHomeMenuListener(): HomeListener.MenuListener {
        return object : HomeListener.MenuListener {
            override fun navigateToSeasonal() {
                navigation.navigateToSeasonal()
            }

            override fun showExploreDialog() {
                viewModel.loadSearchCategories()
            }

            override fun navigateToReview() {
                navigation.navigateToReview()
            }

            override fun navigateToCalendar() {
                navigation.navigateToCalendar()
            }
        }
    }

    private fun getReleasingTodayListener(): HomeListener.ReleasingTodayListener {
        return object : HomeListener.ReleasingTodayListener {
            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }

            override fun navigateToListEditor(mediaList: MediaList) {
                navigation.navigateToEditor(mediaList.media.getId(), true)
            }

            override fun showProgressDialog(mediaList: MediaList) {
                dialog.showProgressDialog(MediaType.ANIME, mediaList.progress, mediaList.media.episodes, false) {
                    viewModel.updateProgress(mediaList, it)
                }
            }
        }
    }

    private fun getSocialListener(): HomeListener.SocialListener {
        return object : HomeListener.SocialListener {
            override fun navigateToSocial() {
                navigation.navigateToSocial()
            }
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