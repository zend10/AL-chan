package com.zen.alchan.ui.common

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.Genre
import com.zen.alchan.helper.pojo.getHexColor
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.list_genre.view.*

class GenreRvAdapter(private val context: Context, list: List<Genre>) : BaseRecyclerViewAdapter<Genre>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_genre, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(genre: Genre) {
            view.genreText.text = genre.name
            view.genreCard.setCardBackgroundColor(Color.parseColor(genre.getHexColor()))
        }
    }
}