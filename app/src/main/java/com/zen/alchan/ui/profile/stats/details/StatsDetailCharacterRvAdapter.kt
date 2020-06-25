package com.zen.alchan.ui.profile.stats.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import kotlinx.android.synthetic.main.list_media_cover_grid.view.*

class StatsDetailCharacterRvAdapter(private val context: Context,
                                    private val list: List<StatsDetailRvAdapter.StatsDetailCharacterItem>,
                                    private val listener: StatsDetailCharacterListener
): RecyclerView.Adapter<StatsDetailCharacterRvAdapter.ViewHolder>() {

    interface StatsDetailCharacterListener {
        fun passSelectedCharacter(id: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_cover_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.image).into(holder.characterImage)
        holder.itemView.setOnClickListener { listener.passSelectedCharacter(item.id) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val characterImage = view.mediaCoverImage!!
    }
}