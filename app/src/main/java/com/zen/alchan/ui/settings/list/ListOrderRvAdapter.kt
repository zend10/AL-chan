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
) : BaseRecyclerViewAdapter<ListOrder>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ListTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position])
    }

    inner class ViewHolder(private val binding: ListTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listOrder: ListOrder) {
            binding.itemText.text = listOrder.name.convertFromSnakeCase()
            binding.itemLayout.clicks { listener.getSelectedListOrder(listOrder) }
        }
    }

    interface ListOrderListener {
        fun getSelectedListOrder(listOrder: ListOrder)
    }
}