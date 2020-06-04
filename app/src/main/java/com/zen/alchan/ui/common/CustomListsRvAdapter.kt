package com.zen.alchan.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.CustomListsItem
import kotlinx.android.synthetic.main.list_checkbox.view.*

class CustomListsRvAdapter(private val list: List<CustomListsItem>,
                           private val listener: CustomListsListener?,
                           private val isClickable: Boolean? = true
) : RecyclerView.Adapter<CustomListsRvAdapter.ViewHolder>() {

    interface CustomListsListener {
        fun passSelected(index: Int, isChecked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.checkBoxLabel.text = item.customList
        holder.checkBoxField.isChecked = item.isChecked
        if (isClickable == true) {
            holder.checkBoxField.setOnClickListener {
                listener?.passSelected(position, !item.isChecked)
            }
            holder.checkBoxLabel.setOnClickListener {
                holder.checkBoxField.performClick()
            }
        } else {
            holder.checkBoxField.isEnabled = false
            holder.checkBoxLabel.isEnabled = false
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBoxField = view.checkBoxField!!
        val checkBoxLabel = view.checkBoxLabel!!
    }
}