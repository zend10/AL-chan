package com.zen.alchan.ui.browse.staff.manga

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.StaffMedia
import kotlinx.android.synthetic.main.list_one_image.view.*
import type.MediaType

class StaffMangaRvAdapter(private val context: Context,
                          private val list: List<StaffMedia?>,
                          private val listener: StaffMangaListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface StaffMangaListener {
        fun passSelectedMedia(mediaId: Int, mediaType: MediaType)
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
            holder.mediaNameText.text = item?.mediaTitle
            holder.staffRoleText.text = item?.staffRole
            GlideApp.with(context).load(item?.mediaImage).into(holder.mediaImage)
            holder.itemView.setOnClickListener {
                listener.passSelectedMedia(item?.mediaId!!, item.mediaType!!)
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
        val mediaImage = view.leftImage!!
        val mediaNameText = view.leftText!!
        val staffRoleText = view.leftSubtitleText!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}