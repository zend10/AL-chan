package com.zen.alchan.ui.medialist

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.databinding.ListMediaListGridBinding
import com.zen.databinding.ListTitleBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.helper.utils.ImageUtil

class MediaListGridRvAdapter(
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
                    listener.navigateToMedia(media)
                }
                mediaListTitleText.clicks {
                    listener.navigateToMedia(media)
                }
                mediaListTitleText.setOnLongClickListener {
                    listener.copyMediaTitle(getTitle(media))
                    true
                }

                // format
                mediaListFormatText.text = getFormat(media)
                mediaListFormatLayout.show(shouldShowMediaFormat())

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
                    if (!isViewer) {
                        listener.showQuickDetail(mediaList)
                        return@clicks
                    }

                    listener.showScoreDialog(mediaList)
                }

                // progress
                mediaListProgressText.text = getProgressText(mediaList)
                mediaListProgressLayout.show(shouldShowProgress(media))
                mediaListProgressLayout.clicks {
                    if (!isViewer) {
                        listener.showQuickDetail(mediaList)
                        return@clicks
                    }

                    listener.showProgressDialog(mediaList, false)
                }

                mediaListProgressVolumeText.text = getProgressVolumeText(mediaList)
                mediaListProgressVolumeLayout.show(shouldShowProgressVolume(media))
                mediaListProgressVolumeLayout.clicks {
                    if (!isViewer) {
                        listener.showQuickDetail(mediaList)
                        return@clicks
                    }

                    listener.showProgressDialog(mediaList, true)
                }

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
                mediaListProgressVolumeText.setTextColor(primaryColor)

                val secondaryColor = listStyle.getSecondaryColor(context)
                mediaListAiringIndicator.imageTintList = ColorStateList.valueOf(secondaryColor)

                val textColor = listStyle.getTextColor(context)
                mediaListFormatText.setTextColor(textColor)
                mediaListNotesIcon.imageTintList = ColorStateList.valueOf(textColor)

                val cardColor = listStyle.getCardColor(context)
                mediaListCardBackground.setCardBackgroundColor(cardColor)
                mediaListTitleLayout.setCardBackgroundColor(getTransparentCardColor())
                mediaListScoreLayout.setCardBackgroundColor(getTransparentCardColor())
                mediaListProgressLayout.setCardBackgroundColor(getTransparentCardColor())
                mediaListProgressVolumeLayout.setCardBackgroundColor(getTransparentCardColor())
                mediaListFormatLayout.setCardBackgroundColor(getTransparentCardColor())
                mediaListAiringLayout.setCardBackgroundColor(getTransparentCardColor())
                mediaListNotesLayout.setCardBackgroundColor(getTransparentCardColor())
            }
        }
    }
}