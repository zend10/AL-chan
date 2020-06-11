package com.zen.alchan.ui.settings.list

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.helper.libs.DragListener
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import com.zen.alchan.ui.base.BaseViewHolder
import kotlinx.android.synthetic.main.list_reorder.view.*
import java.util.*

class ListSettingsReorderRvAdapter(private val list: List<String?>,
                                   private val isEnabled: Boolean,
                                   private val listener: DragListener
): BaseRecyclerViewAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_reorder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = list[position]
        if (holder is ViewHolder) {
            holder.reorderText.text = item
            holder.itemView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    listener.onStartDrag(holder)
                }
                true
            }
            holder.itemView.isEnabled = isEnabled
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onRowSelected(itemViewHolder: BaseViewHolder) {
        // do nothing
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for ( i in fromPosition downTo toPosition + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowClear(itemViewHolder: BaseViewHolder) {
        // do nothing
    }

    class ViewHolder(view: View) : BaseViewHolder(view) {
        val reorderText = view.reorderText!!
    }
}