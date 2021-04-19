package com.zen.alchan.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T>(
    protected var list: List<T>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(list: List<T>) {
        this.list = list
        notifyDataSetChanged()
    }
}