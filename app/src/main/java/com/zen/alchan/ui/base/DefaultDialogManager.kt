package com.zen.alchan.ui.base

import android.content.Context
import androidx.appcompat.app.AlertDialog

class DefaultDialogManager(private val context: Context) : DialogManager {

    override fun showMessageDialog(title: Int, message: Int, positiveButton: Int) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton, null)
            .setCancelable(false)
            .show()
    }
}