package com.zen.alchan.ui.mangalist.list

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
import com.zen.alchan.helper.removeTrailingZero
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.list_manga_list_grid.view.*
import type.MediaFormat
import type.ScoreFormat

class MangaListGridRvAdapter(private val context: Context,
                             private val list: List<MediaList>,
                             private val scoreFormat: ScoreFormat,
                             private val listStyle: ListStyle?,
                             private val listener: MangaListListener
) : RecyclerView.Adapter<MangaListGridRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_manga_list_grid, parent, false)
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
            holder.mangaRatingText.text = if (mediaList.score == null || mediaList.score?.toInt() == 0) {
                "?"
            } else {
                mediaList.score?.removeTrailingZero()
            }
        }

        holder.mangaProgressText.text = "Ch. ${mediaList.progress}/${mediaList.media?.chapters ?: '?'}"
        holder.mangaProgressVolumesText.text = "Vo. ${mediaList.progressVolumes}/${mediaList.media?.volumes ?: '?'}"

        if (mediaList.media?.format == MediaFormat.MANGA || mediaList.media?.format == MediaFormat.ONE_SHOT) {
            holder.mangaProgressVolumesLayout.visibility = if (listStyle?.hideMangaVolume == true) View.GONE else View.VISIBLE
            holder.mangaProgressLayout.visibility = if (listStyle?.hideMangaChapter == true) View.GONE else View.VISIBLE
        } else if (mediaList.media?.format == MediaFormat.NOVEL) {
            holder.mangaProgressVolumesLayout.visibility = if (listStyle?.hideNovelVolume == true) View.GONE else View.VISIBLE
            holder.mangaProgressLayout.visibility = if (listStyle?.hideNovelChapter == true) View.GONE else View.VISIBLE
        }

        holder.mangaProgressLayout.setOnClickListener {
            listener.openProgressDialog(mediaList)
        }

        holder.mangaProgressVolumesLayout.setOnClickListener {
            listener.openProgressDialog(mediaList, true)
        }

        holder.mangaScoreLayout.setOnClickListener {
            listener.openScoreDialog(mediaList)
        }

        holder.mangaCoverImage.setOnClickListener {
            listener.openEditor(mediaList.id)
        }

        holder.mangaTitleLayout.setOnClickListener {
            listener.openBrowsePage(mediaList.media!!)
        }

        holder.mangaCoverImage.setOnLongClickListener {
            if (listStyle?.longPressViewDetail == true) {
                listener.showDetail(mediaList.id)
                true
            } else {
                false
            }
        }

        if (listStyle?.showNotesIndicator == true && !mediaList.notes.isNullOrBlank()) {
            holder.mangaNotesLayout.visibility = View.VISIBLE
            holder.mangaNotesLayout.setOnClickListener {
                DialogUtility.showToast(context, mediaList.notes, Toast.LENGTH_LONG)
            }
        } else {
            holder.mangaNotesLayout.visibility = View.GONE
        }

        if (listStyle?.showPriorityIndicator == true && mediaList.priority != null && mediaList.priority != 0) {
            holder.mangaPriorityIndicator.visibility = View.VISIBLE
            holder.mangaPriorityIndicator.backgroundTintList = ColorStateList.valueOf(Constant.PRIORITY_COLOR_MAP[mediaList.priority!!]!!)
        } else {
            holder.mangaPriorityIndicator.visibility = View.GONE
        }

        if (listStyle?.cardColor != null) {
            holder.listCardBackground.setCardBackgroundColor(Color.parseColor(listStyle.cardColor))

            // 6 is color hex code length without the alpha
            val transparentCardColor = Color.parseColor("#CC" + listStyle.cardColor?.substring(listStyle.cardColor?.length!! - 6))
            holder.mangaTitleLayout.setCardBackgroundColor(transparentCardColor)
            holder.mangaFormatLayout.setCardBackgroundColor(transparentCardColor)
            holder.mangaScoreLayout.setCardBackgroundColor(transparentCardColor)
            holder.mangaProgressLayout.setCardBackgroundColor(transparentCardColor)
            holder.mangaProgressVolumesLayout.setCardBackgroundColor(transparentCardColor)
            holder.mangaNotesLayout.setCardBackgroundColor(transparentCardColor)
        }

        if (listStyle?.primaryColor != null) {
            holder.mangaTitleText.setTextColor(Color.parseColor(listStyle.primaryColor))
            holder.mangaRatingText.setTextColor(Color.parseColor(listStyle.primaryColor))
            holder.mangaProgressText.setTextColor(Color.parseColor(listStyle.primaryColor))
            holder.mangaProgressVolumesText.setTextColor(Color.parseColor(listStyle.primaryColor))
            if (scoreFormat == ScoreFormat.POINT_3) {
                holder.mangaStarIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(listStyle.primaryColor))
            }
        }

        if (listStyle?.textColor != null) {
            holder.mangaFormatText.setTextColor(Color.parseColor(listStyle.textColor))
            holder.mangaNotesIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(listStyle.textColor))
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
        val mangaPriorityIndicator = view.mangaPriorityIndicator!!
        val mangaNotesLayout = view.mangaNotesLayout!!
        val mangaNotesIcon = view.mangaNotesIcon!!
    }
}