package com.zen.alchan.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.databinding.ListChipBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class ChipRvAdapter(
    list: List<String>,
    private val listener: ChipListener
) : BaseRecyclerViewAdapter<String>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ListChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position], position)
    }

    inner class ViewHolder(private val binding: ListChipBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, index: Int) {
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