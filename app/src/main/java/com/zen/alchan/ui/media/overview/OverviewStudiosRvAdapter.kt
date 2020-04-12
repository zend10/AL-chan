package com.zen.alchan.ui.media.overview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.KeyValueItem
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_text_clickable.view.*

class OverviewStudiosRvAdapter(val context: Context?,
                               val list: List<KeyValueItem>,
                               val listener: OverviewStudioListener
) : RecyclerView.Adapter<OverviewStudiosRvAdapter.ViewHolder>() {

    interface OverviewStudioListener {
        fun passSelectedStudio(studioId: Int?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_text_clickable, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.clickableText.text = item.key
        holder.itemView.setOnClickListener { listener.passSelectedStudio(item.id) }

        if (item.id == null) {
            holder.clickableText.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeContentColor))
        } else {
            holder.clickableText.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clickableText = view.clickableText!!
    }
}