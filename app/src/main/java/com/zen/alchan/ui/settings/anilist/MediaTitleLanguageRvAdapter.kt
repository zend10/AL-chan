package com.zen.alchan.ui.settings.anilist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.list_text.view.*
import type.UserTitleLanguage

class MediaTitleLanguageRvAdapter(
    private val context: Context,
    list: List<UserTitleLanguage>,
    private val listener: MediaTitleLanguageListener
) : BaseRecyclerViewAdapter<UserTitleLanguage>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(userTitleLanguage: UserTitleLanguage) {
            view.itemText.text = when (userTitleLanguage) {
                UserTitleLanguage.ROMAJI -> context.getString(R.string.use_media_romaji_name_format)
                UserTitleLanguage.ENGLISH -> context.getString(R.string.use_media_english_name_format)
                UserTitleLanguage.NATIVE -> context.getString(R.string.use_media_native_name_format)
                else -> userTitleLanguage.name.convertFromSnakeCase()
            }

            view.itemLayout.clicks { listener.getSelectedLanguage(userTitleLanguage) }
        }
    }

    interface MediaTitleLanguageListener {
        fun getSelectedLanguage(userTitleLanguage: UserTitleLanguage)
    }
}