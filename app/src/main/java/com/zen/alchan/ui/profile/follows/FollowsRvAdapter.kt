package com.zen.alchan.ui.profile.follows

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.helper.enums.FollowPage
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.FollowsItem
import kotlinx.android.synthetic.main.list_follows.view.*

class FollowsRvAdapter(private val context: Context,
                       private val list: List<FollowsItem?>,
                       private val fromOtherUser: Boolean,
                       private val listener: FollowsListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface FollowsListener {
        fun openUserPage(id: Int)
        fun toggleFollow(id: Int, isFollowing: Boolean)
        fun openAniListPage(url: String)
    }

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_follows, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]

            GlideApp.with(context).load(item?.image).apply(RequestOptions.circleCropTransform()).into(holder.followAvatar)
            holder.followNameText.text = item?.name

            if (fromOtherUser) {
                if (item?.isFollowing == true && item.isFollower) {
                    holder.followStatusText.text = context.getString(R.string.mutual)
                    holder.followStatusLayout.visibility = View.VISIBLE
                } else if (item?.isFollowing == true && !item.isFollower) {
                    holder.followStatusText.text = context.getString(R.string.following)
                    holder.followStatusLayout.visibility = View.VISIBLE
                } else if (item?.isFollowing == false && item.isFollower) {
                    holder.followStatusText.text = context.getString(R.string.follows_you)
                    holder.followStatusLayout.visibility = View.VISIBLE
                } else {
                    holder.followStatusText.text = ""
                    holder.followStatusLayout.visibility = View.GONE
                }
            } else {
                if (item?.isFollowing == true && item.isFollower) {
                    holder.followStatusText.text = context.getString(R.string.mutual)
                    holder.followStatusLayout.visibility = View.VISIBLE
                } else {
                    holder.followStatusText.text = ""
                    holder.followStatusLayout.visibility = View.GONE
                }
            }

            holder.followMoreIcon.setOnClickListener {
                val wrapper = ContextThemeWrapper(context, R.style.PopupTheme)
                val popupMenu = PopupMenu(wrapper, it)
                popupMenu.menuInflater.inflate(R.menu.menu_follows, popupMenu.menu)
                if (fromOtherUser) {
                    popupMenu.menu.findItem(R.id.itemUnfollow).isVisible = false
                    popupMenu.menu.findItem(R.id.itemFollow).isVisible = false
                } else {
                    if (item?.isFollowing == true) {
                        popupMenu.menu.findItem(R.id.itemUnfollow).isVisible = true
                        popupMenu.menu.findItem(R.id.itemFollow).isVisible = false
                    } else {
                        popupMenu.menu.findItem(R.id.itemUnfollow).isVisible = false
                        popupMenu.menu.findItem(R.id.itemFollow).isVisible = true
                    }
                }
                popupMenu.setOnMenuItemClickListener { menuItem: MenuItem? ->
                    when (menuItem?.itemId) {
                        R.id.itemFollow -> listener.toggleFollow(item?.id!!, true)
                        R.id.itemUnfollow -> listener.toggleFollow(item?.id!!, false)
                        R.id.itemViewOnAniList -> listener.openAniListPage(item?.siteUrl!!)
                    }
                    true
                }
                popupMenu.show()
            }

            holder.itemView.setOnClickListener {
                listener.openUserPage(item?.id!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val followAvatar = view.followAvatar!!
        val followNameText = view.followNameText!!
        val followStatusText = view.followStatusText!!
        val followStatusLayout = view.followStatusLayout!!
        val followMoreIcon = view.followMoreIcon!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}