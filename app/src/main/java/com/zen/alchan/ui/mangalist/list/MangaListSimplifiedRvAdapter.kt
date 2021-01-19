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
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.ListStyle
import com.zen.alchan.helper.roundToOneDecimal
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_manga_list_simplified.view.*
import kotlinx.android.synthetic.main.list_title.view.*
import type.MediaFormat
import type.ScoreFormat

class MangaListSimplifiedRvAdapter(private val context: Context,
                                   private val list: List<MediaList>,
                                   private val scoreFormat: ScoreFormat,
                                   private val listStyle: ListStyle?,
                                   private val listener: MangaListListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_TITLE = 0
        const val VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_TITLE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_title, parent, false)
            TitleViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_manga_list_simplified, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TitleViewHolder) {
            val item = list[position]
            holder.titleText.text = item.notes
            if (listStyle?.textColor != null) {
                holder.titleText.setTextColor(Color.parseColor(listStyle.textColor))
            }
        } else if (holder is ItemViewHolder) {
            val mediaList = list[position]

            holder.mangaTitleText.text = mediaList.media?.title?.userPreferred

            if (scoreFormat == ScoreFormat.POINT_3) {
                GlideApp.with(context).load(AndroidUtility.getSmileyFromScore(mediaList.score)).into(holder.mangaStarIcon)
                holder.mangaStarIcon.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                holder.mangaRatingText.text = ""
                holder.mangaDummyRatingText.visibility = View.GONE
            } else {
                GlideApp.with(context).load(R.drawable.ic_star_filled).into(holder.mangaStarIcon)
                holder.mangaStarIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yellowStar))
                holder.mangaRatingText.text = if (mediaList.score == null || mediaList.score?.toInt() == 0) {
                    "?"
                } else {
                    mediaList.score?.roundToOneDecimal()
                }
                holder.mangaDummyRatingText.visibility = View.INVISIBLE
            }

            holder.mangaProgressVolumesText.text = "${mediaList.progressVolumes}/${mediaList.media?.volumes ?: '?'}"

            holder.mangaProgressText.text = "${mediaList.progress}/${mediaList.media?.chapters ?: '?'}"

            if (mediaList.media?.format == MediaFormat.MANGA || mediaList.media?.format == MediaFormat.ONE_SHOT) {
                holder.mangaProgressVolumesText.visibility = if (listStyle?.hideMangaVolume == true) View.GONE else View.VISIBLE
                holder.mangaDummyProgressVolumesText.visibility = if (listStyle?.hideMangaVolume == true) View.GONE else View.INVISIBLE
                holder.mangaProgressText.visibility = if (listStyle?.hideMangaChapter == true) View.GONE else View.VISIBLE
                holder.mangaDummyProgressText.visibility = if (listStyle?.hideMangaChapter == true) View.GONE else View.INVISIBLE
            } else if (mediaList.media?.format == MediaFormat.NOVEL) {
                holder.mangaProgressVolumesText.visibility = if (listStyle?.hideNovelVolume == true) View.GONE else View.VISIBLE
                holder.mangaDummyProgressVolumesText.visibility = if (listStyle?.hideNovelVolume == true) View.GONE else View.INVISIBLE
                holder.mangaProgressText.visibility = if (listStyle?.hideNovelChapter == true) View.GONE else View.VISIBLE
                holder.mangaDummyProgressText.visibility = if (listStyle?.hideNovelChapter == true) View.GONE else View.INVISIBLE
            }

            holder.itemView.setOnClickListener {
                listener.openEditor(mediaList.id)
            }

            holder.mangaProgressText.setOnClickListener {
                listener.openProgressDialog(mediaList)
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

            holder.itemView.setOnLongClickListener {
                if (listStyle?.longPressViewDetail == true) {
                    listener.showDetail(mediaList.id)
                    true
                } else {
                    false
                }
            }

            if (listStyle?.showPriorityIndicator == true && mediaList.priority != null && mediaList.priority != 0) {
                holder.mangaPriorityIndicator.visibility = View.VISIBLE
                holder.mangaPriorityIndicator.setBackgroundColor(Constant.PRIORITY_COLOR_MAP[mediaList.priority!!]!!)
            } else {
                holder.mangaPriorityIndicator.visibility = View.GONE
            }

            if (listStyle?.cardColor != null) {
                holder.listCardBackground.setCardBackgroundColor(Color.parseColor(listStyle.cardColor))
            }

            if (listStyle?.primaryColor != null) {
                holder.mangaRatingText.setTextColor(Color.parseColor(listStyle.primaryColor))
                holder.mangaProgressText.setTextColor(Color.parseColor(listStyle.primaryColor))
                holder.mangaProgressVolumesText.setTextColor(Color.parseColor(listStyle.primaryColor))
                if (scoreFormat == ScoreFormat.POINT_3) {
                    holder.mangaStarIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(listStyle.primaryColor))
                }
            }

            if (listStyle?.textColor != null) {
                holder.mangaTitleText.setTextColor(Color.parseColor(listStyle.textColor))
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].id == 0) VIEW_TYPE_TITLE else VIEW_TYPE_ITEM
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listCardBackground = view.listCardBackground!!
        val mangaTitleText = view.mangaTitleText!!
        val mangaStarIcon = view.mangaStarIcon!!
        val mangaRatingText = view.mangaRatingText!!
        val mangaDummyRatingText = view.mangaDummyRatingText!!
        val mangaProgressText = view.mangaProgressText!!
        val mangaDummyProgressText = view.mangaDummyProgressText!!
        val mangaProgressVolumesText = view.mangaProgressVolumesText!!
        val mangaDummyProgressVolumesText = view.mangaDummyProgressVolumesText!!
        val mangaPriorityIndicator = view.mangaPriorityIndicator!!
    }

    class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText = view.titleText!!
    }
}