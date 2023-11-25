package com.zen.alchan.ui.reorder

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import com.zen.databinding.ListReorderBinding
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import java.util.*

class ReorderRvAdapter(
    list: List<String>,
    private val listener: DragListener?
) : BaseReorderRecyclerViewAdapter<String, ListReorderBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListReorderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(list[position], holder)
        }
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ItemViewHolder(private val binding: ListReorderBinding) : ViewHolder(binding) {

        override fun bind(item: String, index: Int) {
            // do nothing
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: String, viewHolder: ViewHolder) {
            binding.reorderText.text = item
            binding.reorderIcon.show(listener != null)
            binding.root.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    listener?.onStartDrag(viewHolder)
                }
                true
            }
        }
    }
}