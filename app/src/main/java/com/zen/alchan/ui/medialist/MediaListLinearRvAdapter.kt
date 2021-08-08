package com.zen.alchan.ui.medialist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.databinding.ListMediaListLinearBinding
import com.zen.alchan.databinding.ListTextBinding
import com.zen.alchan.databinding.ListTitleBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.MediaListItem
import com.zen.alchan.helper.utils.ImageUtil
import type.MediaType

class MediaListLinearRvAdapter(
    private val context: Context,
    list: List<MediaListItem>
) : BaseMediaListRvAdapter(context, list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MediaListItem.VIEW_TYPE_TITLE -> {
                val view = ListTitleBinding.inflate(inflater, parent, false)
                TitleViewHolder(view)
            }
            else -> {
                val view = ListMediaListLinearBinding.inflate(inflater, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    inner class ItemViewHolder(private val binding: ListMediaListLinearBinding) : ViewHolder(binding) {
        override fun bind(item: MediaListItem, index: Int) {
            val mediaList = item.mediaList
            val media = mediaList.media
            binding.apply {
                ImageUtil.loadImage(context, media.coverImage.extraLarge, mediaListCoverImage)

                mediaListTitleText.text = media.getTitle(appSetting)
                mediaListFormatText.text = media.getFormattedMediaFormat(true)

                // airing indicator
                mediaListAiringIndicator.show(shouldShowAiringLayout(media))
                mediaListAiringText.show(shouldShowAiringLayout(media))
                mediaListAiringText.text = generateAiringText(mediaList)

                // progress bar
                mediaListProgressBar.max = getMaxProgressBar(mediaList)
                mediaListProgressBar.progress = mediaList.progress

                // score
                mediaListScoreText.text = "${mediaList.score}"
                mediaListScoreSmiley.show(false)

                // progress
                mediaListProgressText.text = generateProgressAndMaxProgressText(mediaList)
                mediaListIncrementProgressButton.text = getProgressButtonText(media)

                // progress volume
                mediaListProgressVolumeText.text = generateProgressAndMaxProgressText(mediaList, true)
                mediaListProgressVolumeText.show(shouldShowVolumeProgress(media))
                mediaListIncrementProgressVolumeButton.show(shouldShowVolumeProgress(media))

                // events
                mediaListTitleText.clicks {  }
                mediaListCoverImage.clicks {  }
                mediaListScoreLayout.clicks {  }
                mediaListProgressText.clicks {  }
                mediaListIncrementProgressButton.clicks {  }
                mediaListProgressVolumeText.clicks {  }
                mediaListIncrementProgressVolumeButton.clicks {  }
            }
        }

        private fun shouldShowAiringLayout(media: Media): Boolean {
            return media.type == MediaType.ANIME && media.nextAiringEpisode != null
        }

        private fun generateAiringText(mediaList: MediaList): String {
            val nextAiringEpisode = mediaList.media.nextAiringEpisode ?: return ""
            val airingScheduleText = if (appSetting.useRelativeDateForNextAiringEpisode) {
                val secondsUntilAiring = nextAiringEpisode.timeUntilAiring
                val remainingTime = when {
                    secondsUntilAiring > 3600 * 24 -> {
                        secondsUntilAiring.convertSecondsToDays().showUnit(context, R.plurals.day)
                    }
                    secondsUntilAiring >= 3600 -> {
                        secondsUntilAiring.convertSecondsToHours().showUnit(context, R.plurals.hour)
                    }
                    else -> {
                        secondsUntilAiring.convertSecondsToMinutes().showUnit(context, R.plurals.minute)
                    }
                }
                context.getString(R.string.ep_x_in_y, nextAiringEpisode.episode, remainingTime)
            } else {
                context.getString(R.string.ep_x_on_y, nextAiringEpisode.episode, nextAiringEpisode.airingAt.displaySecondsInDayDateTimeFormat())
            }

            var episodesBehindText = ""
            val progressDifference = nextAiringEpisode.episode - mediaList.progress
            if (progressDifference > 1) {
                episodesBehindText += " "
                episodesBehindText += context.getString(R.string.you_are_x_behind, (progressDifference - 1).showUnit(context, R.plurals.episode))
            }

            return airingScheduleText + episodesBehindText
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

        fun generateProgressAndMaxProgressText(mediaList: MediaList, showVolumeProgress: Boolean = false): String {
            return when (mediaList.media.type) {
                MediaType.ANIME -> {
                    "${mediaList.progress} / ${mediaList.media.episodes ?: "?"}"
                }
                MediaType.MANGA -> {
                    if (showVolumeProgress)
                        "${mediaList.progressVolumes} / ${mediaList.media.volumes ?: "?"}"
                    else
                        "${mediaList.progress} / ${mediaList.media.chapters ?: "?"}"
                }
                else -> {
                    "${mediaList.progress} / ?"
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

        private fun shouldShowVolumeProgress(media: Media): Boolean {
            return media.type == MediaType.MANGA
        }
    }
}