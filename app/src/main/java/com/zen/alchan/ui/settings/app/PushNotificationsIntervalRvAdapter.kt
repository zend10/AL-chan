package com.zen.alchan.ui.settings.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.showUnit
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.list_text.view.*

class PushNotificationsIntervalRvAdapter(
    private val context: Context,
    list: List<Int>,
    private val listener: PushNotificationsIntervalListener
) : BaseRecyclerViewAdapter<Int>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(list[position])
        }
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(interval: Int) {
            view.itemText.text = interval.showUnit(context, R.plurals.hour)
            view.itemLayout.clicks {
                listener.getSelectedInterval(interval)
            }
        }
    }

    interface PushNotificationsIntervalListener {
        fun getSelectedInterval(interval: Int)
    }
}