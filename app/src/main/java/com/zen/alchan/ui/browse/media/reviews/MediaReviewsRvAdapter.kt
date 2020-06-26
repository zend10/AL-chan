package com.zen.alchan.ui.browse.media.reviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.secondsToDate
import com.zen.alchan.helper.secondsToDateTime
import kotlinx.android.synthetic.main.list_media_reviews.view.*

class MediaReviewsRvAdapter(private val context: Context,
                            private val list: List<MediaReviewsQuery.Node?>,
                            private val listener: MediaReviewsListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface MediaReviewsListener {
        fun passSelectedReview(reviewId: Int)
    }

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_reviews, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]

            holder.reviewUsernameText.text = item?.user?.name
            holder.reviewCreatedDateText.text = item?.createdAt?.secondsToDate()
            GlideApp.with(context).load(item?.user?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.reviewUserAvatar)

            holder.reviewSummaryText.text = item?.summary

            holder.reviewUpvoteText.text = item?.rating?.toString() ?: "0"
            holder.reviewDownvoteText.text = if (item?.ratingAmount == null || item.ratingAmount == 0) "0" else (item.ratingAmount - (item.rating ?: 0)).toString()
            holder.reviewScoreText.text = item?.score?.toString() ?: "0"

            holder.itemView.setOnClickListener {
                listener.passSelectedReview(item?.id!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reviewUserAvatar = view.reviewUserAvatar!!
        val reviewUsernameText = view.reviewUsernameText!!
        val reviewCreatedDateText = view.reviewCreatedDateText!!
        val reviewSummaryText = view.reviewSummaryText!!
        val reviewUpvoteIcon = view.reviewUpvoteIcon!!
        val reviewUpvoteText = view.reviewUpvoteText!!
        val reviewDownvoteIcon = view.reviewDownvoteIcon!!
        val reviewDownvoteText = view.reviewDownvoteText!!
        val reviewScoreText = view.reviewScoreText
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}