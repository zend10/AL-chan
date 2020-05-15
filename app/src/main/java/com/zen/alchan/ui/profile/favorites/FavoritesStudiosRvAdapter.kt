package com.zen.alchan.ui.profile.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.FavoriteItem
import kotlinx.android.synthetic.main.list_flexbox_card.view.*

class FavoritesStudiosRvAdapter(private val list: List<FavoriteItem>,
                                private val listener: FavoritesListener
) : RecyclerView.Adapter<FavoritesStudiosRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_flexbox_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.cardText.text = item.content
        holder.itemView.setOnClickListener { listener.passSelectedItem(item.id, item.browsePage) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val cardText = view.cardText!!
    }
}