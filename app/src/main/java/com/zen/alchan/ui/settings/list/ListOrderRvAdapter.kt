package com.zen.alchan.ui.settings.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.databinding.ListTextBinding
import com.zen.alchan.helper.enums.ListOrder
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class ListOrderRvAdapter(
    private val context: Context,
    list: List<ListOrder>,
    private val listener: ListOrderListener
) : BaseRecyclerViewAdapter<ListOrder, ListTextBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: ListOrder, index: Int) {
            binding.itemText.text = item.name.convertFromSnakeCase()
            binding.itemLayout.clicks { listener.getSelectedListOrder(item) }
        }
    }

    interface ListOrderListener {
        fun getSelectedListOrder(listOrder: ListOrder)
    }
}