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
import type.UserStaffNameLanguage

class StaffCharacterNamingRvAdapter(
    private val context: Context,
    list: List<UserStaffNameLanguage>,
    private val listener: StaffCharacterNamingListener
) : BaseRecyclerViewAdapter<UserStaffNameLanguage>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(userStaffNameLanguage: UserStaffNameLanguage) {
            view.itemText.text = when (userStaffNameLanguage) {
                UserStaffNameLanguage.ROMAJI_WESTERN -> context.getString(R.string.use_staff_character_romaji_western_name_format)
                UserStaffNameLanguage.ROMAJI -> context.getString(R.string.use_staff_character_romaji_name_format)
                UserStaffNameLanguage.NATIVE -> context.getString(R.string.use_staff_character_native_name_format)
                else -> userStaffNameLanguage.name.convertFromSnakeCase()
            }
            view.itemLayout.clicks { listener.getSelectedNaming(userStaffNameLanguage) }
        }
    }

    interface StaffCharacterNamingListener {
        fun getSelectedNaming(userStaffNameLanguage: UserStaffNameLanguage)
    }
}