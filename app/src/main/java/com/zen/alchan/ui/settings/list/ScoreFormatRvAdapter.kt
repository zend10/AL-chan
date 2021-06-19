package com.zen.alchan.ui.settings.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.helper.extensions.getString
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.list_text.view.*
import type.ScoreFormat

class ScoreFormatRvAdapter(
    private val context: Context,
    list: List<ScoreFormat>,
    private val listener: ScoreFormatListener
) : BaseRecyclerViewAdapter<ScoreFormat>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(scoreFormat: ScoreFormat) {
            view.itemText.text = scoreFormat.getString(context)
            view.itemLayout.clicks { listener.getSelectedScoreFormat(scoreFormat) }
        }
    }

    interface ScoreFormatListener {
        fun getSelectedScoreFormat(scoreFormat: ScoreFormat)
    }
}