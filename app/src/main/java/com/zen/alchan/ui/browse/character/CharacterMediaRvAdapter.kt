package com.zen.alchan.ui.browse.character

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.CharacterMedia
import com.zen.alchan.helper.replaceUnderscore
import kotlinx.android.synthetic.main.list_character_media.view.*
import kotlinx.android.synthetic.main.list_two_images.view.*
import type.MediaType
import type.StaffLanguage

class CharacterMediaRvAdapter(private val context: Context,
                              private val list: List<CharacterMedia>,
                              private val listener: CharacterMediaListener
) : RecyclerView.Adapter<CharacterMediaRvAdapter.ViewHolder>() {

    interface CharacterMediaListener {
        fun passSelectedMedia(mediaId: Int, mediaType: MediaType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_character_media, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.mediaImage).into(holder.mediaCoverImage)
        holder.mediaTitleText.text = item.mediaTitle
        holder.characterRoleText.text = item.role?.name?.substring(0, 1)
        holder.mediaFormatText.text = item.mediaFormat?.name?.replaceUnderscore()

        holder.mediaCoverImage.setOnClickListener {
            listener.passSelectedMedia(item.mediaId!!, item.mediaType!!)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaCoverImage = view.mediaCoverImage!!
        val mediaTitleText = view.mediaTitleText!!
        val mediaFormatText = view.mediaFormatText!!
        val characterRoleText = view.characterRoleText!!
    }
}