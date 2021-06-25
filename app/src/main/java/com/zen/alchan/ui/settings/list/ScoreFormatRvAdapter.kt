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
) : BaseRecyclerViewAdapter<ScoreFormat>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ListTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position])
    }

    inner class ViewHolder(private val binding: ListTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(scoreFormat: ScoreFormat) {
            binding.itemText.text = scoreFormat.getString(context)
            binding.itemLayout.clicks { listener.getSelectedScoreFormat(scoreFormat) }
        }
    }

    interface ScoreFormatListener {
        fun getSelectedScoreFormat(scoreFormat: ScoreFormat)
    }
}