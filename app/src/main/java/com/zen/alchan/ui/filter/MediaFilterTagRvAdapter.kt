package com.zen.alchan.ui.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.list_original_checkbox.view.*
import kotlinx.android.synthetic.main.list_subtitle.view.*

class MediaFilterTagRvAdapter(private val context: Context,
                              private val list: List<MediaFilterTagDialog.MediaFilterTagItem>,
                              private val listener: MediaFilterTagListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_CATEGORY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_original_checkbox, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_subtitle, parent, false)
            CategoryViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is ItemViewHolder) {
            holder.itemCheckBox.text = item.name
            holder.itemCheckBox.isChecked = item.isChecked
            holder.itemCheckBox.setOnClickListener {
                listener.passSelectedTag(item.name)
            }
            holder.itemCheckBox.setOnLongClickListener {
                DialogUtility.showToast(context, item.description, Toast.LENGTH_LONG)
                true
            }
        } else if (holder is CategoryViewHolder) {
            holder.subtitleText.text = item.name
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].isCategory) VIEW_TYPE_CATEGORY else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemCheckBox = view.itemCheckBox!!
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subtitleText = view.subtitleText
    }
}