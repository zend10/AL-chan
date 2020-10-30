package com.zen.alchan.ui.browse.media.overview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import kotlinx.android.synthetic.main.list_text_clickable.view.*

class OverviewThemesRvAdapter(private val list: List<String>,
                              private val listener: OverviewThemeListener
) : RecyclerView.Adapter<OverviewThemesRvAdapter.ViewHolder>() {

    interface OverviewThemeListener {
        fun passSelectedTheme(title: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_text_clickable, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.clickableText.text = item
        holder.itemView.setOnClickListener {
            listener.passSelectedTheme(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clickableText = view.clickableText!!
    }
}