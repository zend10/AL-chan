package com.zen.alchan.ui.reviews

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.response.*
import com.zen.alchan.helper.changeStatusBarColor
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.pojo.Review
import com.zen.alchan.helper.updateSidePadding
import com.zen.alchan.helper.updateTopPadding
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseActivity
import com.zen.alchan.ui.browse.BrowseActivity
import kotlinx.android.synthetic.main.activity_reviews.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.ReviewSort

class ReviewsActivity : BaseActivity() {

    private val viewModel by viewModel<ReviewsViewModel>()

    private lateinit var adapter: ReviewsRvAdapter
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        changeStatusBarColor(AndroidUtility.getResValueFromRefAttr(this, R.attr.themeCardColor))

        reviewsLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateSidePadding(windowInsets, initialPadding)
            view.updateTopPadding(windowInsets, initialPadding)
        }

        if (viewModel.selectedSort == null) {
            viewModel.selectedSort = ReviewSort.CREATED_AT_DESC
        }

        setSupportActionBar(toolbarLayout)
        supportActionBar?.apply {
            title = getString(R.string.reviews)
            setDisplayHomeAsUpEnabled(true)
        }

        adapter = assignAdapter()
        reviewsRecyclerView.adapter = adapter

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.reviewsData.observe(this, Observer {
            loadingLayout.visibility = View.GONE
            when (it.responseStatus) {
                ResponseStatus.SUCCESS -> {
                    if (isLoading) {
                        viewModel.reviewsList.removeAt(viewModel.reviewsList.lastIndex)
                        adapter.notifyItemRemoved(viewModel.reviewsList.size)
                        isLoading = false
                    }

                    if (!viewModel.hasNextPage) {
                        return@Observer
                    }

                    viewModel.hasNextPage = it.data?.page?.pageInfo?.hasNextPage ?: false
                    viewModel.page += 1
                    viewModel.isInit = true

                    it.data?.page?.reviews?.forEach { review ->
                        if (review?.user != null && review.media != null) {
                            val user = User(
                                id = review.user.id,
                                name = review.user.name,
                                avatar = UserAvatar(medium = review.user.avatar?.medium, large = null)
                            )
                            val media = Media(
                                id = review.media.id,
                                title = MediaTitle(userPreferred = review.media.title?.userPreferred ?: ""),
                                coverImage = MediaCoverImage(medium = review.media.coverImage?.medium, large = null),
                                bannerImage = review.media.bannerImage
                            )
                            viewModel.reviewsList.add(
                                Review(
                                    id = review.id,
                                    userId = review.userId,
                                    mediaId = review.mediaId,
                                    mediaType = review.mediaType,
                                    summary = review.summary,
                                    rating = review.rating,
                                    ratingAmount = review.ratingAmount,
                                    userRating = review.userRating,
                                    score = review.score,
                                    siteUrl = review.siteUrl,
                                    createdAt = review.createdAt,
                                    updatedAt = review.updatedAt,
                                    user = user,
                                    media = media
                                )
                            )
                        }
                    }

                    adapter.notifyDataSetChanged()
                    emptyLayout.visibility = if (viewModel.reviewsList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                ResponseStatus.ERROR -> {
                    DialogUtility.showToast(this, it.message)
                    if (isLoading) {
                        viewModel.reviewsList.removeAt(viewModel.reviewsList.lastIndex)
                        adapter.notifyItemRemoved(viewModel.reviewsList.size)
                        isLoading = false
                    }
                    emptyLayout.visibility = if (viewModel.reviewsList.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
            }
        })

        if (!viewModel.isInit) {
            loadingLayout.visibility = View.VISIBLE
            viewModel.getReviews()
        }
    }

    private fun initLayout() {
        reviewsRefreshLayout.setOnRefreshListener {
            reviewsRefreshLayout.isRefreshing = false

            isLoading = false
            viewModel.page = 1
            viewModel.hasNextPage = true
            viewModel.reviewsList.clear()
            adapter.notifyDataSetChanged()

            viewModel.getReviews()
            loadingLayout.visibility = View.VISIBLE
        }

        mediaTypeText.text = if (viewModel.selectedMediaType == null) {
            getString(R.string.all).toUpperCase()
        } else {
            viewModel.selectedMediaType?.name
        }

        mediaTypeText.setOnClickListener {
            AlertDialog.Builder(this)
                .setItems(viewModel.mediaTypeArray) { _, which ->
                    viewModel.selectedMediaType = viewModel.mediaTypeList[which]
                    mediaTypeText.text = viewModel.mediaTypeArray[which]

                    isLoading = false
                    viewModel.page = 1
                    viewModel.hasNextPage = true
                    viewModel.reviewsList.clear()
                    adapter.notifyDataSetChanged()

                    viewModel.getReviews()
                    loadingLayout.visibility = View.VISIBLE
                }
                .show()
        }

        sortText.text = viewModel.sortReviewArray[viewModel.sortReviewList.indexOf(viewModel.selectedSort)]

        sortText.setOnClickListener {
            AlertDialog.Builder(this)
                .setItems(viewModel.sortReviewArray) { _, which ->
                    viewModel.selectedSort = viewModel.sortReviewList[which]
                    sortText.text = viewModel.sortReviewArray[which]

                    isLoading = false
                    viewModel.page = 1
                    viewModel.hasNextPage = true
                    viewModel.reviewsList.clear()
                    adapter.notifyDataSetChanged()

                    viewModel.getReviews()
                    loadingLayout.visibility = View.VISIBLE
                }
                .show()
        }

        reviewsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1) && viewModel.isInit && !isLoading) {
                    loadMore()
                    isLoading = true
                }
            }
        })
    }

    private fun loadMore() {
        if (viewModel.hasNextPage) {
            viewModel.reviewsList.add(null)
            adapter.notifyItemInserted(viewModel.reviewsList.lastIndex)
            viewModel.getReviews()
        }
    }

    private fun assignAdapter() : ReviewsRvAdapter {
        return ReviewsRvAdapter(this, viewModel.reviewsList, object : ReviewsRvAdapter.ReviewsListener {
            override fun openReview(id: Int) {
                val intent = Intent(this@ReviewsActivity, BrowseActivity::class.java)
                intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.REVIEW.name)
                intent.putExtra(BrowseActivity.LOAD_ID, id)
                startActivity(intent)
            }

            override fun openUser(userId: Int) {
                val intent = Intent(this@ReviewsActivity, BrowseActivity::class.java)
                intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.USER.name)
                intent.putExtra(BrowseActivity.LOAD_ID, userId)
                startActivity(intent)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}
