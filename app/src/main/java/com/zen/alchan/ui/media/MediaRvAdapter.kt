package com.zen.alchan.ui.media

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.flexbox.FlexboxLayoutManager
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.*
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.MediaItem
import com.zen.alchan.helper.utils.GridSpacingItemDecoration
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter
import com.zen.alchan.ui.common.GenreRvAdapter
import com.zen.alchan.ui.common.TextRvAdapter

class MediaRvAdapter(
    private val context: Context,
    list: List<MediaItem>,
    private val appSetting: AppSetting,
    private val width: Int,
    private val listener: MediaListener
) : BaseRecyclerViewAdapter<MediaItem, ViewBinding>(list) {

    private var genreAdapter: GenreRvAdapter? = null
    private var characterAdapter: MediaCharacterRvAdapter? = null
    private var synonymsAdapter: TextRvAdapter? = null
    private var studiosAdapter: TextRvAdapter? = null
    private var tagsAdapter: MediaTagsRvAdapter? = null
    private var producersAdapter: TextRvAdapter? = null
    private var serializationsAdapter: TextRvAdapter? = null
    private var staffAdapter: MediaStaffRvAdapter? = null
    private var relationsAdapter: MediaRelationsRvAdapter? = null
    private var recommendationsAdapter: MediaRecommendationsRvAdapter? = null
    private var linksAdapter: MediaLinksRvAdapter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            MediaItem.VIEW_TYPE_GENRE -> {
                val view = LayoutTitleAndListBinding.inflate(inflater, parent, false)
                genreAdapter = GenreRvAdapter(context, listOf(), object : GenreRvAdapter.GenreListener {
                    override fun getGenre(genre: Genre) {
                        listener.mediaGenreListener.navigateToExplore(genre)
                    }
                })
                view.listRecyclerView.adapter = genreAdapter
                view.listRecyclerView.layoutManager = FlexboxLayoutManager(context)
                return GenreViewHolder(view)
            }
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
                serializationsAdapter = TextRvAdapter(context, listOf())
                view.mediaInfoSerializationsRecyclerView.adapter = serializationsAdapter
                return InfoViewHolder(view)
            }
            MediaItem.VIEW_TYPE_TAGS -> {
                val view = LayoutTitleAndListBinding.inflate(inflater, parent, false)
                tagsAdapter = MediaTagsRvAdapter(context, listOf(), listener.mediaTagsListener)
                view.listRecyclerView.adapter = tagsAdapter
                view.listRecyclerView.layoutManager = GridLayoutManager(context, 2)
                view.listRecyclerView.addItemDecoration(GridSpacingItemDecoration(2, context.resources.getDimensionPixelSize(R.dimen.marginSmall), false))
                return TagsViewHolder(view)
            }
            MediaItem.VIEW_TYPE_STAFF -> {
                val view = LayoutHorizontalListBinding.inflate(inflater, parent, false)
                staffAdapter = MediaStaffRvAdapter(context, listOf(), appSetting, width, listener.mediaStaffListener)
                view.horizontalListRecyclerView.adapter = staffAdapter
                view.horizontalListRecyclerView.addItemDecoration(SpaceItemDecoration(right = context.resources.getDimensionPixelSize(R.dimen.marginPageBig)))
                return StaffViewHolder(view)
            }
            MediaItem.VIEW_TYPE_RELATIONS -> {
                val view = LayoutHorizontalListBinding.inflate(inflater, parent, false)
                relationsAdapter = MediaRelationsRvAdapter(context, listOf(), appSetting, width, listener.mediaRelationsListener)
                view.horizontalListRecyclerView.adapter = relationsAdapter
                view.horizontalListRecyclerView.addItemDecoration(SpaceItemDecoration(right = context.resources.getDimensionPixelSize(R.dimen.marginPageNormal)))
                return RelationsViewHolder(view)
            }
            MediaItem.VIEW_TYPE_RECOMMENDATIONS -> {
                val view = LayoutHorizontalListBinding.inflate(inflater, parent, false)
                recommendationsAdapter = MediaRecommendationsRvAdapter(context, listOf(), appSetting, width, listener.mediaRecommendationsListener)
                view.horizontalListRecyclerView.adapter = recommendationsAdapter
                view.horizontalListRecyclerView.addItemDecoration(SpaceItemDecoration(right = context.resources.getDimensionPixelSize(R.dimen.marginPageNormal)))
                return RecommendationsViewHolder(view)
            }
            MediaItem.VIEW_TYPE_LINKS -> {
                val view = LayoutTitleAndListBinding.inflate(inflater, parent, false)
                linksAdapter = MediaLinksRvAdapter(context, listOf(), listener.mediaLinksListener)
                view.listRecyclerView.adapter = linksAdapter
                view.listRecyclerView.layoutManager = FlexboxLayoutManager(context)
                return LinkViewHolder(view)
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

    inner class GenreViewHolder(private val binding: LayoutTitleAndListBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.titleLayout.show(false)
            genreAdapter?.updateData(item.media.genres)
        }
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
            binding.horizontalListSeeMore.clicks { listener.mediaCharacterListener.navigateToMediaCharacters(item.media) }
            characterAdapter?.updateData(item.media.characters.nodes)
        }
    }

    inner class InfoViewHolder(private val binding: LayoutMediaInfoBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.apply {
                val fallbackTitle = item.media.title.romaji
                mediaInfoRomajiText.text = item.media.title.romaji
                mediaInfoRomajiText.clicks {
                    listener.mediaInfoListener.copyTitle(item.media.title.romaji)
                }

                val englishTitle = item.media.title.english.ifBlank { fallbackTitle }
                mediaInfoEnglishText.text = englishTitle
                mediaInfoEnglishText.clicks {
                    listener.mediaInfoListener.copyTitle(englishTitle)
                }

                val nativeTitle = item.media.title.native.ifBlank { fallbackTitle }
                mediaInfoNativeText.text = nativeTitle
                mediaInfoNativeText.clicks {
                    listener.mediaInfoListener.copyTitle(nativeTitle)
                }

                synonymsAdapter?.updateData(item.media.synonyms.ifEmpty { listOf(fallbackTitle) })

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

                val studios = item.media.studios.edges.filter { it.isMain }.map { it.node.name }
                val producers = item.media.studios.edges.filter { !it.isMain }.map { it.node.name }
                val serializations = item.media.mangaSerialization?.map { it.name } ?: listOf()
                studiosAdapter?.updateData(studios)
                producersAdapter?.updateData(producers)
                serializationsAdapter?.updateData(serializations)
                mediaInfoStudiosLayout.show(studios.isNotEmpty())
                mediaInfoProducersLayout.show(producers.isNotEmpty())
                mediaInfoSerializationsLayout.show(serializations.isNotEmpty())
                mediaInfoDividerThree.root.show(studios.isNotEmpty() || producers.isNotEmpty() || serializations.isNotEmpty())
            }
        }
    }

    inner class TagsViewHolder(private val binding: LayoutTitleAndListBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.titleText.text = context.getString(R.string.tags)
            binding.seeMoreText.text = if (item.showSpoilerTags) context.getString(R.string.hide_spoilers) else context.getString(R.string.show_spoilers)
            binding.seeMoreText.setTextColor(context.getAttrValue(R.attr.themeSecondaryColor))
            binding.seeMoreText.clicks {
                listener.mediaTagsListener.shouldShowSpoilers(!item.showSpoilerTags)
            }
            binding.footnoteText.text = context.getString(R.string.long_press_to_see_tag_description)
            binding.footnoteText.show(true)
            tagsAdapter?.updateData(
                if (item.showSpoilerTags)
                    item.media.tags
                else
                    item.media.tags.filter { !it.isGeneralSpoiler && !it.isMediaSpoiler }
            )
        }
    }

    inner class StaffViewHolder(private val binding: LayoutHorizontalListBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.horizontalListTitle.text = context.getString(R.string.staff)
            binding.horizontalListSeeMore.clicks { listener.mediaStaffListener.navigateToMediaStaff(item.media) }
            staffAdapter?.updateData(item.media.getMainStaff())
        }
    }

    inner class RelationsViewHolder(private val binding: LayoutHorizontalListBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.horizontalListTitle.text = context.getString(R.string.relations)
            binding.horizontalListSeeMore.show(false)
            relationsAdapter?.updateData(item.media.relations.edges)
        }
    }

    inner class RecommendationsViewHolder(private val binding: LayoutHorizontalListBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.horizontalListTitle.text = context.getString(R.string.recommendations)
            binding.horizontalListSeeMore.show(false)
            recommendationsAdapter?.updateData(item.media.recommendations.nodes)
        }
    }

    inner class LinkViewHolder(private val binding: LayoutTitleAndListBinding) : ViewHolder(binding) {
        override fun bind(item: MediaItem, index: Int) {
            binding.titleText.text = context.getString(R.string.links)
            binding.seeMoreText.show(false)
            binding.footnoteText.text = context.getString(R.string.long_press_to_copy_link)
            binding.footnoteText.show(true)
            linksAdapter?.updateData(item.media.externalLinks)
        }
    }
}