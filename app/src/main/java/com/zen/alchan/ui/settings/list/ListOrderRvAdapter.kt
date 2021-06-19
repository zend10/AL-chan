package com.zen.alchan.ui.settings.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.ListOrder
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.list_text.view.*

class ListOrderRvAdapter(
    private val context: Context,
    list: List<ListOrder>,
    private val listener: ListOrderListener
) : BaseRecyclerViewAdapter<ListOrder>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(listOrder: ListOrder) {
            view.itemText.text = listOrder.name.convertFromSnakeCase()
            view.itemLayout.clicks { listener.getSelectedListOrder(listOrder) }
        }
    }

    interface ListOrderListener {
        fun getSelectedListOrder(listOrder: ListOrder)
    }
}