package com.zen.alchan.ui.browse.staff

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.StaffCharacter
import kotlinx.android.synthetic.main.list_two_images.view.*
import type.MediaType

class StaffCharacterRvAdapter(private val context: Context,
                              private val list: List<StaffCharacter>,
                              private val listener: StaffCharacterListener
) : RecyclerView.Adapter<StaffCharacterRvAdapter.ViewHolder>() {

    interface StaffCharacterListener {
        fun passSelectedMedia(mediaId: Int, mediaType: MediaType)
        fun passSelectedCharacter(characterId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_two_images, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.mediaImage).into(holder.mediaImage)
        holder.mediaTitleText.text = item.mediaTitle

        holder.mediaImage.visibility = View.VISIBLE
        holder.mediaTitleText.visibility = View.VISIBLE

        GlideApp.with(context).load(item.characterImage).into(holder.characterImage)
        holder.characterNameText.text = item.characterName
        holder.characterRoleText.text = item.characterRole?.name

        holder.mediaImage.setOnClickListener {
            listener.passSelectedMedia(item.mediaId!!, item.mediaType!!)
        }

        holder.characterImage.setOnClickListener {
            listener.passSelectedCharacter(item.characterId!!)
        }

        holder.itemView.setOnClickListener {
            listener.passSelectedCharacter(item.characterId!!)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val characterImage = view.leftImage!!
        val characterNameText = view.leftText!!
        val characterRoleText = view.leftSubtitleText!!
        val mediaImage = view.rightImage!!
        val mediaTitleText = view.righText!!
    }
}