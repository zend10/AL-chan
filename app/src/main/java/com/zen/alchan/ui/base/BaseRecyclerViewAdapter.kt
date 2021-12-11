package com.zen.alchan.ui.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerViewAdapter<T, VB: ViewBinding>(
    protected var list: List<T>,
) : RecyclerView.Adapter<BaseRecyclerViewAdapter<T, VB>.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_LOADING = -1
        const val VIEW_TYPE_CONTENT = -2
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_CONTENT
    }

    fun updateData(list: List<T>, alwaysRefresh: Boolean = false) {
        if (!alwaysRefresh && this.list == list)
            return

        this.list = list
        notifyDataSetChanged()
    }

    abstract inner class ViewHolder(private val binding: VB) : RecyclerView.ViewHolder(binding.root), ViewHolderContract<T>
}