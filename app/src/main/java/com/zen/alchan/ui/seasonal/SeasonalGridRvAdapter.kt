package com.zen.alchan.ui.seasonal

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.response.SeasonalAnime
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.replaceUnderscore
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.list_seasonal_grid.view.*
import type.MediaListStatus

class SeasonalGridRvAdapter(private val context: Context,
                            private val list: List<SeasonalAnime>,
                            private val listener: SeasonalListener
): RecyclerView.Adapter<SeasonalGridRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_seasonal_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        GlideApp.with(context).load(item.coverImage?.large).into(holder.animeCoverImage)
        holder.animeTitleText.text = item.title?.userPreferred

        if (item.mediaListEntry != null) {
            GlideApp.with(context).load(R.drawable.ic_filled_circle).into(holder.animeStatusIcon)
            holder.animeStatusIcon.imageTintList = ColorStateList.valueOf(Constant.STATUS_COLOR_MAP[item.mediaListEntry?.status] ?: Constant.STATUS_COLOR_LIST[0])
            holder.animeStatusIcon.setOnClickListener {
                DialogUtility.showToast(context, if (item.mediaListEntry?.status == MediaListStatus.CURRENT) context.getString(R.string.watching_caps) else item.mediaListEntry?.status?.name.replaceUnderscore())
            }
        } else {
            GlideApp.with(context).load(R.drawable.ic_add_property).into(holder.animeStatusIcon)
            holder.animeStatusIcon.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeNegativeColor))
            holder.animeStatusIcon.setOnClickListener {
                listener.addToPlanning(item.id)
            }
        }

        holder.itemView.setOnLongClickListener { listener.openDetail(item); true }

        holder.itemView.setOnClickListener { listener.openAnime(item.id) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val animeCoverImage = view.animeCoverImage!!
        val animeTitleText = view.animeTitleText!!
        val animeStatusIcon = view.animeStatusIcon!!
    }
}