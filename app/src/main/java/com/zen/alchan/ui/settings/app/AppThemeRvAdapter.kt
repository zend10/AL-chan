package com.zen.alchan.ui.settings.app

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_app_theme.view.*

class AppThemeRvAdapter(private val context: Context,
                        private val list: List<AppColorTheme>,
                        private val listener: AppThemeListener
) : RecyclerView.Adapter<AppThemeRvAdapter.ViewHolder>() {

    interface AppThemeListener {
        fun passSelectedTheme(theme: AppColorTheme)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_app_theme, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = list[position]

        var backgroundColor = ContextCompat.getColor(context, R.color.black)
        var strokeColor = ContextCompat.getColor(context, R.color.white)

        if (theme.name.contains("LIGHT")) {
            backgroundColor = ContextCompat.getColor(context, R.color.whiteLightTransparent80)
            strokeColor = ContextCompat.getColor(context, R.color.black)
        } else if (theme.name.contains("DARK")) {
            backgroundColor = ContextCompat.getColor(context, R.color.pureBlackLightTransparent80)
            strokeColor = ContextCompat.getColor(context, R.color.white)
        } else {
            backgroundColor = ContextCompat.getColor(context, R.color.blackLightTransparent80)
            strokeColor = ContextCompat.getColor(context, R.color.white)
        }

        holder.themeName.text = theme.name.replaceUnderscore()
        holder.themeName.setTextColor(strokeColor)

        holder.itemView.setBackgroundColor(backgroundColor)
        holder.primaryColorItem.strokeColor = strokeColor
        holder.secondaryColorItem.strokeColor = strokeColor
        holder.negativeColorItem.strokeColor = strokeColor

        val colorPalette = theme.value
        holder.primaryColorItem.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorPalette.primaryColor))
        holder.secondaryColorItem.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorPalette.secondaryColor))
        holder.negativeColorItem.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorPalette.negativeColor))

        holder.itemView.setOnClickListener {
            listener.passSelectedTheme(theme)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val themeName = view.themeName!!
        val primaryColorItem = view.primaryColorItem!!
        val secondaryColorItem = view.secondaryColorItem!!
        val negativeColorItem = view.negativeColorItem!!
    }
}