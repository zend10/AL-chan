package com.zen.alchan.ui.media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.databinding.LayoutTitleAndTextBinding
import com.zen.alchan.helper.pojo.MediaItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class MediaRvAdapter(
    private val context: Context,
    list: List<MediaItem>,
    private val appSetting: AppSetting,
    private val listener: MediaListener
) : BaseRecyclerViewAdapter<MediaItem, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            MediaItem.VIEW_TYPE_SYNOPSIS -> {
                val view = LayoutTitleAndTextBinding.inflate(inflater, parent, false)
                return SynopsisViewHolder(view)
            }
            else -> {
                val view = LayoutTitleAndTextBinding.inflate(inflater, parent, false)
                return SynopsisViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    inner class SynopsisViewHolder(private val binding: LayoutTitleAndTextBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.itemTitle.text = context.getString(R.string.synopsis)
            binding.itemText.text = item.synopsis
        }
    }
}