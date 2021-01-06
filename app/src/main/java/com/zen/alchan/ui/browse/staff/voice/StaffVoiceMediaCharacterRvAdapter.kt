package com.zen.alchan.ui.browse.staff.voice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.StaffCharacter
import com.zen.alchan.helper.replaceUnderscore
import kotlinx.android.synthetic.main.list_two_images.view.*
import type.MediaType

class StaffVoiceMediaCharacterRvAdapter(private val context: Context,
                                        private val list: List<StaffCharacter?>,
                                        private val listener: StaffVoiceListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_two_images, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]
            val media = item?.characterMediaList!![0]

            GlideApp.with(context).load(media.mediaImage).into(holder.mediaImage)
            holder.mediaTitleText.text = media.mediaTitle
            holder.mediaFormatText.text = media.mediaFormat?.name?.replaceUnderscore()

            GlideApp.with(context).load(item.characterImage).into(holder.characterImage)
            holder.characterNameText.text = item.characterName

            holder.characterImage.visibility = View.VISIBLE
            holder.characterNameText.visibility = View.VISIBLE

            holder.mediaImage.setOnClickListener {
                listener.passSelectedMedia(media.mediaId!!, media.mediaType ?: MediaType.ANIME)
            }

            holder.characterImage.setOnClickListener {
                listener.passSelectedCharacter(item.characterId!!)
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
        val mediaTitleText = view.leftText!!
        val mediaFormatText = view.leftSubtitleText!!
        val characterImage = view.rightImage!!
        val characterNameText = view.righText!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}