package com.zen.alchan.ui.settings.app

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppColorTheme
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.dialog_app_theme.view.*

class AppThemeDialog : DialogFragment() {

    private lateinit var listener: AppThemeDialogListener
    private lateinit var dialogView: View

    private lateinit var selectedTheme: AppColorTheme
    private lateinit var adapter: AppThemeRvAdapter
    private var themeList = ArrayList<AppColorTheme>()

    companion object {
        const val SELECTED_THEME = "selectedTheme"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(activity)
        builder.setTitle(R.string.theme)

        dialogView = activity!!.layoutInflater.inflate(R.layout.dialog_app_theme, null)

        selectedTheme = AppColorTheme.valueOf(arguments?.getString(SELECTED_THEME)!!)
        themeList.addAll(AppColorTheme.values())
        setPaletteColor()

        adapter = assignAdapter()
        dialogView.appThemeRecyclerView.adapter = adapter

        builder.setView(dialogView)
        builder.setPositiveButton(R.string.change) { _, _ ->
            listener.passSelectedTheme(selectedTheme)
        }
        builder.setNegativeButton(R.string.cancel, null)
        return builder.create()
    }

    private fun assignAdapter(): AppThemeRvAdapter {
        return AppThemeRvAdapter(activity!!, themeList, selectedTheme, object : AppThemeRvAdapter.AppThemeListener {
            override fun passSelectedTheme(theme: AppColorTheme) {
                selectedTheme = theme
                adapter = assignAdapter()
                dialogView.appThemeRecyclerView.adapter = adapter
                setPaletteColor()
            }
        })
    }

    private fun setPaletteColor() {
        val palette = selectedTheme.value
        dialogView.primaryColorItem.setCardBackgroundColor(ContextCompat.getColor(activity!!, palette.primaryColor))
        dialogView.secondaryColorItem.setCardBackgroundColor(ContextCompat.getColor(activity!!, palette.secondaryColor))
        dialogView.negativeColorItem.setCardBackgroundColor(ContextCompat.getColor(activity!!, palette.negativeColor))
    }

    fun setListener(appThemeDialogListener: AppThemeDialogListener) {
        listener = appThemeDialogListener
    }
}