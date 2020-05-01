package com.zen.alchan.ui.mangalist.list

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.removeTrailingZero
import com.zen.alchan.helper.secondsToDateTime
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_manga_list_linear.view.*
import type.MediaFormat
import type.ScoreFormat

class MangaListRvAdapter(private val context: Context,
                         private val list: List<MediaList>,
                         private val scoreFormat: ScoreFormat,
                         private val listStyle: ListStyle?,
                         private val listener: MangaListListener
) : RecyclerView.Adapter<MangaListRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_manga_list_linear, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mediaList = list[position]

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
        holder.mangaIncrementProgressButton.visibility = if ((mediaList.media?.chapters == null || mediaList.media?.chapters!! > mediaList.progress!!) && mediaList.progress!! < UShort.MAX_VALUE.toInt()) View.VISIBLE else View.GONE

        holder.mangaProgressVolumesText.text = "${mediaList.progressVolumes}/${mediaList.media?.volumes ?: '?'}"
        holder.mangaIncrementProgressVolumesButton.visibility = if ((mediaList.media?.volumes == null || mediaList.media?.volumes!! > mediaList.progressVolumes!!) && mediaList.progressVolumes!! < UShort.MAX_VALUE.toInt()) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            listener.openEditor(mediaList.id)
        }

        holder.mangaIncrementProgressButton.setOnClickListener {
            listener.incrementProgress(mediaList)
        }

        holder.mangaProgressText.setOnClickListener {
            listener.openProgressDialog(mediaList)
        }

        holder.mangaIncrementProgressVolumesButton.setOnClickListener {
            listener.incrementProgress(mediaList, true)
        }

        holder.mangaProgressVolumesText.setOnClickListener {
            listener.openProgressDialog(mediaList, true)
        }

        holder.mangaRatingText.setOnClickListener {
            listener.openScoreDialog(mediaList)
        }

        holder.mangaStarIcon.setOnClickListener {
            listener.openScoreDialog(mediaList)
        }

        holder.mangaTitleText.setOnClickListener {
            listener.openBrowsePage(mediaList.media!!)
        }

        if (listStyle?.cardColor != null) {
            holder.listCardBackground.setCardBackgroundColor(Color.parseColor(listStyle.cardColor))
        }

        if (listStyle?.primaryColor != null) {
            holder.mangaProgressText.setTextColor(Color.parseColor(listStyle.primaryColor))
            holder.mangaIncrementProgressButton.strokeColor = ColorStateList.valueOf(Color.parseColor(listStyle.primaryColor))
            holder.mangaIncrementProgressButton.setTextColor(Color.parseColor(listStyle.primaryColor))
            if (scoreFormat == ScoreFormat.POINT_3) {
                holder.mangaStarIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(listStyle.primaryColor))
            }

            holder.mangaProgressVolumesText.setTextColor(Color.parseColor(listStyle.primaryColor))
            holder.mangaIncrementProgressVolumesButton.strokeColor = ColorStateList.valueOf(Color.parseColor(listStyle.primaryColor))
            holder.mangaIncrementProgressVolumesButton.setTextColor(Color.parseColor(listStyle.primaryColor))
            if (scoreFormat == ScoreFormat.POINT_3) {
                holder.mangaStarIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(listStyle.primaryColor))
            }
        }

        if (listStyle?.secondaryColor != null) {
            holder.mangaProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor(listStyle.secondaryColor))
            val transparentSecondary = listStyle.secondaryColor?.substring(0, 1) + "80" + listStyle.secondaryColor?.substring(1)
            holder.mangaProgressBar.progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor(transparentSecondary))
        }

        if (listStyle?.textColor != null) {
            holder.mangaTitleText.setTextColor(Color.parseColor(listStyle.textColor))
            holder.mangaFormatText.setTextColor(Color.parseColor(listStyle.textColor))
            holder.mangaRatingText.setTextColor(Color.parseColor(listStyle.textColor))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listCardBackground = view.listCardBackground!!
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
    }
}