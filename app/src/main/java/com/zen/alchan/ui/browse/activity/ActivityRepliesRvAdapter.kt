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
import com.zen.alchan.data.response.User
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.ActivityReply
import com.zen.alchan.helper.secondsToDateTime
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.list_activity_replies.view.*

class ActivityRepliesRvAdapter(private val context: Context,
                               private val list: List<ActivityReply>,
                               private val currentUserId: Int?,
                               private val maxWidth: Int,
                               private val markwon: Markwon,
                               private val listener: ActivityRepliesListener
): RecyclerView.Adapter<ActivityRepliesRvAdapter.ViewHolder>() {

    interface ActivityRepliesListener {
        fun openUserPage(userId: Int)
        fun editReply(replyId: Int)
        fun deleteReply(replyId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_activity_replies, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        GlideApp.with(context).load(item.user?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
        holder.avatarImage.setOnClickListener {
            listener.openUserPage(item.userId!!)
        }

        holder.nameText.text = item.user?.name
        holder.nameText.setOnClickListener {
            listener.openUserPage(item.userId!!)
        }

        holder.timeText.text = item.createdAt.secondsToDateTime()

        AndroidUtility.convertMarkdown(context, holder.activityTextLayout, item.text, maxWidth * 4 / 5, markwon)

        holder.activityLikeIcon.imageTintList = if (item.isLiked == true) {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
        } else {
            ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        }
        holder.activityLikeText.text = if (item.likeCount != 0) item.likeCount.toString() else ""
        holder.activityLikeLayout.setOnClickListener {
            // toggle like
        }

        if (item.userId != currentUserId) {
            holder.activityMoreLayout.visibility = View.GONE
        } else {
            holder.activityMoreLayout.visibility = View.VISIBLE
        }

        holder.activityMoreLayout.setOnClickListener {
            // view pop up menu (edit, delete, view in anilist, copy link)
            val popupMenu = PopupMenu(context, it)
            popupMenu.menuInflater.inflate(R.menu.menu_activity, popupMenu.menu)

            popupMenu.menu.apply {
                findItem(R.id.itemEdit).isVisible = item.userId == currentUserId
                findItem(R.id.itemDelete).isVisible = item.userId == currentUserId
                findItem(R.id.itemViewOnAniList).isVisible = false
                findItem(R.id.itemCopyLink).isVisible = false
            }

            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem? ->
                when (menuItem?.itemId) {
                    R.id.itemEdit -> listener.editReply(item.id)
                    R.id.itemDelete -> listener.deleteReply(item.id)
                }
                true
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarImage = view.avatarImage!!
        val nameText = view.nameText!!
        val timeText = view.timeText!!
        val activityTextLayout = view.activityTextLayout!!
        val activityLikeLayout = view.activityLikeLayout!!
        val activityLikeIcon = view.activityLikeIcon!!
        val activityLikeText = view.activityLikeText!!
        val activityMoreLayout = view.activityMoreLayout!!
    }
}