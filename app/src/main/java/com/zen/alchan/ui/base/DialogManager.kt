package com.zen.alchan.ui.base

import com.zen.alchan.data.response.anilist.MediaTag
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.SliderItem
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.ui.common.BottomSheetTextInputDialog

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

    fun <T> showListDialog(list: List<ListItem<T>>, action: (data: T, index: Int) -> Unit)
    fun showListDialog(adapter: BaseRecyclerViewAdapter<*, *>)
    fun dismissListDialog()

    fun showTextInputDialog(currentText: String, textInputSetting: TextInputSetting, action: (newText: String) -> Unit)
    fun showSliderDialog(sliderItem: SliderItem, useSingleSlider: Boolean = false, action: (minValue: Int?, maxValue: Int?) -> Unit)
    fun <T> showMultiSelectDialog(list: List<ListItem<T>>, selectedIndex: ArrayList<Int>, action: (data: List<T>) -> Unit)

    fun showTagDialog(list: List<ListItem<MediaTag?>>, selectedIndex: ArrayList<Int>, action: (data: List<MediaTag>) -> Unit)
}