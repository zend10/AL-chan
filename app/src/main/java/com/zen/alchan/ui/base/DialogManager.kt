package com.zen.alchan.ui.base

import android.content.Context

interface DialogManager {
    fun showToast(message: Int)
    fun showMessageDialog(title: Int, message: Int, positiveButton: Int)
    fun showConfirmationDialog(
        title: Int,
        message: Int, 
        positiveButton: Int, 
        positiveAction: () -> Unit, 
        negativeButton: Int, 
        negativeAction: () -> Unit
    )
}