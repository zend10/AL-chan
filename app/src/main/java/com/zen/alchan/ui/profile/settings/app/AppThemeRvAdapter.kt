package com.zen.alchan.ui.profile.settings.app

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_flexbox_button.view.*

class AppThemeRvAdapter(private val context: Context,
                        private val list: List<AppColorTheme>,
                        private val selectedTheme: AppColorTheme,
                        private val listener: AppThemeListener
) : RecyclerView.Adapter<AppThemeRvAdapter.ViewHolder>() {

    interface AppThemeListener {
        fun passSelectedTheme(theme: AppColorTheme)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_flexbox_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = list[position]
        holder.appThemeItem.text = theme.name.toLowerCase().capitalize()

        if (theme == selectedTheme) {
            holder.appThemeItem.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeCardColor))
            holder.appThemeItem.backgroundTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
        } else {
            holder.appThemeItem.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
            holder.appThemeItem.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.transparent))
        }

        holder.appThemeItem.setOnClickListener {
            listener.passSelectedTheme(theme)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appThemeItem = view.flexBoxItem!!
    }
}