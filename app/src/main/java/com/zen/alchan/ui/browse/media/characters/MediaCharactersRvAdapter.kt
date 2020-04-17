package com.zen.alchan.ui.browse.media.characters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.MediaCharacters
import kotlinx.android.synthetic.main.list_characters.view.*
import type.StaffLanguage

class MediaCharactersRvAdapter(private val context: Context,
                               private val list: List<MediaCharacters?>,
                               private val language: StaffLanguage,
                               private val listener: MediaCharactersListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface MediaCharactersListener {
        fun passSelectedCharacter(characterId: Int)
        fun passSelectedVoiceActor(voiceActorId: Int)
    }

    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_characters, parent, false)
            ItemViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = list[position]
            holder.characterNameText.text = item?.characterName
            GlideApp.with(context).load(item?.characterImage).into(holder.characterImage)
            holder.characterRoleText.text = item?.role?.name

            holder.characterImage.setOnClickListener {
                listener.passSelectedCharacter(item?.characterId!!)
            }

            if (!item?.voiceActors.isNullOrEmpty()) {
                val voiceActor = item?.voiceActors?.find { it.voiceActorLanguage == language }
                if (voiceActor != null) {
                    holder.voiceActorNameText.text = voiceActor.voiceActorName
                    GlideApp.with(context).load(voiceActor.voiceActorImage).into(holder.voiceActorImage)
                    holder.voiceActorNameText.visibility = View.VISIBLE
                    holder.voiceActorImage.visibility = View.VISIBLE

                    holder.characterImage.setOnClickListener {
                        listener.passSelectedVoiceActor(voiceActor.voiceActorId!!)
                    }
                } else {
                    holder.voiceActorNameText.visibility = View.GONE
                    holder.voiceActorImage.visibility = View.GONE
                }
            } else {
                holder.voiceActorNameText.visibility = View.GONE
                holder.voiceActorImage.visibility = View.GONE
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
        val characterImage = view.characterImage!!
        val characterNameText = view.characterNameText!!
        val characterRoleText = view.characterRoleText!!
        val voiceActorImage = view.voiceActorImage!!
        val voiceActorNameText = view.voiceActorNameText!!
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}