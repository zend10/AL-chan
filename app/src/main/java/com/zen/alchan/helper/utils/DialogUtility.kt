package com.zen.alchan.helper.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtility {

    fun showToast(context: Context?, message: String?, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, length).show()
    }

    fun showToast(context: Context?, message: Int, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, context?.getString(message), length).show()
    }

    fun showOptionDialog(
        context: Context,
        title: Int,
        message: Int,
        positiveButton: Int,
        positiveAction: () -> Unit,
        negativeButton: Int,
        negativeAction: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ -> positiveAction() }
            .setNegativeButton(negativeButton) { _, _ -> negativeAction() }
            .show()
    }

    fun showOptionDialog(
        context: Context,
        title: Int,
        message: Int,
        positiveButton: Int,
        positiveAction: () -> Unit,
        negativeButton: Int,
        negativeAction: () -> Unit,
        cancelable: Boolean
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ -> positiveAction() }
            .setNegativeButton(negativeButton) { _, _ -> negativeAction() }
            .setCancelable(cancelable)
            .show()
    }

    fun showOptionDialog(
        context: Context,
        title: Int,
        message: String,
        positiveButton: Int,
        positiveAction: () -> Unit,
        negativeButton: Int,
        negativeAction: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ -> positiveAction() }
            .setNegativeButton(negativeButton) { _, _ -> negativeAction() }
            .show()
    }

    fun showInfoDialog(context: Context, message: Int) {
        MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .show()
    }

    fun showInfoDialog(context: Context, message: String) {
        MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .show()
    }

    fun showCustomViewDialog(
        context: Context,
        title: Int,
        customView: View,
        positiveButton: Int,
        positiveAction: () -> Unit,
        negativeButton: Int,
        negativeAction: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(customView)
            .setPositiveButton(positiveButton) { _, _ -> positiveAction() }
            .setNegativeButton(negativeButton) { _, _ -> negativeAction() }
            .show()
    }

    fun showActionDialog(
        context: Context,
        title: Int,
        message: Int,
        positiveButton: Int,
        positiveAction: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ -> positiveAction() }
            .show()
    }

    fun showForceActionDialog(
        context: Context,
        title: Int,
        message: Int,
        positiveButton: Int,
        positiveAction: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(positiveButton) { _, _ -> positiveAction() }
            .show()
    }
}