package com.zen.alchan.ui.reorder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.ListReorderBinding
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class ReorderRvAdapter(
    private val context: Context,
    list: List<String>
) : BaseRecyclerViewAdapter<String, ListReorderBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListReorderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    inner class ItemViewHolder(private val binding: ListReorderBinding) : ViewHolder(binding) {
        override fun bind(item: String, index: Int) {
            binding.reorderText.text = item
        }
    }
}