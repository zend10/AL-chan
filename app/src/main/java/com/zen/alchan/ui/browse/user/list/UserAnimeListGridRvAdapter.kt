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
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.removeTrailingZero
import com.zen.alchan.helper.secondsToDateTime
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.list_anime_list_grid.view.*
import type.ScoreFormat

class UserAnimeListGridRvAdapter(private val context: Context,
                                 private val list: List<UserMediaListCollectionQuery.Entry?>,
                                 private val scoreFormat: ScoreFormat,
                                 private val listener: UserMediaListener
): RecyclerView.Adapter<UserAnimeListGridRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_anime_list_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mediaList = list[position]!!

        GlideApp.with(context).load(mediaList.media?.coverImage?.large).into(holder.animeCoverImage)
        holder.animeTitleText.text = mediaList.media?.title?.userPreferred
        holder.animeFormatText.text = mediaList.media?.format?.name?.replace('_', ' ')

        if (mediaList.media?.nextAiringEpisode != null) {
            holder.animeAiringLayout.visibility = View.VISIBLE
            GlideApp.with(context).load(R.drawable.ic_filled_circle).into(holder.animeAiringIcon)
        } else {
            holder.animeAiringLayout.visibility = View.GONE
        }

        if (scoreFormat == ScoreFormat.POINT_3) {
            GlideApp.with(context).load(AndroidUtility.getSmileyFromScore(mediaList.score)).into(holder.animeStarIcon)
            holder.animeStarIcon.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
            holder.animeRatingText.text = ""
        } else {
            GlideApp.with(context).load(R.drawable.ic_star_filled).into(holder.animeStarIcon)
            holder.animeStarIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellowStar))
            holder.animeRatingText.text = mediaList.score?.removeTrailingZero()
        }

        holder.animeProgressText.text = "${mediaList.progress}/${mediaList.media?.episodes ?: '?'}"

        holder.animeAiringLayout.setOnClickListener {
            DialogUtility.showToast(
                context,
                "Ep ${mediaList.media?.nextAiringEpisode?.episode} on ${mediaList.media?.nextAiringEpisode?.airingAt?.secondsToDateTime()}.",
                Toast.LENGTH_LONG
            )
        }

        holder.animeProgressLayout.isEnabled = false
        holder.animeScoreLayout.isEnabled = false

        holder.animeCoverImage.setOnClickListener {
            listener.openSelectedMedia(mediaList.media?.id!!, mediaList.media.type!!)
        }

        holder.animeCoverImage.setOnLongClickListener {
            listener.viewMediaListDetail(mediaList.id)
            true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val animeTitleText = view.animeTitleText!!
        val animeTitleLayout = view.animeTitleLayout!!
        val animeCoverImage = view.animeCoverImage!!
        val animeFormatText = view.animeFormatText!!
        val animeProgressText = view.animeProgressText!!
        val animeScoreLayout = view.animeScoreLayout!!
        val animeStarIcon = view.animeStarIcon!!
        val animeRatingText = view.animeRatingText!!
        val animeProgressLayout = view.animeProgressLayout!!
        val animeAiringLayout = view.animeAiringLayout!!
        val animeAiringIcon = view.animeAiringIcon!!
    }
}