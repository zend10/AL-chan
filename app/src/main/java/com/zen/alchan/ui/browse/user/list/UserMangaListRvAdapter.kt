package com.zen.alchan.ui.browse.user.list

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.removeTrailingZero
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.list_manga_list_linear.view.*
import type.ScoreFormat

class UserMangaListRvAdapter(private val context: Context,
                             private val list: List<UserMediaListCollectionQuery.Entry?>,
                             private val scoreFormat: ScoreFormat,
                             private val listener: UserMediaListener
): RecyclerView.Adapter<UserMangaListRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_manga_list_linear, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mediaList = list[position]!!

        GlideApp.with(context).load(mediaList.media?.coverImage?.large).into(holder.mangaCoverImage)
        holder.mangaTitleText.text = mediaList.media?.title?.userPreferred
        holder.mangaFormatText.text = mediaList.media?.format?.name?.replace('_', ' ')

        if (scoreFormat == ScoreFormat.POINT_3) {
            GlideApp.with(context).load(AndroidUtility.getSmileyFromScore(mediaList.score)).into(holder.mangaStarIcon)
            holder.mangaStarIcon.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
            holder.mangaRatingText.text = ""
        } else {
            GlideApp.with(context).load(R.drawable.ic_star_filled).into(holder.mangaStarIcon)
            holder.mangaStarIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellowStar))
            holder.mangaRatingText.text = mediaList.score?.removeTrailingZero()
        }

        holder.mangaProgressBar.progress = if (mediaList.media?.chapters == null && mediaList.progress!! > 0) {
            50
        } else if (mediaList.media?.chapters != null && mediaList.media?.chapters != 0) {
            (mediaList.progress!!.toDouble() / mediaList.media?.chapters!!.toDouble() * 100).toInt()
        } else {
            0
        }

        holder.mangaProgressText.text = "${mediaList.progress}/${mediaList.media?.chapters ?: '?'}"
        holder.mangaIncrementProgressButton.visibility = View.GONE

        holder.mangaProgressVolumesText.text = "${mediaList.progressVolumes}/${mediaList.media?.volumes ?: '?'}"
        holder.mangaIncrementProgressVolumesButton.visibility = View.GONE

        if (!mediaList.notes.isNullOrBlank()) {
            holder.mangaNotesLayout.visibility = View.VISIBLE
            holder.mangaNotesLayout.setOnClickListener {
                DialogUtility.showToast(context, mediaList.notes, Toast.LENGTH_LONG)
            }
        } else {
            holder.mangaNotesLayout.visibility = View.GONE
        }

        if (mediaList.priority != null && mediaList.priority != 0) {
            holder.mangaPriorityIndicator.visibility = View.VISIBLE
            holder.mangaPriorityIndicator.setBackgroundColor(Constant.PRIORITY_COLOR_MAP[mediaList.priority]!!)
        } else {
            holder.mangaPriorityIndicator.visibility = View.GONE
        }

        holder.mangaTitleText.isEnabled = false
        holder.mangaProgressText.isEnabled = false
        holder.mangaProgressVolumesText.isEnabled = false

        holder.mangaRatingText.isEnabled = false
        holder.mangaStarIcon.isEnabled = false

        holder.itemView.setOnClickListener {
            listener.openSelectedMedia(mediaList.media?.id!!, mediaList.media.type!!)
        }

        holder.itemView.setOnLongClickListener {
            listener.viewMediaListDetail(mediaList.id)
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mangaTitleText = view.mangaTitleText!!
        val mangaCoverImage = view.mangaCoverImage!!
        val mangaFormatText = view.mangaFormatText!!
        val mangaStarIcon = view.mangaStarIcon!!
        val mangaRatingText = view.mangaRatingText!!
        val mangaProgressBar = view.mangaProgressBar!!
        val mangaProgressText = view.mangaProgressText!!
        val mangaIncrementProgressButton = view.mangaIncrementProgressButton!!
        val mangaProgressVolumesText = view.mangaProgressVolumesText!!
        val mangaIncrementProgressVolumesButton = view.mangaIncrementProgressVolumesButton!!
        val mangaNotesLayout = view.mangaNotesLayout!!
        val mangaNotesIcon = view.mangaNotesIcon!!
        val mangaPriorityIndicator = view.mangaPriorityIndicator!!
    }
}