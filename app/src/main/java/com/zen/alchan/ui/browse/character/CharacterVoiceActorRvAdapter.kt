package com.zen.alchan.ui.browse.character

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.CharacterMedia
import com.zen.alchan.helper.pojo.CharacterVoiceActors
import com.zen.alchan.helper.pojo.MediaCharacters
import kotlinx.android.synthetic.main.list_character_voice_actors.view.*

class CharacterVoiceActorRvAdapter(private val context: Context,
                                   private val list: List<CharacterVoiceActors>,
                                   private val itemWidth: Int,
                                   private val listener: CharacterVoiceActorListener
) : RecyclerView.Adapter<CharacterVoiceActorRvAdapter.ViewHolder>() {

    interface CharacterVoiceActorListener {
        fun passSelectedVoiceActor(voiceActorId: Int)
        fun showMediaList(list: List<CharacterMedia>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_character_voice_actors, parent, false)
        view.layoutParams.width = itemWidth
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.voiceActorNameText.text = item.voiceActorName
        holder.voiceActorLanguage.text = item.voiceActorLanguage?.name
        GlideApp.with(context).load(item.voiceActorImage).apply(RequestOptions.circleCropTransform()).into(holder.voiceActorImage)
        holder.itemView.setOnClickListener { listener.passSelectedVoiceActor(item.voiceActorId!!) }
        holder.itemView.setOnLongClickListener { listener.showMediaList(item.characterMediaList!!); true }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val voiceActorImage = view.voiceActorImage!!
        val voiceActorNameText = view.voiceActorNameText!!
        val voiceActorLanguage = view.voiceActorLanguage!!
    }
}