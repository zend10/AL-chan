package com.zen.alchan.ui.medialist

import android.content.Context
import androidx.viewbinding.ViewBinding
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.databinding.ListTextBinding
import com.zen.alchan.databinding.ListTitleBinding
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

abstract class BaseMediaListRvAdapter(
    private val context: Context,
    list: List<MediaListItem>
) : BaseRecyclerViewAdapter<MediaListItem, ViewBinding>(list) {

    protected var listStyle = ListStyle()
    protected var mediaListOptions = MediaListOptions()

    fun applyListStyle(listStyle: ListStyle) {
        this.listStyle = listStyle
    }

    fun applyMediaListOptions(mediaListOptions: MediaListOptions) {
        this.mediaListOptions = mediaListOptions
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    protected inner class TitleViewHolder(private val binding: ListTitleBinding) : ViewHolder(binding) {
        override fun bind(item: MediaListItem, index: Int) {
            binding.titleText.text = item.title
        }
    }
}