package com.zen.alchan.ui.social

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.ActivityItem
import com.zen.alchan.helper.pojo.ListActivity
import com.zen.alchan.helper.pojo.MessageActivity
import com.zen.alchan.helper.pojo.TextActivity
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.secondsToDateTime
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_activity.view.*
import org.koin.core.logger.MESSAGE
import type.ActivityType

class FriendsActivityRvAdapter(private val context: Context,
                               private val list: List<ActivityItem>,
                               private val listener: ActivityListener
): RecyclerView.Adapter<FriendsActivityRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_activity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        handleLayout(item, holder)
    }

    private fun handleLayout(act: ActivityItem, holder: ViewHolder) {
        holder.itemView.setOnClickListener {
            // open activity page
        }

        holder.activityListLayout.visibility = View.GONE
        holder.activityTextLayout.visibility = View.GONE

        holder.avatarImage.setOnClickListener {
            // open user page
        }
        holder.timeText.text = act.createdAt.secondsToDateTime()

        holder.activityReplyText.text = if (act.replyCount != 0) act.replyCount.toString() else ""
        holder.activityReplyLayout.setOnClickListener {
            // view all replies
        }
        holder.activityLikeText.text = if (act.likeCount != 0) act.likeCount.toString() else ""
        holder.activityLikeLayout.setOnClickListener {
            // toggle like
        }
        holder.activitySubscribeIcon.imageTintList = if (act.isSubscribed == true) {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
        } else {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        }
        holder.activitySubscribeLayout.setOnClickListener {
            // toggle subscribe
        }
        holder.activityMoreLayout.setOnClickListener {
            // view pop up menu (edit, delete, view in anilist, copy link, view all likes
        }

        when (act) {
            is TextActivity -> handleTextActivityLayout(act, holder)
            is ListActivity -> handleListActivityLayout(act, holder)
            is MessageActivity -> { }
        }
    }

    private fun handleTextActivityLayout(act: TextActivity, holder: ViewHolder) {
        holder.nameText.text = act.user?.name
        GlideApp.with(context).load(act.user?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
        holder.activityTextLayout.text = act.text ?: ""

        holder.activityListLayout.visibility = View.GONE
        holder.activityTextLayout.visibility = View.VISIBLE
    }

    private fun handleListActivityLayout(act: ListActivity, holder: ViewHolder) {
        holder.nameText.text = act.user?.name
        GlideApp.with(context).load(act.user?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)

        holder.listActivityText.text = "${act.status?.capitalize()}${if (act.progress != null) " ${act.progress} of " else " "}${act.media?.title?.userPreferred}"
        holder.mediaTitleText.text = act.media?.title?.userPreferred
        GlideApp.with(context).load(act.media?.coverImage?.medium).into(holder.mediaImage)
        holder.mediaYearText.text = if (act.media?.startDate?.year != null) {
            act.media.startDate.year.toString()
        } else {
            "TBA"
        }
        holder.mediaTypeText.text = act.media?.type?.name
        if (act.media?.format != null) {
            holder.mediaFormatDivider.visibility = View.VISIBLE
            holder.mediaFormatText.text = act.media.format.name.replaceUnderscore()
        } else {
            holder.mediaFormatDivider.visibility = View.GONE
            holder.mediaFormatText.text = ""
        }
        holder.mediaLayout.setOnClickListener {
            // visit media page
        }

        holder.activityListLayout.visibility = View.VISIBLE
        holder.activityTextLayout.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarImage = view.avatarImage!!
        val nameText = view.nameText!!
        val timeText = view.timeText!!

        val activityListLayout = view.activityListLayout!!
        val listActivityText = view.listActivityText!!
        val mediaLayout = view.mediaLayout!!
        val mediaImage = view.mediaImage!!
        val mediaTitleText = view.mediaTitleText!!
        val mediaYearText = view.mediaYearText!!
        val mediaTypeText = view.mediaTypeText!!
        val mediaFormatDivider = view.mediaFormatDivider!!
        val mediaFormatText = view.mediaFormatText!!

        val activityTextLayout = view.activityTextLayout!!

        val activityReplyLayout = view.activityReplyLayout!!
        val activityReplyText = view.activityReplyText!!
        val activityLikeLayout = view.activityLikeLayout!!
        val activityLikeText = view.activityLikeText!!
        val activitySubscribeLayout = view.activitySubscribeLayout!!
        val activitySubscribeIcon = view.activitySubscribeIcon!!
        val activityMoreLayout = view.activityMoreLayout!!
    }
}