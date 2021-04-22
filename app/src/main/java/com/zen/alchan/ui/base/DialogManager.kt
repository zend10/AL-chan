package com.zen.alchan.ui.base

interface DialogManager {
    fun showToast(message: Int)
    fun showMessageDialog(title: Int, message: Int, positiveButton: Int)
}