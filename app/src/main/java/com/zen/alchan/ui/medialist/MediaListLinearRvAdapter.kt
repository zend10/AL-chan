package com.zen.alchan.ui.medialist

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.entitiy.AppSetting
import com.zen.alchan.data.entitiy.ListStyle
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.databinding.ListMediaListLinearBinding
import com.zen.alchan.databinding.ListTitleBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.helper.utils.ImageUtil
import type.MediaType

class MediaListLinearRvAdapter(
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
                val view = ListMediaListLinearBinding.inflate(inflater, parent, false)
                ListItemViewHolder(view)
            }
        }
    }

    inner class ListItemViewHolder(private val binding: ListMediaListLinearBinding) : ItemViewHolder(binding) {
        override fun bind(item: MediaListItem, index: Int) {
            val mediaList = item.mediaList
            val media = mediaList.media
            binding.apply {
                // cover
                ImageUtil.loadImage(context, getCoverImage(media), mediaListCoverImage)
                mediaListCoverImage.clicks {

                }

                // title
                mediaListTitleText.text = getTitle(media)
                mediaListTitleText.clicks {

                }

                // format
                mediaListFormatText.text = getFormat(media)

                // airing indicator
                mediaListAiringIndicator.show(shouldShowAiringIndicator(media))
                mediaListAiringText.show(shouldShowAiringIndicator(media))
                mediaListAiringText.text = getAiringText(mediaList)

                // notes
                mediaListNotesLayout.show(shouldShowNotes(mediaList))
                mediaListNotesLayout.clicks {
                    listener.showNotes(mediaList)
                }

                // priority
                mediaListPriority.show(shouldShowPriority(mediaList))
                mediaListPriority.setBackgroundColor(getPriorityColor(mediaList))

                // score
                handleScoring(mediaListScoreLayout, mediaListScoreIcon, mediaListScoreText, mediaListScoreSmiley, mediaList)
                mediaListScoreLayout.clicks {

                }

                // progress
                mediaListProgressText.text = getProgressText(mediaList)
                mediaListIncrementProgressButton.text = getProgressButtonText(media)
                mediaListProgressText.show(shouldShowProgress(media))
                mediaListIncrementProgressButton.show(shouldShowProgress(media))
                mediaListProgressText.clicks {

                }
                mediaListIncrementProgressButton.clicks {

                }

                mediaListProgressVolumeText.text = getProgressVolumeText(mediaList)
                mediaListProgressVolumeText.show(shouldShowProgressVolume(media))
                mediaListIncrementProgressVolumeButton.show(shouldShowProgressVolume(media))
                mediaListProgressVolumeText.clicks {

                }
                mediaListIncrementProgressVolumeButton.clicks {

                }

                // progress bar
                mediaListProgressBar.max = getMaxProgressBar(mediaList)
                mediaListProgressBar.progress = mediaList.progress


                // style
//                val cardColor = if (listStyle.cardColor != null)
//                    Color.parseColor(listStyle.cardColor)
//                else
//                    context.getAttrValue(R.attr.themeCardColor)
//
//                mediaListCardBackground.setCardBackgroundColor(cardColor)
            }
        }

        private fun getMaxProgressBar(mediaList: MediaList): Int {
            return when (mediaList.media.type) {
                MediaType.MANGA -> {
                    if (mediaList.media.chapters == null || mediaList.media.chapters == 0)
                        mediaList.progress * 2
                    else
                        mediaList.media.chapters
                }
                else -> {
                    if (mediaList.media.episodes == null || mediaList.media.episodes == 0)
                        mediaList.progress * 2
                    else
                        mediaList.media.episodes
                }
            }
        }

        private fun getProgressButtonText(media: Media): String {
            return when (media.type) {
                MediaType.MANGA -> {
                    context.getString(R.string.plus_one_ch)
                }
                else -> {
                    context.getString(R.string.plus_one_ep)
                }
            }
        }
    }
}