package com.zen.alchan.ui.editor

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.databinding.ListCustomListsBinding
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class CustomListsRvAdapter(
    items: List<Pair<String, Boolean>>,
    private val listener: CustomListsListener
) : BaseRecyclerViewAdapter<Pair<String, Boolean>, ListCustomListsBinding>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListCustomListsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListCustomListsBinding) : ViewHolder(binding) {
        override fun bind(item: Pair<String, Boolean>, index: Int) {
            binding.customListsName.text = item.first
            binding.customListsCheckBox.isChecked = item.second
            binding.customListsCheckBox.setOnClickListener {
                val isChecked = binding.customListsCheckBox.isChecked
                listener.getNewCustomList(item.first to isChecked)
            }
        }
    }

    interface CustomListsListener {
        fun getNewCustomList(newCustomList: Pair<String, Boolean>)
    }
}