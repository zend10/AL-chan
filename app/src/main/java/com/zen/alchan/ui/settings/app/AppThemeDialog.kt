package com.zen.alchan.ui.settings.app

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppColorTheme
import kotlinx.android.synthetic.main.dialog_list.view.*

class AppThemeDialog : DialogFragment() {

    private lateinit var listener: AppThemeDialogListener
    private lateinit var dialogView: View

    private lateinit var adapter: AppThemeRvAdapter
    private var themeList = ArrayList<AppColorTheme?>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        builder.setTitle(R.string.theme)

        if (!this::listener.isInitialized) {
            dismiss()
        }

        dialogView = activity!!.layoutInflater.inflate(R.layout.dialog_list, null)

        themeList.addAll(AppColorTheme.values())

        var titleIndex = 0
        for (index in 0 until themeList.size) {
            if (themeList[index]?.name?.contains("COMMUNITY") == true) {
                titleIndex = index
                break
            }
        }
        themeList.add(titleIndex, null)

        adapter = assignAdapter()
        dialogView.listRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        dialogView.listRecyclerView.adapter = adapter

        builder.setView(dialogView)
        builder.setNegativeButton(R.string.cancel, null)
        return builder.create()
    }

    private fun assignAdapter(): AppThemeRvAdapter {
        return AppThemeRvAdapter(activity!!, themeList, object : AppThemeRvAdapter.AppThemeListener {
            override fun passSelectedTheme(theme: AppColorTheme) {
                listener.passSelectedTheme(theme)
                dismiss()
            }
        })
    }

    fun setListener(appThemeDialogListener: AppThemeDialogListener) {
        listener = appThemeDialogListener
    }
}