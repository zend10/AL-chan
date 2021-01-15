package com.zen.alchan.ui.browse.user.list

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
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.roundToOneDecimal
import com.zen.alchan.helper.secondsToDateTime
import com.zen.alchan.helper.setRegularPlural
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.list_anime_list_grid.view.*
import kotlinx.android.synthetic.main.list_title.view.*
import type.ScoreFormat

class UserAnimeListGridRvAdapter(private val context: Context,
                                 private val list: List<UserMediaListCollectionQuery.Entry?>,
                                 private val scoreFormat: ScoreFormat,
                                 private val userId: Int?,
                                 private val useRelativeDate: Boolean,
                                 private val listener: UserMediaListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_TITLE = 0
        const val VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_TITLE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_title, parent, false)
            TitleViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_anime_list_grid, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TitleViewHolder) {
            val item = list[position]
            holder.titleText.text = item?.notes
        } else if (holder is ItemViewHolder) {
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
                holder.animeRatingText.text = if (mediaList.score == null || mediaList.score.toInt() == 0) {
                    "?"
                } else {
                    mediaList.score.roundToOneDecimal()
                }
            }

            holder.animeProgressText.text = "${mediaList.progress}/${mediaList.media?.episodes ?: '?'}"

            holder.animeAiringLayout.setOnClickListener {
                val episode = mediaList.media?.nextAiringEpisode?.episode
                val timeUntilAiring = mediaList.media?.nextAiringEpisode?.timeUntilAiring ?: 0

                val message = if (useRelativeDate) {
                    when {
                        timeUntilAiring > 3600 * 24 -> {
                            context.getString(R.string.ep_in, episode, timeUntilAiring / 3600 / 24 + 1, context.getString(R.string.day).setRegularPlural(timeUntilAiring / 3600 / 24 + 1))
                        }
                        timeUntilAiring >= 3600 -> {
                            context.getString(R.string.ep_in, episode, timeUntilAiring / 3600, context.getString(R.string.hour).setRegularPlural(timeUntilAiring / 3600))
                        }
                        else -> {
                            context.getString(R.string.ep_in, episode, timeUntilAiring / 60, context.getString(R.string.minute).setRegularPlural(timeUntilAiring / 60))
                        }
                    }
                } else {
                    context.getString(R.string.ep_on, episode, mediaList.media?.nextAiringEpisode?.airingAt?.secondsToDateTime())
                }

                DialogUtility.showToast(context, message, Toast.LENGTH_LONG)
            }

            if (!mediaList.notes.isNullOrBlank()) {
                holder.animeNotesLayout.visibility = View.VISIBLE
                holder.animeNotesLayout.setOnClickListener {
                    DialogUtility.showInfoDialog(context, mediaList.notes)
                }
            } else {
                holder.animeNotesLayout.visibility = View.GONE
            }

            if (mediaList.priority != null && mediaList.priority != 0) {
                holder.animePriorityIndicator.visibility = View.VISIBLE
                holder.animePriorityIndicator.backgroundTintList = ColorStateList.valueOf(Constant.PRIORITY_COLOR_MAP[mediaList.priority]!!)
            } else {
                holder.animePriorityIndicator.visibility = View.GONE
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

            if (userId == Constant.EVA_ID) {
                holder.listCardBackground.setCardBackgroundColor(Color.parseColor("#80000000"))
            } else {
                holder.listCardBackground.setCardBackgroundColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeCardColor))
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position]?.id == 0) VIEW_TYPE_TITLE else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listCardBackground = view.listCardBackground!!
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
        val animePriorityIndicator = view.animePriorityIndicator!!
        val animeNotesLayout = view.animeNotesLayout!!
        val animeNotesIcon = view.animeNotesIcon!!
    }

    class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText = view.titleText!!
    }
}