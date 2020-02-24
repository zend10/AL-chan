package com.zen.alchan.ui.profile.settings.anilist

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_flexbox_button.view.*
import type.UserTitleLanguage

class TitleLanguageRvAdapter(private val context: Context,
                             private val list: List<UserTitleLanguage>,
                             private val selectedLanguage: UserTitleLanguage,
                             private val listener: TitleLanguageListener
) : RecyclerView.Adapter<TitleLanguageRvAdapter.ViewHolder>() {

    interface TitleLanguageListener {
        fun passSelectedLanguage(language: UserTitleLanguage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_flexbox_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = list[position]
        holder.languageItem.text = language.name.toLowerCase().capitalize()

        if (language == selectedLanguage) {
            holder.languageItem.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeBackgroundColor))
            holder.languageItem.backgroundTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
        } else {
            holder.languageItem.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
            holder.languageItem.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.transparent))
        }

        holder.languageItem.setOnClickListener {
            listener.passSelectedLanguage(language)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val languageItem = view.flexBoxItem!!
    }
}