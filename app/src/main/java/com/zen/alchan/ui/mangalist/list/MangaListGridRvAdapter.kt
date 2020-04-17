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
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_manga_list_grid.view.*
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

        GlideApp.with(context).load(mediaList.media?.coverImage?.extraLarge).into(holder.mangaCoverImage)
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

        holder.mangaProgressText.text = "${mediaList.progress}/${mediaList.media?.chapters ?: '?'}"

        holder.mangaProgressLayout.setOnClickListener {
            listener.openProgressDialog(mediaList)
        }

        holder.mangaScoreLayout.setOnClickListener {
            listener.openScoreDialog(mediaList)
        }

        holder.mangaCoverImage.setOnClickListener {
            listener.openEditor(mediaList.id)
        }

        holder.mangaCoverImage.setOnLongClickListener {
            listener.openBrowsePage(mediaList.media!!)
            true
        }

        if (listStyle?.cardColor != null) {
            holder.listCardBackground.setCardBackgroundColor(Color.parseColor(listStyle.cardColor))

            // 6 is color hex code length without the alpha
            val transparentCardColor =  "#CC" + listStyle.cardColor?.substring(listStyle.cardColor?.length!! - 6)
            holder.mangaTitleLayout.setCardBackgroundColor(Color.parseColor(transparentCardColor))
            holder.mangaFormatLayout.setCardBackgroundColor(Color.parseColor(transparentCardColor))
        }

        if (listStyle?.primaryColor != null) {
            holder.mangaProgressText.setTextColor(Color.parseColor(listStyle.primaryColor))
            if (scoreFormat == ScoreFormat.POINT_3) {
                holder.mangaStarIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(listStyle.primaryColor))
            }
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
        val mangaTitleLayout = view.mangaTitleLayout!!
        val mangaCoverImage = view.mangaCoverImage!!
        val mangaFormatText = view.mangaFormatText!!
        val mangaFormatLayout = view.mangaFormatLayout!!
        val mangaProgressText = view.mangaProgressText!!
        val mangaScoreLayout = view.mangaScoreLayout!!
        val mangaStarIcon = view.mangaStarIcon!!
        val mangaRatingText = view.mangaRatingText!!
        val mangaProgressLayout = view.mangaProgressLayout!!
    }
}