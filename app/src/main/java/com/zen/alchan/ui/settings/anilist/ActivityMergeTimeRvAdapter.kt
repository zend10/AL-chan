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
) : BaseRecyclerViewAdapter<ActivityMergeTime, ListTextBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListTextBinding) : ViewHolder(binding) {
        override fun bind(item: ActivityMergeTime, index: Int) {
            binding.itemText.text = item.getString(context)
            binding.itemLayout.clicks {
                listener.passSelectedMinute(item.minute)
            }
        }
    }

    interface ActivityMergeTimeListener {
        fun passSelectedMinute(minute: Int)
    }
}