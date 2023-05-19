package com.zen.alchan.ui.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Review
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.FragmentReviewBinding
import com.zen.alchan.helper.enums.getString
import com.zen.alchan.helper.enums.getStringResource
import com.zen.alchan.helper.extensions.applyBottomSidePaddingInsets
import com.zen.alchan.helper.extensions.applySidePaddingInsets
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReviewFragment : BaseFragment<FragmentReviewBinding, ReviewViewModel>() {

    override val viewModel: ReviewViewModel by viewModel()

    private var adapter: ReviewRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentReviewBinding {
        return FragmentReviewBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        with(binding) {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.reviews))

            adapter = ReviewRvAdapter(requireContext(), listOf(), AppSetting(), getReviewListener())
            reviewRecyclerView.adapter = adapter

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
            viewModel.appSetting.subscribe {
                adapter = ReviewRvAdapter(requireContext(), listOf(), it, getReviewListener())
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

        viewModel.loadData(Unit)
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
        @JvmStatic
        fun newInstance() = ReviewFragment()
    }
}