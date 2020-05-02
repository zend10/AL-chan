package com.zen.alchan.ui.browse.studio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.StudioMedia
import com.zen.alchan.helper.replaceUnderscore
import kotlinx.android.synthetic.main.list_character_media.view.*
import type.MediaType

class StudioMediaRvAdapter(private val context: Context,
                           private val list: List<StudioMedia?>,
                           private val listener: StudioMediaListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface StudioMediaListener {
        fun passSelectedMedia(mediaId: Int, mediaType: MediaType)
    }

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_character_media, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]
            holder.mediaTitleText.text = item?.mediaTitle
            holder.mediaFormatText.text = item?.mediaFormat?.name?.replaceUnderscore()
            holder.characterRoleLayout.visibility = View.GONE
            GlideApp.with(context).load(item?.mediaImage).into(holder.mediaCoverImage)
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
        val mediaFormatText = view.mediaFormatText!!
        val mediaTitleText = view.mediaTitleText!!
        val characterRoleLayout = view.characterRoleLayout!!
        val mediaCoverImage = view.mediaCoverImage!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}