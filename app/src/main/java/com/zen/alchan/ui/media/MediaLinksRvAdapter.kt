package com.zen.alchan.ui.media

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.zen.alchan.R
import com.zen.alchan.data.response.anilist.MediaExternalLink
import com.zen.alchan.databinding.ListMediaLinkBinding
import com.zen.alchan.helper.enums.OtherLink
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.getAttrValue
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class MediaLinksRvAdapter(
    private val context: Context,
    list: List<MediaExternalLink>,
    private val listener: MediaListener.MediaLinksListener
) : BaseRecyclerViewAdapter<MediaExternalLink, ListMediaLinkBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListMediaLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListMediaLinkBinding) : ViewHolder(binding) {
        override fun bind(item: MediaExternalLink, index: Int) {
            binding.apply {
                if (item.color.isNotBlank()) {
                    linkCard.setCardBackgroundColor(Color.parseColor(item.color))
                    linkName.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    linkCard.setCardBackgroundColor(context.getAttrValue(R.attr.themeCardColor))
                    linkName.setTextColor(context.getAttrValue(R.attr.themeContentColor))
                }

                linkName.text = item.getSiteNameWithLanguage()

                when {
                    item.icon.isNotBlank() -> {
                        ImageUtil.loadImage(context, item.icon, linkIcon)
                    }
                    item.site == OtherLink.ANILIST.siteName -> {
                        ImageUtil.loadImage(context, R.drawable.ic_anilist, linkIcon)
                    }
                    else -> {
                        ImageUtil.loadImage(context, R.drawable.ic_link, linkIcon)
                        if (item.color.isNotBlank())
                            linkIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                        else
                            linkIcon.imageTintList = ColorStateList.valueOf(context.getAttrValue(R.attr.themeContentColor))
                    }
                }

                linkCard.clicks {
                    listener.navigateToUrl(item)
                }

                linkCard.setOnLongClickListener {
                    listener.copyExternalLink(item)
                    true
                }
            }
        }
    }
}