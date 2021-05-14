package com.zen.alchan.ui.settings.app

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.enums.getColorName
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.AppThemeItem
import com.zen.alchan.ui.base.BaseDialogFragment
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.dialog_app_theme_list.view.*
import kotlinx.android.synthetic.main.layout_header.view.*
import kotlinx.android.synthetic.main.list_app_theme.view.*
import kotlinx.android.synthetic.main.list_app_theme.view.appThemeText
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppThemeListDialog : BaseDialogFragment(R.layout.dialog_app_theme_list) {

    private val viewModel by viewModel<AppThemeListViewModel>()

    private var adapter: AppThemeListAdapter? = null
    private var listener: AppThemeListener? = null

    override fun setUpLayout() {
        adapter = AppThemeListAdapter(requireContext(), listOf(), listener)
        view?.appThemeRecyclerView?.adapter = adapter
    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.appThemeItems.subscribe {
                adapter?.updateData(it)
            }
        )

        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        listener = null
    }

    private class AppThemeListAdapter(
        private val context: Context,
        list: List<AppThemeItem>,
        private val listener: AppThemeListener?
    ) : BaseRecyclerViewAdapter<AppThemeItem>(list) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                VIEW_TYPE_HEADER -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_header, parent, false)
                    HeaderViewHolder(view)
                }
                else -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.list_app_theme, parent, false)
                    AppThemeViewHolder(view)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder) {
                is HeaderViewHolder -> holder.bind(list[position])
                is AppThemeViewHolder -> holder.bind(context, list[position].appTheme ?: AppTheme.DEFAULT_THEME_YELLOW, listener)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (list[position].header != null) VIEW_TYPE_HEADER else VIEW_TYPE_APP_THEME
        }

        private class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            fun bind(appThemeItem: AppThemeItem) {
                view.headerText.text = appThemeItem.header
                view.lowerHeaderDivider.show(false)
            }
        }

        private class AppThemeViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            fun bind(context: Context, appTheme: AppTheme, listener: AppThemeListener?) {
                view.appThemeText.text = appTheme.getColorName()

                view.appThemePrimaryColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, appTheme.colors.first))
                view.appThemeSecondaryColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, appTheme.colors.second))
                view.appThemeNegativeColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, appTheme.colors.third))

                view.appThemeLayout.setOnClickListener {
                    listener?.getSelectedAppTheme(appTheme)
                }
            }
        }

        companion object {
            private const val VIEW_TYPE_HEADER = 100
            private const val VIEW_TYPE_APP_THEME = 200
        }
    }

    interface AppThemeListener {
        fun getSelectedAppTheme(appTheme: AppTheme)
    }

    companion object {
        fun newInstance(listener: AppThemeListener): AppThemeListDialog = AppThemeListDialog().apply {
            this.listener = listener
        }
    }
}