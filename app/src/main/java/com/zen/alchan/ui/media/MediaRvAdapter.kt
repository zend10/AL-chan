package com.zen.alchan.ui.media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.databinding.LayoutHorizontalListBinding
import com.zen.alchan.databinding.LayoutMediaInfoBinding
import com.zen.alchan.databinding.LayoutTitleAndListBinding
import com.zen.alchan.databinding.LayoutTitleAndTextBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.MediaItem
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import com.zen.alchan.ui.common.TextRvAdapter

class MediaRvAdapter(
    private val context: Context,
    list: List<MediaItem>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: MediaListener
) : BaseRecyclerViewAdapter<MediaItem, ViewBinding>(list) {

    private var characterAdapter: MediaCharacterRvAdapter? = null
    private var synonymsAdapter: TextRvAdapter? = null
    private var studiosAdapter: TextRvAdapter? = null
    private var producersAdapter: TextRvAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            MediaItem.VIEW_TYPE_SYNOPSIS -> {
                val view = LayoutTitleAndTextBinding.inflate(inflater, parent, false)
                return SynopsisViewHolder(view)
            }
            MediaItem.VIEW_TYPE_CHARACTERS -> {
                val view = LayoutHorizontalListBinding.inflate(inflater, parent, false)
                characterAdapter = MediaCharacterRvAdapter(context, listOf(), appSetting, width, listener.mediaCharacterListener)
                view.horizontalListRecyclerView.adapter = characterAdapter
                view.horizontalListRecyclerView.addItemDecoration(SpaceItemDecoration(right = context.resources.getDimensionPixelSize(R.dimen.marginPageBig)))
                return CharacterViewHolder(view)
            }
            MediaItem.VIEW_TYPE_INFO -> {
                val view = LayoutMediaInfoBinding.inflate(inflater, parent, false)
                synonymsAdapter = TextRvAdapter(context, listOf())
                view.mediaInfoSynonymsRecyclerView.adapter = synonymsAdapter
                studiosAdapter = TextRvAdapter(context, listOf(), getTextListener())
                view.mediaInfoStudiosRecyclerView.adapter = studiosAdapter
                producersAdapter = TextRvAdapter(context, listOf(), getTextListener())
                view.mediaInfoProducersRecyclerView.adapter = producersAdapter
                return InfoViewHolder(view)
            }
            else -> {
                val view = LayoutTitleAndTextBinding.inflate(inflater, parent, false)
                return SynopsisViewHolder(view)
            }
        }
    }

    private fun getTextListener(): TextRvAdapter.TextListener {
        return object : TextRvAdapter.TextListener {
            override fun getText(text: String) {
                val studio = list.find { it.viewType == MediaItem.VIEW_TYPE_INFO }?.media?.studios?.edges?.find { it.node.name == text }?.node
                studio?.let {
                    listener.mediaStudioListener.navigateToStudio(it)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    inner class SynopsisViewHolder(private val binding: LayoutTitleAndTextBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.apply {
                itemTitle.show(true)
                itemTitle.text = context.getString(R.string.synopsis)
                MarkdownUtil.applyMarkdown(context, itemText, item.media.description)
            }
        }
    }

    inner class CharacterViewHolder(private val binding: LayoutHorizontalListBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.horizontalListTitle.text = context.getString(R.string.characters)
            binding.horizontalListSeeMore.clicks { listener.mediaCharacterListener.navigateToMediaCharacter(item.media) }
            characterAdapter?.updateData(item.media.characters.nodes)
        }
    }

    inner class InfoViewHolder(private val binding: LayoutMediaInfoBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.apply {
                mediaInfoRomajiText.text= item.media.title.romaji
                mediaInfoRomajiText.clicks {

                }
                mediaInfoEnglishText.text = item.media.title.english
                mediaInfoEnglishText.clicks {

                }
                mediaInfoNativeText.text = item.media.title.romaji
                mediaInfoNativeText.clicks {

                }

                synonymsAdapter?.updateData(item.media.synonyms)

                mediaInfoFormatText.text = item.media.getFormattedMediaFormat(true)

                mediaInfoLengthLabel.text = when (item.media.type?.getMediaType()) {
                    MediaType.ANIME -> context.getString(R.string.episodes)
                    MediaType.MANGA -> context.getString(R.string.chapters)
                    else -> context.getString(R.string.episodes)
                }
                mediaInfoLengthText.text = item.media.getLength()?.getNumberFormatting() ?: "?"

                mediaInfoDurationLabel.text = when (item.media.type?.getMediaType()) {
                    MediaType.ANIME -> context.getString(R.string.episode_duration)
                    MediaType.MANGA -> context.getString(R.string.volumes)
                    else -> context.getString(R.string.episode_duration)
                }
                mediaInfoDurationText.text = when (item.media.type?.getMediaType()) {
                    MediaType.ANIME -> item.media.duration?.showUnit(context, R.plurals.minute) ?: "?"
                    MediaType.MANGA -> item.media.volumes?.getNumberFormatting() ?: "?"
                    else -> item.media.duration?.showUnit(context, R.plurals.minute) ?: "?"
                }
                mediaInfoDurationLayout.show(mediaInfoDurationText.text != "?")

                mediaInfoSourceText.text = item.media.source?.getString()

                mediaInfoStatusText.text = item.media.status?.getString()

                val startDate = TimeUtil.getReadableDateFromFuzzyDate(item.media.startDate)
                mediaInfoStartDateText.text = if (startDate == "-") "?" else startDate

                val endDate = TimeUtil.getReadableDateFromFuzzyDate(item.media.endDate)
                mediaInfoEndDateText.text = if (endDate == "-") "?" else endDate

                mediaInfoSeasonLayout.show(item.media.type == type.MediaType.ANIME && item.media.season != null && item.media.seasonYear != null)
                mediaInfoSeasonText.text = "${item.media.season?.getString()} ${item.media.seasonYear}"
                mediaInfoSeasonText.clicks {

                }

                studiosAdapter?.updateData(item.media.studios.edges.filter { it.isMain }.map { it.node.name })
                producersAdapter?.updateData(item.media.studios.edges.filter { !it.isMain }.map { it.node.name })
            }
        }
    }
}