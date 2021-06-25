package com.zen.alchan.ui.common

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.databinding.ListGenreBinding
import com.zen.alchan.helper.pojo.Genre
import com.zen.alchan.helper.pojo.getHexColor
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class GenreRvAdapter(private val context: Context, list: List<Genre>) : BaseRecyclerViewAdapter<Genre>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = ListGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) holder.bind(list[position])
    }

    inner class ViewHolder(private val binding: ListGenreBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(genre: Genre) {
            binding.genreText.text = genre.name
            binding.genreCard.setCardBackgroundColor(Color.parseColor(genre.getHexColor()))
        }
    }
}