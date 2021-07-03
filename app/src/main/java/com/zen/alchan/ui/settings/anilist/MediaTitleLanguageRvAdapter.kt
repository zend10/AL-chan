package com.zen.alchan.ui.settings.anilist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.databinding.ListTextBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import type.UserTitleLanguage

class MediaTitleLanguageRvAdapter(
    private val context: Context,
    list: List<UserTitleLanguage>,
    private val listener: MediaTitleLanguageListener
) : BaseRecyclerViewAdapter<UserTitleLanguage, ListTextBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: UserTitleLanguage, index: Int) {
            binding.itemText.text = when (item) {
                UserTitleLanguage.ROMAJI -> context.getString(R.string.use_media_romaji_name_format)
                UserTitleLanguage.ENGLISH -> context.getString(R.string.use_media_english_name_format)
                UserTitleLanguage.NATIVE -> context.getString(R.string.use_media_native_name_format)
                else -> item.name.convertFromSnakeCase()
            }

            binding.itemLayout.clicks { listener.getSelectedLanguage(item) }
        }
    }

    interface MediaTitleLanguageListener {
        fun getSelectedLanguage(userTitleLanguage: UserTitleLanguage)
    }
}