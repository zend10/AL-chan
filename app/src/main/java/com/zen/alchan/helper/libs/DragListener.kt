package com.zen.alchan.helper.libs

import androidx.recyclerview.widget.RecyclerView

interface DragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}