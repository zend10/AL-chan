package com.zen.alchan.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.databinding.ListChipBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class ChipRvAdapter(
    list: List<String>,
    private val listener: ChipListener
) : BaseRecyclerViewAdapter<String, ListChipBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListChipBinding) : ViewHolder(binding) {
        override fun bind(item: String, index: Int) {
            binding.chipText.text = item
            binding.chipDeleteIcon.clicks { listener.deleteItem(index) }
            binding.chipLayout.clicks { listener.getSelectedItem(item, index) }
        }
    }

    interface ChipListener {
        fun getSelectedItem(item: String, index: Int)
        fun deleteItem(index: Int)
    }
}