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
) : BaseRecyclerViewAdapter<UserTitleLanguage>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ListTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position])
    }

    inner class ViewHolder(private val binding: ListTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userTitleLanguage: UserTitleLanguage) {
            binding.itemText.text = when (userTitleLanguage) {
                UserTitleLanguage.ROMAJI -> context.getString(R.string.use_media_romaji_name_format)
                UserTitleLanguage.ENGLISH -> context.getString(R.string.use_media_english_name_format)
                UserTitleLanguage.NATIVE -> context.getString(R.string.use_media_native_name_format)
                else -> userTitleLanguage.name.convertFromSnakeCase()
            }

            binding.itemLayout.clicks { listener.getSelectedLanguage(userTitleLanguage) }
        }
    }

    interface MediaTitleLanguageListener {
        fun getSelectedLanguage(userTitleLanguage: UserTitleLanguage)
    }
}