package com.zen.alchan.ui.medialist

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.ListStyle
import com.zen.alchan.data.response.anilist.MediaListOptions
import com.zen.alchan.databinding.ListMediaListSimplifiedBinding
import com.zen.alchan.databinding.ListTitleBinding
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.helper.pojo.MediaListItem

class MediaListSimplifiedRvAdapter(
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
                val view = ListMediaListSimplifiedBinding.inflate(inflater, parent, false)
                ListItemViewHolder(view)
            }
        }
    }

    inner class ListItemViewHolder(private val binding: ListMediaListSimplifiedBinding) : ItemViewHolder(binding) {
        override fun bind(item: MediaListItem, index: Int) {
            val mediaList = item.mediaList
            val media = mediaList.media
            binding.apply {
                // title
                mediaListTitleText.text = getTitle(media)

                // airing indicator
                mediaListAiringIndicator.show(shouldShowAiringIndicator(media))
                mediaListAiringIndicator.setImageResource(getAiringIndicatorIcon(mediaList))
                mediaListAiringIndicator.clicks {
                    listener.showAiringText(getAiringText(mediaList))
                }

                // priority
                mediaListPriority.show(shouldShowPriority(mediaList))
                mediaListPriority.setBackgroundColor(getPriorityColor(mediaList))

                // score
                handleScoring(mediaListScoreLayout, mediaListScoreIcon, mediaListScoreText, mediaListScoreSmiley, mediaList)
                mediaListScoreLayout.clicks {
                    listener.showScoreDialog(mediaList)
                }

                // progress
                mediaListProgressText.text = getProgressText(mediaList)
                mediaListProgressText.show(shouldShowProgress(media))
                mediaListDummyProgressText.visibility = if (shouldShowProgress(media)) View.INVISIBLE else View.GONE
                mediaListProgressText.clicks {
                    listener.showProgressDialog(mediaList, false)
                }

                mediaListProgressVolumeText.text = getProgressVolumeText(mediaList)
                mediaListProgressVolumeText.show(shouldShowProgressVolume(media))
                mediaListDummyProgressVolumeText.visibility = if (shouldShowProgressVolume(media)) View.INVISIBLE else View.GONE
                mediaListProgressVolumeText.clicks {
                    listener.showProgressDialog(mediaList, true)
                }

                // root
                root.setOnLongClickListener {
                    if (shouldShowQuickDetail())
                        listener.showQuickDetail(mediaList)

                    true
                }

                root.clicks {
                    listener.navigateToListEditor(mediaList)
                }

                // theme
                val primaryColor = listStyle.getPrimaryColor(context)
                mediaListScoreText.setTextColor(primaryColor)
                mediaListScoreSmiley.imageTintList = ColorStateList.valueOf(primaryColor)
                mediaListProgressText.setTextColor(primaryColor)
                mediaListProgressVolumeText.setTextColor(primaryColor)

                val secondaryColor = listStyle.getSecondaryColor(context)
                mediaListAiringIndicator.imageTintList = ColorStateList.valueOf(secondaryColor)

                val textColor = listStyle.getTextColor(context)
                mediaListTitleText.setTextColor(textColor)

                val cardColor = listStyle.getCardColor(context)
                mediaListCardBackground.setCardBackgroundColor(cardColor)
            }
        }
    }
}