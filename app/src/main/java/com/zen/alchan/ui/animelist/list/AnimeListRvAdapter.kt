package com.zen.alchan.ui.animelist.list

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.response.MediaList
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.roundToOneDecimal
import com.zen.alchan.helper.secondsToDateTime
import com.zen.alchan.helper.setRegularPlural
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.list_anime_list_linear.view.*
import type.ScoreFormat

class AnimeListRvAdapter(private val context: Context,
                         private val list: List<MediaList>,
                         private val scoreFormat: ScoreFormat,
                         private val listStyle: ListStyle?,
                         private val listener: AnimeListListener
) : RecyclerView.Adapter<AnimeListRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_anime_list_linear, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mediaList = list[position]

        GlideApp.with(context).load(mediaList.media?.coverImage?.large).into(holder.animeCoverImage)
        holder.animeTitleText.text = mediaList.media?.title?.userPreferred
        holder.animeFormatText.text = mediaList.media?.format?.name?.replace('_', ' ')

        if (mediaList.media?.nextAiringEpisode != null) {
            holder.animeAiringDividerIcon.visibility = View.VISIBLE
            holder.animeAiringDateText.visibility = View.VISIBLE

            var nextEpisodeMessage = "Ep ${mediaList.media?.nextAiringEpisode?.episode} on ${mediaList.media?.nextAiringEpisode?.airingAt?.secondsToDateTime()}"
            val epDiff = mediaList.media?.nextAiringEpisode?.episode!! - mediaList.progress!!
            if (epDiff > 1) {
                nextEpisodeMessage += ". You are ${epDiff - 1} ${"episode".setRegularPlural(epDiff - 1)} behind."
            }
            holder.animeAiringDateText.text = nextEpisodeMessage
        } else {
            holder.animeAiringDividerIcon.visibility = View.GONE
            holder.animeAiringDateText.visibility = View.GONE
        }

        if (scoreFormat == ScoreFormat.POINT_3) {
            GlideApp.with(context).load(AndroidUtility.getSmileyFromScore(mediaList.score)).into(holder.animeStarIcon)
            holder.animeStarIcon.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
            holder.animeRatingText.text = ""
        } else {
            GlideApp.with(context).load(R.drawable.ic_star_filled).into(holder.animeStarIcon)
            holder.animeStarIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellowStar))
            holder.animeRatingText.text = if (mediaList.score == null || mediaList.score?.toInt() == 0) {
                "?"
            } else {
                mediaList.score?.roundToOneDecimal()
            }
        }

        holder.animeProgressBar.progress = if (mediaList.media?.episodes == null && mediaList.progress!! > 0) {
            50
        } else if (mediaList.media?.episodes != null && mediaList.media?.episodes != 0) {
            (mediaList.progress!!.toDouble() / mediaList.media?.episodes!!.toDouble() * 100).toInt()
        } else {
            0
        }

        holder.animeProgressText.text = "${mediaList.progress}/${mediaList.media?.episodes ?: '?'}"
        holder.animeIncrementProgressButton.visibility = if ((mediaList.media?.episodes == null || mediaList.media?.episodes!! > mediaList.progress!!) && mediaList.progress!! < UShort.MAX_VALUE.toInt()) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            listener.openEditor(mediaList.id)
        }

        holder.animeIncrementProgressButton.setOnClickListener {
            listener.incrementProgress(mediaList)
        }

        holder.animeProgressText.setOnClickListener {
            listener.openProgressDialog(mediaList)
        }

        holder.animeRatingText.setOnClickListener {
            listener.openScoreDialog(mediaList)
        }

        holder.animeStarIcon.setOnClickListener {
            listener.openScoreDialog(mediaList)
        }

        holder.animeTitleText.setOnClickListener {
            listener.openBrowsePage(mediaList.media!!)
        }

        holder.itemView.setOnLongClickListener {
            if (listStyle?.longPressViewDetail == true) {
                listener.showDetail(mediaList.id)
                true
            } else {
                false
            }
        }

        if (listStyle?.showNotesIndicator == true && !mediaList.notes.isNullOrBlank()) {
            holder.animeNotesLayout.visibility = View.VISIBLE
            holder.animeNotesLayout.setOnClickListener {
                DialogUtility.showToast(context, mediaList.notes, Toast.LENGTH_LONG)
            }
        } else {
            holder.animeNotesLayout.visibility = View.GONE
        }

        if (listStyle?.showPriorityIndicator == true && mediaList.priority != null && mediaList.priority != 0) {
            holder.animePriorityIndicator.visibility = View.VISIBLE
            holder.animePriorityIndicator.setBackgroundColor(Constant.PRIORITY_COLOR_MAP[mediaList.priority!!]!!)
        } else {
            holder.animePriorityIndicator.visibility = View.GONE
        }

        if (listStyle?.cardColor != null) {
            holder.listCardBackground.setCardBackgroundColor(Color.parseColor(listStyle.cardColor))

            // 6 is color hex code length without the alpha
            val transparentCardColor =  "#CC" + listStyle.cardColor?.substring(listStyle.cardColor?.length!! - 6)
            holder.animeNotesLayout.setCardBackgroundColor(Color.parseColor(transparentCardColor))
        }

        if (listStyle?.primaryColor != null) {
            holder.animeTitleText.setTextColor(Color.parseColor(listStyle.primaryColor))
            holder.animeRatingText.setTextColor(Color.parseColor(listStyle.primaryColor))
            holder.animeProgressText.setTextColor(Color.parseColor(listStyle.primaryColor))
            holder.animeIncrementProgressButton.strokeColor = ColorStateList.valueOf(Color.parseColor(listStyle.primaryColor))
            holder.animeIncrementProgressButton.setTextColor(Color.parseColor(listStyle.primaryColor))

            if (scoreFormat == ScoreFormat.POINT_3) {
                holder.animeStarIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(listStyle.primaryColor))
            }
        }

        if (listStyle?.secondaryColor != null) {
            holder.animeAiringDateText.setTextColor(Color.parseColor(listStyle.secondaryColor))
            holder.animeProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor(listStyle.secondaryColor))
            val transparentSecondary = listStyle.secondaryColor?.substring(0, 1) + "80" + listStyle.secondaryColor?.substring(1)
            holder.animeProgressBar.progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor(transparentSecondary))
        }

        if (listStyle?.textColor != null) {
            holder.animeFormatText.setTextColor(Color.parseColor(listStyle.textColor))
            holder.animeAiringDividerIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(listStyle.textColor))
            holder.animeNotesIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(listStyle.textColor))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listCardBackground = view.listCardBackground!!
        val animeTitleText = view.animeTitleText!!
        val animeCoverImage = view.animeCoverImage!!
        val animeFormatText = view.animeFormatText!!
        val animeAiringDividerIcon = view.animeAiringDividerIcon!!
        val animeAiringDateText = view.animeAiringDateText!!
        val animeStarIcon = view.animeStarIcon!!
        val animeRatingText = view.animeRatingText!!
        val animeProgressBar = view.animeProgressBar!!
        val animeProgressText = view.animeProgressText!!
        val animeIncrementProgressButton = view.animeIncrementProgressButton!!
        val animeNotesLayout = view.animeNotesLayout!!
        val animeNotesIcon = view.animeNotesIcon!!
        val animePriorityIndicator = view.animePriorityIndicator!!
    }
}