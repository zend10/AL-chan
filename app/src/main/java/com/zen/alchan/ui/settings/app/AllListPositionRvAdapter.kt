package com.zen.alchan.ui.settings.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import com.zen.alchan.ui.common.TextRvAdapter
import kotlinx.android.synthetic.main.list_text.view.*

class AllListPositionRvAdapter(
    private val context: Context,
    list: List<String>,
    private val listener: AllListPositionListener
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
        fun bind(content: String, index: Int) {
            val ordinalNumber = "${index + 1} -"
            if (content == "") {
                view.itemText.text = "$ordinalNumber ${context.getString(R.string.top_of_the_list)}"
            } else {
                view.itemText.text = "$ordinalNumber ${context.getString(R.string.below)} $content"
            }
            view.itemLayout.clicks {
                listener.getSelectedIndex(index)
            }
        }
    }

    interface AllListPositionListener {
        fun getSelectedIndex(index: Int)
    }
}