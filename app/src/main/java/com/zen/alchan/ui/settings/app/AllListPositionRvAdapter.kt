package com.zen.alchan.ui.settings.app

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.databinding.ListTextBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class AllListPositionRvAdapter(
    private val context: Context,
    list: List<String>,
    private val listener: AllListPositionListener
) : BaseRecyclerViewAdapter<String>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ListTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(list[position], position)
        }
    }

    inner class ViewHolder(private val binding: ListTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(content: String, index: Int) {
            val ordinalNumber = "${index + 1} -"
            if (content == "") {
                binding.itemText.text = "$ordinalNumber ${context.getString(R.string.top_of_the_list)}"
            } else {
                binding.itemText.text = "$ordinalNumber ${context.getString(R.string.below)} $content"
            }
            binding.itemLayout.clicks {
                listener.getSelectedIndex(index)
            }
        }
    }

    interface AllListPositionListener {
        fun getSelectedIndex(index: Int)
    }
}