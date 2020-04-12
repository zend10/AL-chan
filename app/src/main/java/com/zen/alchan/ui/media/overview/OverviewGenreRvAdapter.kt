package com.zen.alchan.ui.media.overview

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_flexbox_genre.view.*

class OverviewGenreRvAdapter(private val list: List<String>,
                             private val listener: OverviewGenreListener
) : RecyclerView.Adapter<OverviewGenreRvAdapter.ViewHolder>() {

    interface OverviewGenreListener {
        fun passSelectedGenre(genre: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_flexbox_genre, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        val genreColor = Color.parseColor(AndroidUtility.getGenreHexColor(item))
        holder.genreCard.setCardBackgroundColor(genreColor)
        holder.genreText.setTextColor(Color.parseColor("#FFFFFF"))
        holder.genreText.text = item
        holder.itemView.setOnClickListener {
            listener.passSelectedGenre(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val genreCard = view.genreCard!!
        val genreText = view.genreText!!
    }
}