package com.zen.alchan.ui.common.customise

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.ListType
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_flexbox_button.view.*

class ListTypeRvAdapter(private val context: Context,
                        private val list: List<ListType>,
                        private val selectedList: ListType,
                        private val listener: ListTypeListener
) : RecyclerView.Adapter<ListTypeRvAdapter.ViewHolder>() {

    interface ListTypeListener {
        fun passSelectedList(newListType: ListType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_flexbox_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.listTypeItem.text = item.name.toLowerCase().capitalize()

        if (item == selectedList) {
            holder.listTypeItem.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeCardColor))
            holder.listTypeItem.backgroundTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
        } else {
            holder.listTypeItem.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
            holder.listTypeItem.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.transparent))
        }

        holder.listTypeItem.setOnClickListener {
            listener.passSelectedList(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listTypeItem = view.flexBoxItem!!
    }
}