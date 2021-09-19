package com.zen.alchan.ui.medialist

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.entitiy.ListStyle
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.databinding.ListMediaListGridBinding
import com.zen.alchan.databinding.ListTitleBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.getString
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.helper.utils.ImageUtil

class MediaListGridRvAdapter(
    private val context: Context,
    list: List<MediaListItem>,
    appSetting: AppSetting,
    listStyle: ListStyle,
    mediaListOptions: MediaListOptions,
    private val listener: MediaListListener
) : BaseMediaListRvAdapter(context, list, appSetting, listStyle, mediaListOptions) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MediaListItem.VIEW_TYPE_TITLE -> {
                val view = ListTitleBinding.inflate(inflater, parent, false)
                TitleViewHolder(view)
            }
            else -> {
                val view = ListMediaListGridBinding.inflate(inflater, parent, false)
                ListItemViewHolder(view)
            }
        }
    }

    inner class ListItemViewHolder(private val binding: ListMediaListGridBinding) : ItemViewHolder(binding) {
        override fun bind(item: MediaListItem, index: Int) {
            val mediaList = item.mediaList
            val media = mediaList.media
            binding.apply {
                // cover
                ImageUtil.loadImage(context, getCoverImage(media), mediaListCoverImage)

                // title
                mediaListTitleText.text = getTitle(media)
                mediaListTitleLayout.clicks {

                }

                // format
                mediaListFormatText.text = getFormat(media)

                // airing indicator
                mediaListAiringLayout.show(shouldShowAiringIndicator(media))
                mediaListAiringIndicator.setImageResource(getAiringIndicatorIcon(mediaList))
                mediaListAiringLayout.clicks {
                    listener.showAiringText(getAiringText(mediaList))
                }

                // notes
                mediaListNotesLayout.show(shouldShowNotes(mediaList))
                mediaListNotesLayout.clicks {
                    listener.showNotes(mediaList)
                }

                // priority
                mediaListPriority.show(shouldShowPriority(mediaList))
                mediaListPriority.backgroundTintList = ColorStateList.valueOf(getPriorityColor(mediaList))

                // score
                handleScoring(mediaListScoreLayout, mediaListScoreIcon, mediaListScoreText, mediaListScoreSmiley, mediaList)
                mediaListScoreLayout.clicks {

                }

                // progress
                mediaListProgressText.text = getProgressText(mediaList)
                mediaListProgressLayout.show(shouldShowProgress(media))
                mediaListProgressLayout.clicks {

                }

                mediaListProgressVolumeText.text = getProgressVolumeText(mediaList)
                mediaListProgressVolumeLayout.show(shouldShowProgressVolume(media))
                mediaListProgressVolumeLayout.clicks {

                }
            }
        }
    }
}