package com.zen.alchan.ui.reviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.Review
import com.zen.alchan.helper.secondsToDate
import kotlinx.android.synthetic.main.list_reviews.view.*

class ReviewsRvAdapter(private val context: Context,
                       private val list: List<Review?>,
                       private val listener: ReviewsListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ReviewsListener {
        fun openReview(id: Int)
        fun openUser(userId: Int)
    }

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_reviews, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]!!

            GlideApp.with(context).load(item.media?.bannerImage).into(holder.bannerImage)
            GlideApp.with(context).load(item.user?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)

            holder.reviewNameText.text = context.getString(R.string.review_of_by, item.media?.title?.userPreferred, item.user?.name)
            holder.reviewDateText.text = item.createdAt.secondsToDate()
            holder.reviewThumbsUpText.text = item.rating?.toString() ?: "0"
            holder.reviewThumbsDownText.text = if (item.ratingAmount == null || item.ratingAmount == 0) "0" else (item.ratingAmount!! - (item.rating ?: 0)).toString()

            holder.reviewRatingText.text = item.score?.toString() ?: "0"

            holder.reviewSummaryText.text = item.summary

            holder.avatarImage.setOnClickListener {
                listener.openUser(item.userId)
            }

            holder.itemView.setOnClickListener {
                listener.openReview(item.id)
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
        val avatarImage = view.avatarImage!!
        val bannerImage = view.bannerImage!!
        val reviewNameText = view.reviewNameText!!
        val reviewDateText = view.reviewDateText!!
        val reviewThumbsUpText = view.reviewThumbsUpText!!
        val reviewThumbsDownText = view.reviewThumbsDownText!!
        val reviewRatingText = view.reviewRatingText!!
        val reviewSummaryText = view.reviewSummaryText!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}