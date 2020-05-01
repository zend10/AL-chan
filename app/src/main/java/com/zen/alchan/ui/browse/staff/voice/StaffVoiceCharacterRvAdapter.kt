package com.zen.alchan.ui.browse.staff.voice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.StaffCharacterMedia
import com.zen.alchan.helper.replaceUnderscore
import kotlinx.android.synthetic.main.list_character_media.view.*
import type.MediaType

class StaffVoiceCharacterRvAdapter(private val context: Context,
                                   private val list: List<StaffCharacterMedia>,
                                   private val listener: StaffVoiceCharacterListener
) : RecyclerView.Adapter<StaffVoiceCharacterRvAdapter.ViewHolder>() {

    interface StaffVoiceCharacterListener {
        fun passSelectedCharacterMedia(mediaId: Int, mediaType: MediaType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_character_media, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.mediaImage).into(holder.mediaCoverImage)
        holder.mediaTitleText.text = item.mediaTitle
        holder.characterRoleLayout.visibility = View.GONE
        holder.mediaFormatText.text = item.mediaFormat?.name?.replaceUnderscore()

        holder.mediaCoverImage.setOnClickListener {
            listener.passSelectedCharacterMedia(item.mediaId!!, item.mediaType!!)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaCoverImage = view.mediaCoverImage!!
        val mediaTitleText = view.mediaTitleText!!
        val mediaFormatText = view.mediaFormatText!!
        val characterRoleLayout = view.characterRoleLayout!!
    }
}