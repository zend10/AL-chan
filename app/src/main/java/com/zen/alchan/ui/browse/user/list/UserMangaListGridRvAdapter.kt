package com.zen.alchan.ui.browse.user.list

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.removeTrailingZero
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_manga_list_grid.view.*
import type.ScoreFormat

class UserMangaListGridRvAdapter(private val context: Context,
                                 private val list: List<UserMediaListCollectionQuery.Entry?>,
                                 private val scoreFormat: ScoreFormat,
                                 private val listener: UserMediaListener
): RecyclerView.Adapter<UserMangaListGridRvAdapter.ViewHolder>() {

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

        holder.mangaProgressText.text = "Ch. ${mediaList.progress}/${mediaList.media?.chapters ?: '?'}"
        holder.mangaProgressVolumesText.text = "Vo ${mediaList.progressVolumes}/${mediaList.media?.volumes ?: '?'}"

        holder.mangaProgressLayout.isEnabled = false
        holder.mangaProgressVolumesLayout.isEnabled = false
        holder.mangaScoreLayout.isEnabled = false

        holder.mangaCoverImage.setOnClickListener {
            listener.viewMediaListDetail(mediaList.id)
        }

        holder.mangaTitleLayout.setOnClickListener {
            listener.openSelectedMedia(mediaList.media?.id!!, mediaList.media.type!!)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listCardBackground = view.listCardBackground!!
        val mangaTitleText = view.mangaTitleText!!
        val mangaTitleLayout = view.mangaTitleLayout!!
        val mangaCoverImage = view.mangaCoverImage!!
        val mangaFormatText = view.mangaFormatText!!
        val mangaFormatLayout = view.mangaFormatLayout!!
        val mangaProgressText = view.mangaProgressText!!
        val mangaProgressVolumesText = view.mangaProgressVolumesText!!
        val mangaScoreLayout = view.mangaScoreLayout!!
        val mangaStarIcon = view.mangaStarIcon!!
        val mangaRatingText = view.mangaRatingText!!
        val mangaProgressLayout = view.mangaProgressLayout!!
        val mangaProgressVolumesLayout = view.mangaProgressVolumesLayout!!
    }
}