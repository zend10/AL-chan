package com.zen.alchan.ui.browse.activity

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
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
import com.zen.alchan.helper.utils.DialogUtility
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.list_activity.view.*
import type.ActivityType

class ActivityListRvAdapter(
    private val context: Context,
    private val list: List<ActivityItem?>,
    private val currentUserId: Int?,
    private val maxWidth: Int,
    private val markwon: Markwon,
    private val listener: ActivityListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_activity, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]
            handleLayout(item!!, holder)
        }
    }

    private fun handleLayout(act: ActivityItem, holder: ItemViewHolder) {
        holder.itemView.setOnClickListener {
            listener.openActivityPage(act.id)
        }

        holder.activityListLayout.setOnClickListener {
            listener.openActivityPage(act.id)
        }

        holder.activityTextLayout.setOnClickListener {
            listener.openActivityPage(act.id)
        }

        holder.activityListLayout.visibility = View.GONE
        holder.activityTextLayout.visibility = View.GONE

        holder.nameText.setOnLongClickListener {
            AndroidUtility.copyToClipboard(context, holder.nameText.text.toString())
            DialogUtility.showToast(context, context.getString(R.string.text_copied))
            true
        }

        holder.recipientNameText.setOnLongClickListener {
            AndroidUtility.copyToClipboard(context, holder.recipientNameText.text.toString())
            DialogUtility.showToast(context, context.getString(R.string.text_copied))
            true
        }

        holder.timeText.text = act.createdAt.secondsToDateTime()

        holder.activityReplyText.text = if (act.replyCount != 0) act.replyCount.toString() else ""
        holder.activityReplyLayout.setOnClickListener {
            listener.openActivityPage(act.id)
        }

        holder.activityLikeIcon.imageTintList = if (act.isLiked == true) {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
        } else {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        }
        holder.activityLikeText.text = if (act.likeCount != 0) act.likeCount.toString() else ""
        holder.activityLikeLayout.setOnClickListener {
            listener.toggleLike(act.id)
        }

        holder.activitySubscribeIcon.imageTintList = if (act.isSubscribed == true) {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
        } else {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        }
        holder.activitySubscribeLayout.setOnClickListener {
            listener.toggleSubscribe(act.id, act.isSubscribed != true)
        }
        holder.activityMoreLayout.setOnClickListener {
            // view pop up menu (edit, delete, view in anilist, copy link)
            val popupMenu = PopupMenu(context, it)
            popupMenu.menuInflater.inflate(R.menu.menu_activity, popupMenu.menu)

            popupMenu.menu.apply {
                findItem(R.id.itemEdit).isVisible = (act is TextActivity && act.userId == currentUserId) ||
                        (act is MessageActivity && act.messengerId == currentUserId)
                findItem(R.id.itemDelete).isVisible = (act is TextActivity && act.userId == currentUserId) ||
                        (act is ListActivity && act.userId == currentUserId) ||
                        (act is MessageActivity && (act.recipientId == currentUserId || act.messengerId == currentUserId))
            }

            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem? ->
                when (menuItem?.itemId) {
                    R.id.itemEdit -> {
                        listener.editActivity(
                            act.id,
                            when (act) {
                                is TextActivity -> act.text ?: ""
                                is MessageActivity -> act.message ?: ""
                                else -> ""
                            },
                            if (act is MessageActivity) act.recipientId else null,
                            if (act is MessageActivity) act.recipient?.name else null
                        )
                    }
                    R.id.itemDelete -> listener.deleteActivity(act.id)
                    R.id.itemViewOnAniList -> listener.viewOnAniList(act.siteUrl)
                    R.id.itemCopyLink -> listener.copyLink(act.siteUrl)
                }
                true
            }
            popupMenu.show()
        }

        when (act) {
            is TextActivity -> handleTextActivityLayout(act, holder)
            is ListActivity -> handleListActivityLayout(act, holder)
            is MessageActivity -> handleMessageActivityLayout(act, holder)
        }
    }

    private fun handleTextActivityLayout(act: TextActivity, holder: ItemViewHolder) {
        holder.nameText.text = act.user?.name
        holder.nameText.setOnClickListener {
            listener.openUserPage(act.userId!!)
        }
        GlideApp.with(context).load(act.user?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
        holder.avatarImage.setOnClickListener {
            listener.openUserPage(act.userId!!)
        }

        AndroidUtility.convertMarkdown(context, holder.activityTextLayout, act.text, maxWidth, markwon)

        holder.activityListLayout.visibility = View.GONE
        holder.activityTextLayout.visibility = View.VISIBLE
        holder.recipientLayout.visibility = View.GONE
        holder.privateLayout.visibility = View.GONE
    }

    private fun handleListActivityLayout(act: ListActivity, holder: ItemViewHolder) {
        holder.nameText.text = act.user?.name
        holder.nameText.setOnClickListener {
            listener.openUserPage(act.userId!!)
        }
        GlideApp.with(context).load(act.user?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
        holder.avatarImage.setOnClickListener {
            listener.openUserPage(act.userId!!)
        }

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
            listener.openMediaPage(act.media?.id!!, act.media.type)
        }

        holder.activityListLayout.visibility = View.VISIBLE
        holder.activityTextLayout.visibility = View.GONE
        holder.recipientLayout.visibility = View.GONE
        holder.privateLayout.visibility = View.GONE
    }

    private fun handleMessageActivityLayout(act: MessageActivity, holder: ItemViewHolder) {
        holder.nameText.text = act.messenger?.name ?: ""
        holder.nameText.setOnClickListener {
            listener.openUserPage(act.messengerId!!)
        }
        GlideApp.with(context).load(act.messenger?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
        holder.avatarImage.setOnClickListener {
            listener.openUserPage(act.messengerId!!)
        }

        holder.recipientLayout.visibility = View.VISIBLE
        holder.recipientNameText.text = act.recipient?.name ?: ""
        holder.recipientNameText.setOnClickListener {
            listener.openUserPage(act.recipientId!!)
        }

        holder.privateLayout.visibility = if (act.isPrivate == true) View.VISIBLE else View.GONE

        AndroidUtility.convertMarkdown(context, holder.activityTextLayout, act.message, maxWidth, markwon)

        holder.activityListLayout.visibility = View.GONE
        holder.activityTextLayout.visibility = View.VISIBLE
        holder.recipientLayout.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        val activityLikeIcon = view.activityLikeIcon!!
        val activityLikeText = view.activityLikeText!!
        val activitySubscribeLayout = view.activitySubscribeLayout!!
        val activitySubscribeIcon = view.activitySubscribeIcon!!
        val activityMoreLayout = view.activityMoreLayout!!

        val recipientLayout = view.recipientLayout!!
        val recipientNameText = view.recipientNameText!!
        val privateLayout = view.privateLayout!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}