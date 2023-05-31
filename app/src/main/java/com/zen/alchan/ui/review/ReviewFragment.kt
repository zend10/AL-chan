package com.zen.alchan.ui.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.Review
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.FragmentReviewBinding
import com.zen.alchan.helper.enums.getString
import com.zen.alchan.helper.enums.getStringResource
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewFragment : BaseFragment<FragmentReviewBinding, ReviewViewModel>() {

    override val viewModel: ReviewViewModel by viewModel()

    private var adapter: ReviewRvAdapter? = null

    private var media: Media? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReviewBinding {
        return FragmentReviewBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.reviews))

            adapter = ReviewRvAdapter(requireContext(), listOf(), AppSetting(), true, true, getReviewListener())
            reviewRecyclerView.adapter = adapter

            media?.let {
                reviewMediaText.isEnabled = false
                reviewMediaText.setTextColor(requireContext().getThemeNegativeColor())
            }

            reviewlSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }

            reviewMediaText.clicks {
                viewModel.loadMediaTypes()
            }

            reviewSortText.clicks {
                viewModel.loadSorts()
            }

            reviewRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        with(binding) {
            defaultToolbar.defaultToolbar.applyTopPaddingInsets()
            reviewRecyclerView.applySidePaddingInsets()
            reviewFilterLayout.applyBottomSidePaddingInsets()
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.reviewlSwipeRefresh.isRefreshing = it
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.emptyLayoutVisibility.subscribe {
                binding.emptyLayout.emptyLayout.show(it)
            },
            viewModel.reviewAdapterComponent.subscribe {
                media?.let { media ->
                    binding.defaultToolbar.defaultToolbar.subtitle = media.getTitle(it.appSetting)
                }

                adapter = ReviewRvAdapter(requireContext(), listOf(), it.appSetting, it.isMediaReview, it.isUserReview, getReviewListener())
                binding.reviewRecyclerView.adapter = adapter
            },
            viewModel.reviews.subscribe {
                adapter?.updateData(it, true)
            },
            viewModel.mediaType.subscribe {
                binding.reviewMediaText.text = it.data?.let {
                    getString(it.getStringResource())
                } ?: getString(R.string.all)
            },
            viewModel.sort.subscribe {
                binding.reviewSortText.text = it.getString(requireContext())
            },
            viewModel.mediaTypes.subscribe {
                dialog.showListDialog(it) { data, index ->
                    viewModel.updateMediaType(data)
                }
            },
            viewModel.sorts.subscribe {
                dialog.showListDialog(it) { data, index ->
                    viewModel.updateSort(data)
                }
            }
        )

        arguments?.let {
            val userId = it.getInt(USER_ID)
            viewModel.loadData(ReviewParam(media, if (userId == 0) null else userId))
        }
    }

    private fun getReviewListener(): ReviewRvAdapter.ReviewListener {
        return object : ReviewRvAdapter.ReviewListener {
            override fun navigateToUser(user: User) {
                navigation.navigateToUser(user.id)
            }

            override fun navigateToReviewReader(review: Review) {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        private const val USER_ID = "userId"

        @JvmStatic
        fun newInstance(media: Media?, userId: Int?) = ReviewFragment().apply {
            this.media = media
            arguments = Bundle().apply {
                userId?.let { putInt(USER_ID, userId) }
            }
        }
    }
}