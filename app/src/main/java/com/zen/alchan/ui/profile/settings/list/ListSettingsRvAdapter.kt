package com.zen.alchan.ui.profile.settings.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import kotlinx.android.synthetic.main.list_flexbox_button_cancelable.view.*

class ListSettingsRvAdapter(private val list: List<String>,
                            private val code: Int,
                            private val listener: ListSettingsListener
) : RecyclerView.Adapter<ListSettingsRvAdapter.ViewHolder>() {

    interface ListSettingsListener {
        fun editItem(position: Int, code: Int)
        fun deleteItem(position: Int, code: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_flexbox_button_cancelable, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.itemText.text = item
        holder.itemCancelImage.setOnClickListener { listener.deleteItem(position, code) }
        holder.itemCard.setOnClickListener { listener.editItem(position, code) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemCard = view.flexBoxCard!!
        val itemText = view.flexBoxText!!
        val itemCancelImage = view.flexBoxCancelImage!!
    }
}