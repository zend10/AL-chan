package com.zen.alchan.ui.settings.anilist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.databinding.ListTextBinding
import com.zen.alchan.helper.enums.ActivityMergeTime
import com.zen.alchan.helper.enums.getString
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class ActivityMergeTimeRvAdapter(
    private val context: Context,
    list: List<ActivityMergeTime>,
    private val listener: ActivityMergeTimeListener
) : BaseRecyclerViewAdapter<ActivityMergeTime>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ListTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position])
    }

    inner class ViewHolder(private val binding: ListTextBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activityMergeTime: ActivityMergeTime) {
            binding.itemText.text = activityMergeTime.getString(context)
            binding.itemLayout.clicks {
                listener.passSelectedMinute(activityMergeTime.minute)
            }
        }
    }

    interface ActivityMergeTimeListener {
        fun passSelectedMinute(minute: Int)
    }
}