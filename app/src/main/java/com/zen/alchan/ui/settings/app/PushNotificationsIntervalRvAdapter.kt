package com.zen.alchan.ui.settings.app

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.databinding.ListTextBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.showUnit
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class PushNotificationsIntervalRvAdapter(
    private val context: Context,
    list: List<Int>,
    private val listener: PushNotificationsIntervalListener
) : BaseRecyclerViewAdapter<Int>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ListTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(list[position])
        }
    }

    inner class ViewHolder(private val binding: ListTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(interval: Int) {
            binding.itemText.text = interval.showUnit(context, R.plurals.hour)
            binding.itemLayout.clicks {
                listener.getSelectedInterval(interval)
            }
        }
    }

    interface PushNotificationsIntervalListener {
        fun getSelectedInterval(interval: Int)
    }
}