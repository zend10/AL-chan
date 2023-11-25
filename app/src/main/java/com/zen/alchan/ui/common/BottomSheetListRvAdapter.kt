package com.zen.alchan.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.ListTextBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class BottomSheetListRvAdapter<T>(
    private val context: Context,
    list: List<ListItem<T>>,
    private val listener: BottomSheetListListener<T>
) : BaseRecyclerViewAdapter<ListItem<T>, ListTextBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    inner class ItemViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: ListItem<T>, index: Int) {
            var convertedText = item.text
            item.stringResources.forEachIndexed { counter, stringResource ->
                convertedText = convertedText.replace("{$counter}", context.getString(stringResource))
            }
            binding.itemText.text = convertedText
            binding.itemLayout.clicks { listener.getSelectedItem(item.data, index) }
        }
    }

    interface BottomSheetListListener<T> {
        fun getSelectedItem(data: T, index: Int)
    }
}