package com.zen.alchan.ui.browse.character

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.CharacterMedia
import kotlinx.android.synthetic.main.list_two_images.view.*
import type.MediaType
import type.StaffLanguage

class CharacterMediaRvAdapter(private val context: Context,
                              private val list: List<CharacterMedia>,
                              private val language: StaffLanguage,
                              private val listener: CharacterMediaListener
) : RecyclerView.Adapter<CharacterMediaRvAdapter.ViewHolder>() {

    interface CharacterMediaListener {
        fun passSelectedMedia(mediaId: Int, mediaType: MediaType)
        fun passSelectedVoiceActor(voiceActorId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_two_images, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.mediaImage).into(holder.mediaImage)
        holder.mediaName.text = item.mediaTitle
        holder.characterRoleText.text = item.role?.name

        holder.mediaImage.setOnClickListener {
            listener.passSelectedMedia(item.mediaId!!, item.mediaType!!)
        }

        holder.itemView.setOnClickListener {
            listener.passSelectedMedia(item.mediaId!!, item.mediaType!!)
        }

        if (!item.voiceActorList.isNullOrEmpty()) {
            val voiceActor = item.voiceActorList.find { it.voiceActorLanguage == language }
            if (voiceActor != null) {
                holder.voiceActorNameText.text = voiceActor.voiceActorName
                GlideApp.with(context).load(voiceActor.voiceActorImage).into(holder.voiceActorImage)
                holder.voiceActorNameText.visibility = View.VISIBLE
                holder.voiceActorImage.visibility = View.VISIBLE

                holder.voiceActorImage.setOnClickListener {
                    listener.passSelectedVoiceActor(voiceActor.voiceActorId!!)
                }
            } else {
                holder.voiceActorNameText.visibility = View.GONE
                holder.voiceActorImage.visibility = View.GONE
            }
        } else {
            holder.voiceActorImage.visibility = View.GONE
            holder.voiceActorNameText.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mediaImage = view.leftImage!!
        val mediaName = view.leftText!!
        val characterRoleText = view.leftSubtitleText!!
        val voiceActorImage = view.rightImage!!
        val voiceActorNameText = view.righText!!
    }
}