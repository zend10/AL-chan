package com.zen.alchan.ui.base

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zen.alchan.data.response.anilist.MediaTag
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.SliderItem
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.ui.common.*
import com.zen.alchan.ui.common.BottomSheetTagDialog
import com.zen.alchan.ui.common.TagRvAdapter

class DefaultDialogManager(private val context: Context) : DialogManager {

    private var bottomSheetListDialog: BottomSheetListDialog? = null
    private var bottomSheetTextInputDialog: BottomSheetTextInputDialog? = null
    private var bottomSheetSliderDialog: BottomSheetSliderDialog? = null
    private var bottomSheetTagDialog: BottomSheetTagDialog? = null

    override fun showToast(message: Int) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showMessageDialog(title: Int, message: Int, positiveButton: Int) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton, null)
            .setCancelable(false)
            .show()
    }

    override fun showConfirmationDialog(
        title: Int,
        message: Int,
        positiveButton: Int,
        positiveAction: () -> Unit,
        negativeButton: Int,
        negativeAction: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ -> positiveAction() }
            .setNegativeButton(negativeButton) { _, _ -> negativeAction() }
            .setCancelable(false)
            .show()
    }

    override fun <T> showListDialog(
        list: List<ListItem<T>>,
        action: (data: T, index: Int) -> Unit
    ) {
        val adapter = BottomSheetListRvAdapter(context, list, object : BottomSheetListRvAdapter.BottomSheetListListener<T> {
            override fun getSelectedItem(data: T, index: Int) {
                dismissListDialog()
                action(data, index)
            }
        })
        bottomSheetListDialog = BottomSheetListDialog.newInstance(adapter)
        bottomSheetListDialog?.dialog?.setOnCancelListener {
            bottomSheetListDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetListDialog?.show(it, null)
        }
    }

    override fun showListDialog(adapter: BaseRecyclerViewAdapter<*, *>) {
        bottomSheetListDialog = BottomSheetListDialog.newInstance(adapter)
        bottomSheetListDialog?.dialog?.setOnCancelListener {
            bottomSheetListDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetListDialog?.show(it, null)
        }
    }

    override fun dismissListDialog() {
        bottomSheetListDialog?.dismiss()
        bottomSheetListDialog = null
    }

    override fun showTextInputDialog(
        currentText: String,
        textInputSetting: TextInputSetting,
        action: (newText: String) -> Unit
    ) {
        bottomSheetTextInputDialog = BottomSheetTextInputDialog.newInstance(currentText, textInputSetting, object : BottomSheetTextInputDialog.BottomSheetTextInputListener {
            override fun getNewText(newText: String) {
                action(newText)
            }
        })
        bottomSheetTextInputDialog?.dialog?.setOnCancelListener {
            bottomSheetTextInputDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetTextInputDialog?.show(it, null)
        }
    }

    override fun showSliderDialog(
        sliderItem: SliderItem,
        useSingleSlider: Boolean,
        action: (minValue: Int?, maxValue: Int?) -> Unit
    ) {
        bottomSheetSliderDialog = BottomSheetSliderDialog.newInstance(sliderItem, useSingleSlider, object : BottomSheetSliderDialog.BottomSheetSliderListener {
            override fun getNewValues(minValue: Int?, maxValue: Int?) {
                action(minValue, maxValue)
            }
        })
        bottomSheetSliderDialog?.dialog?.setOnCancelListener {
            bottomSheetSliderDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetSliderDialog?.show(it, null)
        }
    }

    override fun <T> showMultiSelectDialog(
        list: List<ListItem<T>>,
        selectedIndex: ArrayList<Int>,
        action: (data: List<T>) -> Unit
    ) {
        val adapter = BottomSheetMultiSelectRvAdapter(context, list, selectedIndex, object : BottomSheetMultiSelectRvAdapter.BottomSheetMultiSelectListener<T> {
            override fun getSelectedItems(data: List<T>, index: List<Int>) {
                action(data)
            }
        })
        bottomSheetListDialog = BottomSheetListDialog.newInstance(adapter)
        bottomSheetListDialog?.dialog?.setOnCancelListener {
            bottomSheetListDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetListDialog?.show(it, null)
        }
    }

    override fun showTagDialog(
        list: List<ListItem<MediaTag?>>,
        selectedIndex: ArrayList<Int>,
        action: (data: List<MediaTag>) -> Unit
    ) {
        bottomSheetTagDialog = BottomSheetTagDialog.newInstance(list, selectedIndex, object : BottomSheetTagDialog.TagDialogListener {
            override fun getSelectedTags(list: List<MediaTag>) {
                action(list)
            }
        })
        bottomSheetTagDialog?.dialog?.setOnCancelListener {
            bottomSheetTagDialog = null
        }
        (context as? AppCompatActivity)?.supportFragmentManager?.let {
            bottomSheetTagDialog?.show(it, null)
        }
    }
}