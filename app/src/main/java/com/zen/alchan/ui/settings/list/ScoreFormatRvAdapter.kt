package com.zen.alchan.ui.settings.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.databinding.ListTextBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.getString
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import type.ScoreFormat

class ScoreFormatRvAdapter(
    private val context: Context,
    list: List<ScoreFormat>,
    private val listener: ScoreFormatListener
) : BaseRecyclerViewAdapter<ScoreFormat, ListTextBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: ScoreFormat, index: Int) {
            binding.itemText.text = item.getString(context)
            binding.itemLayout.clicks { listener.getSelectedScoreFormat(item) }
        }
    }

    interface ScoreFormatListener {
        fun getSelectedScoreFormat(scoreFormat: ScoreFormat)
    }
}