package com.zen.alchan.ui.settings.app

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.databinding.LayoutHeaderBinding
import com.zen.alchan.databinding.ListAppThemeBinding
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.getColorName
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.AppThemeItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class AppThemeRvAdapter(
    private val context: Context,
    list: List<AppThemeItem>,
    private val listener: AppThemeListener?
) : BaseRecyclerViewAdapter<AppThemeItem>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = ListAppThemeBinding.inflate(inflater, parent, false)
                AppThemeViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(list[position])
            is AppThemeViewHolder -> holder.bind(list[position].appTheme ?: AppTheme.DEFAULT_THEME_YELLOW)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].header != null) VIEW_TYPE_HEADER else VIEW_TYPE_APP_THEME
    }

    inner class HeaderViewHolder(private val binding: LayoutHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(appThemeItem: AppThemeItem) {
            binding.headerText.text = appThemeItem.header
            binding.upperHeaderDivider.root.show(true)
        }
    }

    inner class AppThemeViewHolder(private val binding: ListAppThemeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(appTheme: AppTheme) {
            binding.apply {
                appThemeText.text = appTheme.getColorName()

                appThemePrimaryColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, appTheme.colors.first))
                appThemeSecondaryColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, appTheme.colors.second))
                appThemeNegativeColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, appTheme.colors.third))

                appThemeLayout.clicks {
                    listener?.getSelectedAppTheme(appTheme)
                }
            }
        }
    }

    interface AppThemeListener {
        fun getSelectedAppTheme(appTheme: AppTheme)
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 100
        private const val VIEW_TYPE_APP_THEME = 200
    }
}