package com.zen.alchan.ui.browse.media.stats

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.StatusDistributionItem
import kotlinx.android.synthetic.main.list_chart_legend.view.*

class MediaStatsStatusRvAdapter(private val context: Context,
                                private val list: List<StatusDistributionItem>
) : RecyclerView.Adapter<MediaStatsStatusRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_chart_legend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.legendColorIcon.imageTintList = ColorStateList.valueOf(item.color)
        holder.legendDescriptionText.text = item.status
        holder.legendValueText.text = item.amount.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val legendColorIcon = view.legendColorIcon!!
        val legendDescriptionText = view.legendDescriptionText!!
        val legendValueText = view.legendValueText!!
    }
}