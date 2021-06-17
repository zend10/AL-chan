package com.zen.alchan.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.list_chip.view.*

class ChipRvAdapter(
    list: List<String>,
    private val listener: ChipListener
) : BaseRecyclerViewAdapter<String>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_chip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position], position)
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: String, index: Int) {
            view.chipText.text = item
            view.chipDeleteIcon.clicks { listener.deleteItem(index) }
            view.chipLayout.clicks { listener.getSelectedItem(item, index) }
        }
    }

    interface ChipListener {
        fun getSelectedItem(item: String, index: Int)
        fun deleteItem(index: Int)
    }
}