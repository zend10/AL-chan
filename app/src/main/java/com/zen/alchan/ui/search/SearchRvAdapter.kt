package com.zen.alchan.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.ListLoadingBinding
import com.zen.alchan.databinding.ListSearchBinding
import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.pojo.SearchItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class SearchRvAdapter(
    private val context: Context,
    list: List<SearchItem?>,
    private val appSetting: AppSetting,
    private val listener: SearchListener
) : BaseRecyclerViewAdapter<SearchItem?, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val view = ListLoadingBinding.inflate(inflater, parent, false)
                LoadingViewHolder(view)
            }
            else -> {
                val view = ListSearchBinding.inflate(inflater, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    inner class ItemViewHolder(private val binding: ListSearchBinding) : ViewHolder(binding) {
        override fun bind(item: SearchItem?, index: Int) {
            if (item == null)
                return

            binding.apply {
                when (item.searchCategory) {
                    SearchCategory.ANIME, SearchCategory.MANGA -> {
                        ImageUtil.loadImage(context, item.media.getCoverImage(appSetting), searchImage)
                        searchTitle.text = item.media.getTitle(appSetting)
                        root.clicks { listener.navigateToMedia(item.media) }
                    }
                    SearchCategory.CHARACTER -> {

                    }
                    SearchCategory.STAFF -> {

                    }
                    SearchCategory.STUDIO -> {

                    }
                    SearchCategory.USER -> {

                    }
                }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ListLoadingBinding) : ViewHolder(binding) {
        override fun bind(item: SearchItem?, index: Int) {
            // do nothing
        }
    }

    interface SearchListener {
        fun navigateToMedia(media: Media)
    }
}