package com.zen.alchan.ui.profile.reviews

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.secondsToDate
import kotlinx.android.synthetic.main.list_user_review.view.*

class UserReviewsRvAdapter(private val context: Context,
                           private val list: List<UserReviewsQuery.Review>,
                           private val listener: ReviewsListener
) : RecyclerView.Adapter<UserReviewsRvAdapter.ViewHolder>() {

    interface ReviewsListener {
        fun passSelectedReview(reviewId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_user_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.reviewTitle.text = item.media?.title?.userPreferred
        holder.reviewDateText.text = "${item.createdAt?.secondsToDate()}${if (item.updatedAt != null) " (Last Updated on ${item.updatedAt?.secondsToDate()})" else ""}"
        holder.reviewSummaryText.text = item.summary

        holder.reviewUpvoteText.text = item.rating?.toString() ?: "0"
        holder.reviewDownvoteText.text = if (item.ratingAmount == null || item.ratingAmount == 0) "0" else (item.ratingAmount - (item.rating ?: 0)).toString()
        holder.reviewScoreText.text = item.score?.toString() ?: "0"

        holder.itemView.setOnClickListener {
            listener.passSelectedReview(item.id)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reviewTitle = view.reviewTitle!!
        val reviewDateText = view.reviewDateText!!
        val reviewSummaryText = view.reviewSummaryText!!
        val reviewUpvoteText = view.reviewUpvoteText!!
        val reviewDownvoteText = view.reviewDownvoteText!!
        val reviewScoreText = view.reviewScoreText!!
    }
}