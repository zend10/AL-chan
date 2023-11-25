package com.zen.alchan.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.data.response.anilist.MediaTag
import com.zen.databinding.LayoutHeaderBinding
import com.zen.databinding.ListCheckboxTextBinding
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class TagRvAdapter(
    private val context: Context,
    list: List<ListItem<MediaTag?>>,
    private val selectedTagIds: ArrayList<Int>,
    private val listener: TagListener
) : BaseRecyclerViewAdapter<ListItem<MediaTag?>, ViewBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_CATEGORY -> {
                val binding = LayoutHeaderBinding.inflate(inflater, parent, false)
                CategoryViewHolder(binding)
            }
            else -> {
                val binding = ListCheckboxTextBinding.inflate(inflater, parent, false)
                TagViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].data == null) VIEW_TYPE_CATEGORY else VIEW_TYPE_TAG
    }

    inner class CategoryViewHolder(private val binding: LayoutHeaderBinding) : ViewHolder(binding) {
        override fun bind(item: ListItem<MediaTag?>, index: Int) {
            binding.headerText.text = item.text
            binding.upperHeaderDivider.root.show(true)
        }
    }

    inner class TagViewHolder(private val binding: ListCheckboxTextBinding) : ViewHolder(binding) {
        override fun bind(item: ListItem<MediaTag?>, index: Int) {
            binding.itemText.text = item.text

            val tagId = item.data?.id
            val isItemSelected = selectedTagIds.contains(tagId)
            binding.itemCheckbox.isChecked = isItemSelected
            binding.itemLayout.setOnClickListener {
                tagId?.let {
                    if (isItemSelected) {
                        selectedTagIds.remove(tagId)
                        binding.itemCheckbox.isChecked = false
                    } else {
                        selectedTagIds.add(tagId)
                        binding.itemCheckbox.isChecked = true
                    }
                }

                listener.getSelectedItems(selectedTagIds)
            }
        }
    }

    interface TagListener {
        fun getSelectedItems(selectedTagIds: ArrayList<Int>)
    }

    companion object {
        private const val VIEW_TYPE_CATEGORY = 100
        private const val VIEW_TYPE_TAG = 200
    }
}