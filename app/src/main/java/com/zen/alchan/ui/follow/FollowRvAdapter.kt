package com.zen.alchan.ui.follow

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.PopupMenuCompat
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.User
import com.zen.alchan.databinding.ListFollowBinding
import com.zen.alchan.databinding.ListLoadingBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class FollowRvAdapter(
    private val context: Context,
    list: List<User?>,
    private val appSetting: AppSetting,
    private val listener: FollowListener
) : BaseRecyclerViewAdapter<User?, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = ListLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = ListFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(view)
            }
        }

    }

    inner class ItemViewHolder(private val binding: ListFollowBinding) : ViewHolder(binding) {
        override fun bind(item: User?, index: Int) {
            if (item == null)
                return

            binding.apply {
                ImageUtil.loadImage(context, item.bannerImage, followBanner)
                ImageUtil.loadCircleImage(context, item.avatar.getImageUrl(appSetting), followAvatar)

                followUsername.text = item.name

                badgeLayout.badgeCard.show(item.isFollowing && item.isFollower)
                badgeLayout.badgeText.text = context.getString(R.string.mutual)

                followMoreIcon.clicks {
                    val popupWrapper = ContextThemeWrapper(context, R.style.PopupTheme)
                    val popupMenu = PopupMenu(popupWrapper, followMoreIcon)
                    popupMenu.menuInflater.inflate(R.menu.menu_follow, popupMenu.menu)

                    popupMenu.menu.findItem(R.id.itemFollow).isVisible = !item.isFollowing
                    popupMenu.menu.findItem(R.id.itemUnfollow).isVisible = item.isFollowing

                    popupMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.itemFollow -> listener.toggleFollow(item)
                            R.id.itemUnfollow -> listener.toggleUnfollow(item)
                            R.id.itemViewOnAniList -> listener.viewOnAniList(item)
                        }
                        true
                    }

                    popupMenu.show()
                }

                root.clicks {
                    listener.navigateToUser(item)
                }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ListLoadingBinding) : ViewHolder(binding) {
        override fun bind(item: User?, index: Int) {
            // do nothing
        }
    }

    interface FollowListener {
        fun navigateToUser(user: User)
        fun toggleFollow(user: User)
        fun toggleUnfollow(user: User)
        fun viewOnAniList(user: User)
    }
}