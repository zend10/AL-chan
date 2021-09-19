package com.zen.alchan.ui.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseRecyclerViewAdapter<T, VB: ViewBinding>(
    protected var list: List<T>,
) : RecyclerView.Adapter<BaseRecyclerViewAdapter<T, VB>.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(list: List<T>) {
        this.list = list
        notifyDataSetChanged()
    }

    abstract inner class ViewHolder(private val binding: VB) : RecyclerView.ViewHolder(binding.root), ViewHolderContract<T>
}