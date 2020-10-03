package com.zen.alchan.ui.browse.activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.*
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.secondsToDateTime
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.search.SearchActivity
import com.zen.alchan.ui.social.BestFriendRvAdapter
import com.zen.alchan.ui.social.global.GlobalFeedActivity
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.fragment_social.*
import kotlinx.android.synthetic.main.layout_best_friend.view.*
import kotlinx.android.synthetic.main.layout_view_more.view.*
import kotlinx.android.synthetic.main.list_activity.view.*
import type.ActivityType

class ActivityListRvAdapter(
    private val context: Context,
    private val list: List<ActivityItem?>,
    private val currentUserId: Int?,
    private val maxWidth: Int,
    private val markwon: Markwon,
    private val socialFilter: SocialFilter?,
    private val listener: ActivityListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_BEST_FRIEND = 2
        const val VIEW_TYPE_VIEW_MORE = 3
    }

    private val activityTypeList = arrayListOf(
        null, arrayListOf(ActivityType.TEXT), arrayListOf(ActivityType.ANIME_LIST, ActivityType.MANGA_LIST)
    )

    private val activityTypeArray = arrayOf(
        R.string.all, R.string.status, R.string.list
    )

    private lateinit var bestFriendAdapter: BestFriendRvAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_activity, parent, false)
                ItemViewHolder(view)
            }
            VIEW_TYPE_BEST_FRIEND -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_best_friend, parent, false)
                BestFriedViewHolder(view)
            }
            VIEW_TYPE_VIEW_MORE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_view_more, parent, false)
                ViewMoreViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
                LoadingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                val item = list[if (socialFilter != null) position - 1 else position]
                handleLayout(item!!, holder)
            }
            is BestFriedViewHolder -> handleBestFriendLayout(holder)
            is ViewMoreViewHolder -> handleViewMoreLayout(holder)
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

    private fun handleBestFriendLayout(holder: BestFriedViewHolder) {
        GlideApp.with(context).load(socialFilter?.bannerUrl ?: R.drawable.welcome_background).into(holder.globalActivityImage)
        holder.visitGlobalActivityButton.setOnClickListener {
            val intent = Intent(context, GlobalFeedActivity::class.java)
            intent.putExtra(GlobalFeedActivity.SELECTED_FILTER, GlobalFeedActivity.FILTER_GLOBAL)
            context.startActivity(intent)
        }

        holder.bestFriendInfo.setOnClickListener {
            DialogUtility.showInfoDialog(
                context,
                R.string.best_friend_instruction
            )
        }

        bestFriendAdapter = assignBestFriendAdapter()

        holder.bestFriendsRecyclerView.adapter = bestFriendAdapter

        if (socialFilter?.selectedBestFriend != null) {
            val index = socialFilter.bestFriends.indexOf(socialFilter.selectedBestFriend!!)
            if (index != -1) {
                holder.bestFriendsRecyclerView.scrollToPosition(index)
            }
        }

        holder.friendsActivityFilterText.text = context.getString(activityTypeArray[activityTypeList.indexOf(socialFilter?.selectedActivityType)])

        holder.friendsActivityFilterText.setOnClickListener {
            val activityTypeStringArray = activityTypeArray.map { context.getString(it) }.toTypedArray()
            MaterialAlertDialogBuilder(context)
                .setItems(activityTypeStringArray) { _, which ->
                    listener.changeActivityType(activityTypeList[which])
                }
                .show()
        }
    }

    private fun assignBestFriendAdapter(): BestFriendRvAdapter {
        return BestFriendRvAdapter(context, socialFilter?.bestFriends ?: listOf(), maxWidth / context.resources.getInteger(R.integer.horizontalListCharacterDivider),
            object : BestFriendRvAdapter.BestFriendListener {
                override fun passSelectedBestFriend(position: Int, id: Int?) {
                    if (position != 0) {
                        listener.changeBestFriend(position)
                    } else {
                        val intent = Intent(context, SearchActivity::class.java)
                        intent.putExtra(SearchActivity.SEARCH_USER, true)
                        context.startActivity(intent)
                    }
                }

                override fun openUserPage(id: Int) {
                    val intent = Intent(context, BrowseActivity::class.java)
                    intent.putExtra(BrowseActivity.TARGET_PAGE, BrowsePage.USER.name)
                    intent.putExtra(BrowseActivity.LOAD_ID, id)
                    context.startActivity(intent)
                }
            })
    }

    private fun handleViewMoreLayout(holder: ViewMoreViewHolder) {
        holder.viewMoreButton.setOnClickListener {
            val intent = Intent(context, GlobalFeedActivity::class.java)
            if (socialFilter?.selectedBestFriend != null) {
                intent.putExtra(GlobalFeedActivity.SELECTED_FILTER, GlobalFeedActivity.FILTER_BEST_FRIEND)
                intent.putExtra(GlobalFeedActivity.SELECTED_BEST_FRIEND, socialFilter.selectedBestFriend?.id)
            } else {
                intent.putExtra(GlobalFeedActivity.SELECTED_FILTER, GlobalFeedActivity.FILTER_FOLLOWING)
            }
            intent.putExtra(GlobalFeedActivity.SELECTED_ACTIVITY_TYPE, activityTypeList.indexOf(socialFilter?.selectedActivityType))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return if (socialFilter != null) list.size + 2 else list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (socialFilter != null && position == 0) {
            VIEW_TYPE_BEST_FRIEND
        } else if (socialFilter != null && position > list.size) {
            VIEW_TYPE_VIEW_MORE
        } else if (socialFilter == null && list[position] == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
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

    class BestFriedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val globalActivityImage = view.globalActivityImage!!
        val visitGlobalActivityButton = view.visitGlobalActivityButton!!
        val bestFriendInfo = view.bestFriendInfo!!
        val friendsActivityFilterText = view.friendsActivityFilterText!!
        val bestFriendsRecyclerView = view.bestFriendsRecyclerView!!
    }

    class ViewMoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewMoreButton = view.viewMoreButton!!
    }
}