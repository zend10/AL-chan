package com.zen.alchan.ui.review

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateMargins
import androidx.core.view.updateMarginsRelative
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Review
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.ListLoadingBinding
import com.zen.alchan.databinding.ListReviewBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.getNumberFormatting
import com.zen.alchan.helper.extensions.getString
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class ReviewRvAdapter(
    private val context: Context,
    list: List<Review?>,
    private val appSetting: AppSetting,
    private val isMediaReview: Boolean,
    private val isUserReview: Boolean,
    private val listener: ReviewListener
) : BaseRecyclerViewAdapter<Review?, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = ListLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = ListReviewBinding.inflate(inflater, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    inner class ItemViewHolder(private val binding: ListReviewBinding) : ViewHolder(binding) {
        override fun bind(item: Review?, index: Int) {
            if (item == null)
                return

            with(binding) {

                val constraintLayout = reviewInfoLayout
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                constraintSet.connect(reviewName.id, ConstraintSet.START, if (isUserReview) ConstraintSet.PARENT_ID else reviewAvatarGuideline.id, ConstraintSet.START)
                constraintSet.applyTo(constraintLayout)

                val layoutParams = reviewName.layoutParams as ConstraintLayout.LayoutParams
                if (isMediaReview) {
                    reviewMediaBanner.show(false)
                    reviewUserAvatar.show(true)
                    layoutParams.updateMarginsRelative(start = context.resources.getDimensionPixelSize(R.dimen.marginNormal))
                    reviewName.text = context.getString(R.string.review_by_x, item.user.name)
                } else if (isUserReview) {
                    reviewMediaBanner.show(true)
                    reviewUserAvatar.show(false)
                    layoutParams.updateMarginsRelative(start = 0)
                    reviewName.text = context.getString(R.string.review_of_x, "${item.media.getTitle(appSetting)} (${item.media.format?.getString()})")
                } else {
                    reviewMediaBanner.show(true)
                    reviewUserAvatar.show(true)
                    layoutParams.updateMarginsRelative(start = context.resources.getDimensionPixelSize(R.dimen.marginNormal))
                    reviewName.text = context.getString(R.string.review_of_x_by_y, "${item.media.getTitle(appSetting)} (${item.media.format?.getString()})", item.user.name)
                }
                reviewName.layoutParams = layoutParams

                ImageUtil.loadCircleImage(context, item.user.avatar.getImageUrl(appSetting), reviewUserAvatar)
                ImageUtil.loadImage(context, item.media.bannerImage, reviewMediaBanner)
                reviewDate.text = TimeUtil.displayInDateFormat(item.createdAt)
                reviewSummary.text = item.summary
                reviewUpvote.text = item.rating.getNumberFormatting()
                reviewDownvote.text = (item.ratingAmount - item.rating).getNumberFormatting()
                reviewScore.text = item.score.getNumberFormatting()
                reviewUserAvatar.clicks {
                    listener.navigateToUser(item.user)
                }
                root.clicks {
                    listener.navigateToReviewReader(item)
                }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ListLoadingBinding) : ViewHolder(binding) {
        override fun bind(item: Review?, index: Int) {
            // do nothing
        }
    }

    interface ReviewListener {
        fun navigateToUser(user: User)
        fun navigateToReviewReader(review: Review)
    }
}