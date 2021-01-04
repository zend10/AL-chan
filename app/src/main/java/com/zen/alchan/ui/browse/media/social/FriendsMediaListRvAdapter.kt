package com.zen.alchan.ui.browse.media.social

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.zen.alchan.R
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.roundToOneDecimal
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.list_friends_media_list.view.*
import type.MediaListStatus
import type.MediaType
import type.ScoreFormat

class FriendsMediaListRvAdapter(private val context: Context,
                                private val list: List<MediaSocialQuery.MediaList?>,
                                private val listener: FriendsMediaListListener
) : RecyclerView.Adapter<FriendsMediaListRvAdapter.ViewHolder>() {

    interface FriendsMediaListListener {
        fun passSelectedUser(userId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_friends_media_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        GlideApp.with(context).load(item?.user?.avatar?.medium).apply(RequestOptions.circleCropTransform()).into(holder.avatarImage)
        holder.nameText.text = item?.user?.name

        holder.statusText.text = if (item?.status == MediaListStatus.CURRENT) {
            if (item.media?.type == MediaType.ANIME) {
                context.getString(R.string.watching)
            } else {
                context.getString(R.string.reading)
            }
        } else {
            item?.status?.name?.toLowerCase()?.capitalize()
        }

        if (item?.score != null && item.score != 0.0) {
            holder.scoreLayout.visibility = View.VISIBLE
            when (item.user?.mediaListOptions?.scoreFormat) {
                ScoreFormat.POINT_3 -> {
                    holder.scoreIcon.visibility = View.VISIBLE
                    GlideApp.with(context).load(AndroidUtility.getSmileyFromScore(item.score)).into(holder.scoreIcon)
                    holder.scoreText.text = ""
                }
                ScoreFormat.POINT_5 -> {
                    holder.scoreIcon.visibility = View.VISIBLE
                    GlideApp.with(context).load(R.drawable.ic_star_filled).into(holder.scoreIcon)
                    holder.scoreText.text = item.score.roundToOneDecimal()
                }
                else -> {
                    holder.scoreIcon.visibility = View.INVISIBLE
                    val maxScore = when (item.user?.mediaListOptions?.scoreFormat) {
                        ScoreFormat.POINT_100 -> "100"
                        else -> "10"
                    }
                    holder.scoreText.text = "${item.score.roundToOneDecimal()}/${maxScore}"
                }
            }
        } else {
            holder.scoreLayout.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            if (item?.user?.id != null) {
                listener.passSelectedUser(item.user.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatarImage = view.avatarImage!!
        val nameText = view.nameText!!
        val statusText = view.statusText!!
        val scoreLayout = view.scoreLayout!!
        val scoreIcon = view.scoreIcon!!
        val scoreText = view.scoreText!!
    }
}