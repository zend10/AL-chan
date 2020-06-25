package com.zen.alchan.ui.profile.reviews


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.zen.alchan.R
import com.zen.alchan.data.network.Resource
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.browse.user.UserFragment
import kotlinx.android.synthetic.main.fragment_user_reviews.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class UserReviewsFragment : BaseFragment() {

    private val viewModel by viewModel<UserReviewsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_reviews, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null && arguments?.getInt(UserFragment.USER_ID) != null && arguments?.getInt(UserFragment.USER_ID) != 0) {
            viewModel.otherUserId = arguments?.getInt(UserFragment.USER_ID)
        }

        setupObserver()
    }

    private fun setupObserver() {
        viewModel.getReviewsObserver().observe(viewLifecycleOwner, Observer {
            handleReviewResponse(it)
        })

        viewModel.getTriggerRefreshObserver().observe(viewLifecycleOwner, Observer {
            viewModel.refresh()
        })

        if (!viewModel.isInit) {
            viewModel.getReviews()
        } else {
            profileReviewsRecyclerView.adapter = assignAdapter()
            emptyLayout.visibility = if (viewModel.userReviews.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun handleReviewResponse(it: Resource<UserReviewsQuery.Data>) {
        when (it.responseStatus) {
            ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
            ResponseStatus.SUCCESS -> {
                if (!viewModel.hasNextPage) {
                    return
                }

                viewModel.hasNextPage = it.data?.page?.pageInfo?.hasNextPage ?: false
                viewModel.page += 1
                viewModel.isInit = true
                it.data?.page?.reviews?.forEach { review ->
                    viewModel.userReviews.add(review!!)
                }

                if (viewModel.hasNextPage) {
                    viewModel.getReviews()
                } else {
                    loadingLayout.visibility = View.GONE
                    profileReviewsRecyclerView.adapter = assignAdapter()
                    emptyLayout.visibility = if (viewModel.userReviews.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
            }
            ResponseStatus.ERROR -> {
                loadingLayout.visibility = View.GONE
                DialogUtility.showToast(activity, it.message)
                emptyLayout.visibility = if (viewModel.userReviews.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun assignAdapter(): UserReviewsRvAdapter {
        return UserReviewsRvAdapter(activity!!, viewModel.userReviews, object : UserReviewsRvAdapter.ReviewsListener {
            override fun passSelectedReview(reviewId: Int) {
                if (viewModel.otherUserId != null) {
                    listener?.changeFragment(BrowsePage.REVIEW, reviewId)
                } else {
                    val intent = Intent(activity, BrowseActivity::class.java)
                    intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.REVIEW.name)
                    intent.putExtra(BrowseActivity.LOAD_ID, reviewId)
                    startActivity(intent)
                }
            }
        })
    }
}
