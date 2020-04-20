package com.zen.alchan.ui.browse.media.overview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.MediaCharacters
import kotlinx.android.synthetic.main.list_media_characters.view.*

class OverviewCharactersRvAdapter(private val context: Context,
                                  private val list: List<MediaCharacters>,
                                  private val itemWidth: Int,
                                  private val listener: OverviewCharactersListener
) : RecyclerView.Adapter<OverviewCharactersRvAdapter.ViewHolder>() {

    interface OverviewCharactersListener {
        fun passSelectedCharacter(characterId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_characters, parent, false)
        view.layoutParams.width = itemWidth
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.characterNameText.text = item.characterName
        GlideApp.with(context).load(item.characterImage).apply(RequestOptions.circleCropTransform()).into(holder.characterImage)
        holder.itemView.setOnClickListener { listener.passSelectedCharacter(item.characterId!!) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val characterImage = view.leftImage!!
        val characterNameText = view.leftText!!
    }
}