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
import kotlinx.android.synthetic.main.list_subtitle.view.*

class AppThemeRvAdapter(private val context: Context,
                        private val list: List<AppColorTheme?>,
                        private val listener: AppThemeListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface AppThemeListener {
        fun passSelectedTheme(theme: AppColorTheme)
    }

    companion object {
        const val VIEW_TYPE_TITLE = 0
        const val VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if (viewType == VIEW_TYPE_TITLE) {
            TitleViewHolder(LayoutInflater.from(context).inflate(R.layout.list_subtitle, parent, false))
        } else {
            ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_app_theme, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TitleViewHolder) {
            holder.subtitleText.text = context.getString(R.string.community_theme)
        } else if (holder is ItemViewHolder) {
            val theme = list[position]!!

            var backgroundColor = ContextCompat.getColor(context, R.color.black)
            var strokeColor = ContextCompat.getColor(context, R.color.white)

            if (theme.name.contains("ANILIST_LIGHT")) {
                backgroundColor = ContextCompat.getColor(context, R.color.anilistWhiteTransparent80)
                strokeColor = ContextCompat.getColor(context, R.color.black)
            } else if (theme.name.contains("ANILIST_DARK")) {
                backgroundColor = ContextCompat.getColor(context, R.color.anilistDeepBlueTransparent80)
                strokeColor = ContextCompat.getColor(context, R.color.white)
            } else if (theme.name.contains("LIGHT")) {
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
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) VIEW_TYPE_TITLE else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val themeName = view.themeName!!
        val primaryColorItem = view.primaryColorItem!!
        val secondaryColorItem = view.secondaryColorItem!!
        val negativeColorItem = view.negativeColorItem!!
    }

    class TitleViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val subtitleText = view.subtitleText!!
    }
}