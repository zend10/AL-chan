package com.zen.alchan.ui.social

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.User
import com.zen.databinding.ListLikeBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class LikeRvAdapter(
    private val context: Context,
    list: List<User>,
    private val appSetting: AppSetting,
    private val likeListener: LikeListener
) : BaseRecyclerViewAdapter<User, ListLikeBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListLikeBinding) : ViewHolder(binding) {
        override fun bind(item: User, index: Int) {
            with(binding) {
                ImageUtil.loadCircleImage(context, item.avatar.getImageUrl(appSetting), likeAvatar)
                likeName.text = item.name
                root.clicks {
                    likeListener.navigateToUser(item)
                }
            }
        }
    }

    interface LikeListener {
        fun navigateToUser(user: User)
    }
}