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
import kotlinx.android.synthetic.main.list_seasonal.view.*
import type.MediaListStatus

class SeasonalRvAdapter(private val context: Context,
                        private val list: List<SeasonalAnime>,
                        private val listener: SeasonalListener
): RecyclerView.Adapter<SeasonalRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_seasonal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.seasonalTitleText.text = item.title?.userPreferred

        if (item.studios != null && !item.studios.edges.isNullOrEmpty()) {
            holder.seasonalStudioText.text = item.studios.edges[0].node.name
        } else {
            holder.seasonalStudioText.text = "TBA"
        }

        holder.seasonalScoreText.text = item.averageScore?.toString() ?: "0"
        holder.seasonalFavoriteText.text = item.favourites?.toString() ?: "0"
        holder.seasonalCountText.text = item.stats?.statusDistribution?.sumBy { it.amount }?.toString() ?: "0"

        if (item.mediaListEntry != null) {
            holder.seasonalStatusText.text = if (item.mediaListEntry?.status == MediaListStatus.CURRENT) context.getString(R.string.watching_caps) else item.mediaListEntry?.status?.name.replaceUnderscore()
            GlideApp.with(context).load(R.drawable.ic_filled_circle).into(holder.seasonalStatusIcon)

            val statusColor = Constant.STATUS_COLOR_MAP[item.mediaListEntry?.status] ?: Constant.STATUS_COLOR_LIST[0]
            holder.seasonalStatusText.setTextColor(statusColor)
            holder.seasonalStatusIcon.imageTintList = ColorStateList.valueOf(statusColor)
            holder.seasonalStatusLayout.isEnabled = false
        } else {
            holder.seasonalStatusText.text = context.getString(R.string.add_to_planning)
            GlideApp.with(context).load(R.drawable.ic_add_property).into(holder.seasonalStatusIcon)
            holder.seasonalStatusText.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeNegativeColor))
            holder.seasonalStatusIcon.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeNegativeColor))
            holder.seasonalStatusLayout.isEnabled = true
        }

        holder.seasonalStatusLayout.setOnClickListener {
            if (item.mediaListEntry == null) {
                listener.addToPlanning(item.id)
            }
        }

        GlideApp.with(context).load(item.coverImage?.large).into(holder.seasonalCoverImage)

        holder.itemView.setOnLongClickListener { listener.openDetail(item); true }

        holder.itemView.setOnClickListener { listener.openAnime(item.id) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val seasonalCoverImage = view.seasonalCoverImage!!
        val seasonalTitleText = view.seasonalTitleText!!
        val seasonalStudioText = view.seasonalStudioText!!
        val seasonalScoreText = view.seasonalScoreText!!
        val seasonalFavoriteText = view.seasonalFavoriteText!!
        val seasonalCountText = view.seasonalCountText!!
        val seasonalStatusLayout = view.seasonalStatusLayout!!
        val seasonalStatusIcon = view.seasonalStatusIcon!!
        val seasonalStatusText = view.seasonalStatusText!!
    }
}