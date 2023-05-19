package com.zen.alchan.ui.review

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class ReviewRvAdapter(
    private val context: Context,
    list: List<Review?>,
    private val appSetting: AppSetting,
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
                ImageUtil.loadCircleImage(context, item.user.avatar.getImageUrl(appSetting), reviewUserAvatar)
                ImageUtil.loadImage(context, item.media.bannerImage, reviewMediaBanner)
                reviewName.text = context.getString(R.string.review_of_x_by_y, "${item.media.getTitle(appSetting)} (${item.media.format?.getString()})", item.user.name)
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