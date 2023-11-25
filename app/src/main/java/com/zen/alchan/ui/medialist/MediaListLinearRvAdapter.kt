package com.zen.alchan.ui.medialist

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.databinding.ListMediaListLinearBinding
import com.zen.databinding.ListTitleBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.type.MediaType

class MediaListLinearRvAdapter(
    context: Context,
    list: List<MediaListItem>,
    isViewer: Boolean,
    appSetting: AppSetting,
    listStyle: ListStyle,
    mediaListOptions: MediaListOptions,
    private val listener: MediaListListener
) : BaseMediaListRvAdapter(context, list, isViewer, appSetting, listStyle, mediaListOptions) {

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
                    listener.navigateToMedia(media)
                }

                // title
                mediaListTitleText.text = getTitle(media)
                mediaListTitleText.clicks {
                    listener.navigateToMedia(media)
                }
                mediaListTitleText.setOnLongClickListener {
                    listener.copyMediaTitle(getTitle(media))
                    true
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
                    if (!isViewer) {
                        listener.showQuickDetail(mediaList)
                        return@clicks
                    }

                    listener.showScoreDialog(mediaList)
                }

                // progress
                mediaListProgressText.text = getProgressText(mediaList)
                mediaListIncrementProgressButton.text = getProgressButtonText(media)
                mediaListProgressText.show(shouldShowProgress(media))
                mediaListIncrementProgressButton.show(shouldShowIncrementButton(mediaList, false))
                mediaListProgressText.clicks {
                    if (!isViewer) {
                        listener.showQuickDetail(mediaList)
                        return@clicks
                    }

                    listener.showProgressDialog(mediaList, false)
                }
                mediaListIncrementProgressButton.clicks {
                    listener.incrementProgress(mediaList, mediaList.progress + 1, false)
                }

                mediaListProgressVolumeText.text = getProgressVolumeText(mediaList)
                mediaListProgressVolumeText.show(shouldShowProgressVolume(media))
                mediaListIncrementProgressVolumeButton.show(shouldShowIncrementButton(mediaList, true))
                mediaListProgressVolumeText.clicks {
                    if (!isViewer) {
                        listener.showQuickDetail(mediaList)
                        return@clicks
                    }

                    listener.showProgressDialog(mediaList, true)
                }
                mediaListIncrementProgressVolumeButton.clicks {
                    listener.incrementProgress(mediaList, (mediaList.progressVolumes ?: 0) + 1, true)
                }

                // progress bar
                mediaListProgressBar.max = getMaxProgressBar(mediaList)
                mediaListProgressBar.progress = mediaList.progress

                // root
                root.setOnLongClickListener {
                    if (shouldShowQuickDetail())
                        listener.showQuickDetail(mediaList)

                    true
                }

                root.clicks {
                    if (!isViewer) {
                        listener.navigateToMedia(media)
                        return@clicks
                    }

                    listener.navigateToListEditor(mediaList)
                }

                // theme
                val primaryColor = listStyle.getPrimaryColor(context)
                mediaListTitleText.setTextColor(primaryColor)
                mediaListScoreText.setTextColor(primaryColor)
                mediaListScoreSmiley.imageTintList = ColorStateList.valueOf(primaryColor)
                mediaListProgressText.setTextColor(primaryColor)
                mediaListIncrementProgressButton.setTextColor(primaryColor)
                mediaListIncrementProgressButton.strokeColor = ColorStateList.valueOf(primaryColor)
                mediaListProgressVolumeText.setTextColor(primaryColor)
                mediaListIncrementProgressVolumeButton.setTextColor(primaryColor)
                mediaListIncrementProgressVolumeButton.strokeColor = ColorStateList.valueOf(primaryColor)

                val secondaryColor = listStyle.getSecondaryColor(context)
                mediaListAiringText.setTextColor(secondaryColor)
                mediaListAiringIndicator.imageTintList = ColorStateList.valueOf(secondaryColor)
                mediaListProgressBar.progressTintList = ColorStateList.valueOf(secondaryColor)
                mediaListProgressBar.progressBackgroundTintList = ColorStateList.valueOf(getProgressBarBackgroundColor())

                val textColor = listStyle.getTextColor(context)
                mediaListFormatText.setTextColor(textColor)
                mediaListNotesIcon.imageTintList = ColorStateList.valueOf(textColor)

                val cardColor = listStyle.getCardColor(context)
                mediaListCardBackground.setCardBackgroundColor(cardColor)
                mediaListNotesLayout.setCardBackgroundColor(getTransparentCardColor())
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

        private fun getProgressBarBackgroundColor(): Int {
            return if (listStyle.secondaryColor != null)
                Color.parseColor("#80${listStyle.secondaryColor?.substring(1)}")
            else
                context.getAttrValue(R.attr.themeSecondaryTransparent50Color)
        }

        private fun shouldShowIncrementButton(mediaList: MediaList, isVolumeProgress: Boolean): Boolean {
            if (!isViewer)
                return false

            val isAtMaxProgress = when (mediaList.media.type) {
                MediaType.ANIME -> mediaList.media.episodes != null && mediaList.progress >= mediaList.media.episodes
                MediaType.MANGA -> if (isVolumeProgress) {
                    mediaList.media.volumes != null && (mediaList.progressVolumes ?: 0) >= mediaList.media.volumes
                } else {
                    mediaList.media.chapters != null && mediaList.progress >= mediaList.media.chapters
                }
                else -> false
            }
            return (if (isVolumeProgress) shouldShowProgressVolume(mediaList.media) else shouldShowProgress(mediaList.media)) && !isAtMaxProgress
        }
    }
}