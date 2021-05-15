package com.zen.alchan.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.list_text.view.*

class TextRvAdapter(
    private val context: Context,
    list: List<String>,
    private val listener: TextListener
) : BaseRecyclerViewAdapter<String>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(list[position], position)
        }
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(content: String, position: Int) {
            view.itemText.text = content
            view.itemLayout.setOnClickListener { listener.getSelectedItem(content, position) }
        }
    }

    interface TextListener {
        fun getSelectedItem(text: String, index: Int)
    }
}