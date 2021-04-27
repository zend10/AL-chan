package com.zen.alchan.ui.bio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.BioItem
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class BioRvAdapter(
    private val context: Context,
    list: List<BioItem>
) : BaseRecyclerViewAdapter<BioItem>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            BioItem.VIEW_TYPE_AFFINITY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bio_affinity, parent, false)
                return AffinityViewHolder(view)
            }
            BioItem.VIEW_TYPE_BIO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bio_description, parent, false)
                return BioViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_bio_description, parent, false)
                return BioViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AffinityViewHolder -> holder.bind(list[position].animeAffinity, list[position].mangaAffinity)
            is BioViewHolder -> holder.bind(list[position].bioText)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    class AffinityViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(animeAffinity: Double?, mangaAffinity: Double?) {

        }
    }

    class BioViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(bioText: String?) {

        }
    }
}