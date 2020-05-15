package com.zen.alchan.ui.profile.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.FavoriteItem
import kotlinx.android.synthetic.main.list_media_cover_grid.view.*

class FavoritesRvAdapter(private val context: Context,
                         private val list: List<FavoriteItem>,
                         private val listener: FavoritesListener
) : RecyclerView.Adapter<FavoritesRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_media_cover_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.content).into(holder.mediaCoverImage)
        holder.itemView.setOnClickListener { listener.passSelectedItem(item.id, item.browsePage) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mediaCoverImage = view.mediaCoverImage!!
    }
}