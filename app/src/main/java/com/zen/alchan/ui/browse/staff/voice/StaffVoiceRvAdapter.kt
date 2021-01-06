package com.zen.alchan.ui.browse.staff.voice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.StaffCharacter
import com.zen.alchan.helper.replaceUnderscore
import kotlinx.android.synthetic.main.list_staff_voice.view.*
import type.MediaType

class StaffVoiceRvAdapter(private val context: Context,
                          private val list: List<StaffCharacter?>,
                          private val listener: StaffVoiceListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_staff_voice, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]
            holder.staffCharacterNameText.text = item?.characterName
            holder.staffCharacterRoleText.text = item?.characterRole?.name?.replaceUnderscore()
            GlideApp.with(context).load(item?.characterImage).apply(RequestOptions.circleCropTransform()).into(holder.staffCharacterImage)

            holder.staffCharacterImage.setOnClickListener {
                listener.passSelectedCharacter(item?.characterId!!)
            }

            holder.staffCharacterMediaRecyclerView.adapter = StaffVoiceCharacterRvAdapter(
                context,
                item?.characterMediaList ?: ArrayList(),
                object : StaffVoiceCharacterRvAdapter.StaffVoiceCharacterListener {
                    override fun passSelectedCharacterMedia(mediaId: Int, mediaType: MediaType) {
                        listener.passSelectedMedia(mediaId, mediaType)
                    }
                })
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val staffCharacterImage = view.staffCharacterImage!!
        val staffCharacterNameText = view.staffCharacterNameText!!
        val staffCharacterRoleText = view.staffCharacterRoleText!!
        val staffCharacterMediaRecyclerView = view.staffCharacterMediaRecyclerView!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)
}