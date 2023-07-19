package com.zen.alchan.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.ListCheckboxTextBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class BottomSheetMultiSelectRvAdapter<T>(
    private val context: Context,
    list: List<ListItem<T>>,
    private val selectedIndexes: ArrayList<Int>,
    private val listener: BottomSheetMultiSelectListener<T>
) : BaseRecyclerViewAdapter<ListItem<T>, ListCheckboxTextBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListCheckboxTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    private fun getListItemFromIndexes(): List<T> {
        val selectedItems = ArrayList<T>()
        selectedIndexes.forEach {
            selectedItems.add(list[it].data)
        }
        return selectedItems
    }

    inner class ItemViewHolder(private val binding: ListCheckboxTextBinding) : ViewHolder(binding) {
        override fun bind(item: ListItem<T>, index: Int) {
            var convertedText = item.text
            item.stringResources.forEachIndexed { counter, stringResource ->
                convertedText = convertedText.replace("{$counter}", context.getString(stringResource))
            }
            binding.itemText.text = convertedText
            binding.itemCheckbox.isChecked = selectedIndexes.contains(index)
            binding.itemLayout.setOnClickListener {
                if (selectedIndexes.contains(index)) {
                    selectedIndexes.remove(index)
                    binding.itemCheckbox.isChecked = false
                } else {
                    selectedIndexes.add(index)
                    binding.itemCheckbox.isChecked = true
                }
                listener.getSelectedItems(getListItemFromIndexes(),  selectedIndexes)
            }
        }
    }

    interface BottomSheetMultiSelectListener<T> {
         fun getSelectedItems(data: List<T>, index: List<Int>)
    }
}