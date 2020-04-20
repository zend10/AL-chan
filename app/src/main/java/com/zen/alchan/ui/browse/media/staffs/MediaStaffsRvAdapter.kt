package com.zen.alchan.ui.browse.media.staffs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.MediaStaffs
import kotlinx.android.synthetic.main.list_one_image.view.*

class MediaStaffsRvAdapter(private val context: Context,
                           private val list: List<MediaStaffs?>,
                           private val listener: MediaStaffsListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface MediaStaffsListener {
        fun passSelectedStaff(staffId: Int)
    }

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_one_image, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]
            holder.staffNameText.text = item?.name
            holder.staffRoleText.text = item?.role
            GlideApp.with(context).load(item?.image).into(holder.staffImage)
            holder.itemView.setOnClickListener {
                listener.passSelectedStaff(item?.id!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val staffImage = view.leftImage!!
        val staffNameText = view.leftText!!
        val staffRoleText = view.leftSubtitleText!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}