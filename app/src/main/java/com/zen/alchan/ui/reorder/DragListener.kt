package com.zen.alchan.ui.reorder

import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

interface DragListener {
    fun onStartDrag(viewHolder: BaseRecyclerViewAdapter<*, *>.ViewHolder)
}