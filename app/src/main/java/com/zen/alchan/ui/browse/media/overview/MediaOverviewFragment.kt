package com.zen.alchan.ui.browse.media.overview


import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer

import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.MediaPage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.*
import com.zen.alchan.helper.setRegularPlural
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.helper.utils.Utility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.character.CharacterFragment
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.browse.studio.StudioFragment
import kotlinx.android.synthetic.main.fragment_media_overview.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType
import type.StaffLanguage

/**
 * A simple [Fragment] subclass.
 */
class MediaOverviewFragment : BaseFragment() {

    private val viewModel by viewModel<MediaOverviewViewModel>()

    private var mediaData: MediaOverviewQuery.Media? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.mediaId = arguments?.getInt(MediaFragment.MEDIA_ID)

        setupObserver()
    }

    private fun setupObserver() {
        viewModel.mediaOverviewData.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    viewModel.mediaData = it.data?.media
                    mediaData = it.data?.media
                    initLayout()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        if (viewModel.mediaData == null) {
            viewModel.getMediaOverview()
        } else {
            mediaData = viewModel.mediaData
            initLayout()
        }
    }

    private fun initLayout() {
        handleGenre()
        handleDescription()
        handleCharacters()
        handleTitles()
        handleInfo()
        handleStudios()
        handleStats()
        handleTags()
        handleRelations()
        handleRecommendations()
        handleLinks()
    }

    private fun handleGenre() {
        if (!mediaData?.genres.isNullOrEmpty()) {
            mediaGenreRecyclerView.visibility = View.VISIBLE
            mediaGenreRecyclerView.adapter = OverviewGenreRvAdapter(mediaData?.genres!!, object : OverviewGenreRvAdapter.OverviewGenreListener {
                override fun passSelectedGenre(genre: String) {
                    // TODO: open genre search
                }
            })
        } else {
            mediaGenreRecyclerView.visibility = View.GONE
        }
    }

    private fun handleDescription() {
        val spanned = HtmlCompat.fromHtml(mediaData?.description ?: getString(R.string.no_description), HtmlCompat.FROM_HTML_MODE_LEGACY)
        mediaDescriptionText.text = spanned
        mediaDescriptionArrow.setOnClickListener {
            if (dummyMediaDescriptionText.isVisible) {
                dummyMediaDescriptionText.visibility = View.GONE
                GlideApp.with(this).load(R.drawable.ic_chevron_up).into(mediaDescriptionArrow)
            } else {
                dummyMediaDescriptionText.visibility = View.VISIBLE
                GlideApp.with(this).load(R.drawable.ic_chevron_down).into(mediaDescriptionArrow)
            }
        }
    }

    private fun handleCharacters() {
        if (viewModel.charactersList.isNullOrEmpty()) {
            mediaData?.characters?.edges?.forEach {
                viewModel.charactersList.add(
                    MediaCharacters(
                        it?.node?.id,
                        it?.node?.name?.full,
                        it?.node?.image?.large,
                        it?.role,
                        null
                    )
                )
            }
        }

        if (!viewModel.charactersList.isNullOrEmpty()) {
            mediaCharactersRecyclerView.adapter = assignCharactersAdapter()
            mediaCharactersLayout.visibility = View.VISIBLE
        } else {
            mediaCharactersLayout.visibility = View.GONE
        }
    }

    private fun handleTitles() {
        mediaRomajiText.text = mediaData?.title?.romaji
        mediaRomajiText.setOnClickListener {
            AndroidUtility.copyToClipboard(activity, mediaRomajiText.text.toString())
            DialogUtility.showToast(activity, R.string.text_copied)
        }

        mediaEnglishText.text = mediaData?.title?.english ?: mediaData?.title?.romaji
        mediaEnglishText.setOnClickListener {
            AndroidUtility.copyToClipboard(activity, mediaEnglishText.text.toString())
            DialogUtility.showToast(activity, R.string.text_copied)
        }

        mediaNativeText.text = mediaData?.title?.native_
        mediaNativeText.setOnClickListener {
            AndroidUtility.copyToClipboard(activity, mediaNativeText.text.toString())
            DialogUtility.showToast(activity, R.string.text_copied)
        }

        val synonymsList = mediaData?.synonyms
        if (!synonymsList.isNullOrEmpty()) {
            mediaSynonymsLayout.visibility = View.VISIBLE
            var synonymText = ""
            synonymsList.forEachIndexed { index, s ->
                synonymText += s
                if (index != synonymsList.lastIndex) synonymText += "\n"
            }
            mediaSynonymsText.text = synonymText
        } else {
            mediaSynonymsLayout.visibility = View.GONE
        }
    }

    private fun handleInfo() {
        mediaFormatText.text = mediaData?.format?.name?.replace("_", " ")
        mediaSourceText.text = mediaData?.source?.name?.replace("_", " ") ?: "-"
        mediaStatusText.text = mediaData?.status?.name?.replace("_", " ")

        mediaStartDateText.text = if (mediaData?.startDate != null) {
            Utility.convertToDateFormat(mediaData?.startDate?.year, mediaData?.startDate?.month, mediaData?.startDate?.day) ?: "?"
        } else {
            "?"
        }

        mediaEndDateText.text = if (mediaData?.endDate != null) {
            Utility.convertToDateFormat(mediaData?.endDate?.year, mediaData?.endDate?.month, mediaData?.endDate?.day) ?: "?"
        } else {
            "?"
        }

        if (mediaData?.type == MediaType.ANIME) {
            mediaProgressLabel.text = getString(R.string.episodes)
            mediaProgressText.text = if (mediaData?.episodes == null || mediaData?.episodes == 0) "?" else mediaData?.episodes?.toString()
            if (mediaData?.duration != null && mediaData?.duration != 0) {
                mediaDurationLayout.visibility = View.VISIBLE
                mediaDurationText.text = "${mediaData?.duration} ${getString(R.string.min).setRegularPlural(mediaData?.duration)}"
            } else {
                mediaDurationLayout.visibility = View.GONE
            }
            mediaVolumesLayout.visibility = View.GONE
            if (mediaData?.season != null) {
                mediaSeasonLayout.visibility = View.VISIBLE
                mediaSeasonText.text = "${mediaData?.season?.name} ${mediaData?.seasonYear}"
                mediaSeasonText.setOnClickListener {
                    // TODO: open seasonal chart
                }
            } else {
                mediaSeasonLayout.visibility = View.GONE
            }
        } else if (mediaData?.type == MediaType.MANGA) {
            mediaProgressLabel.text = getString(R.string.chapters)
            mediaProgressText.text = if (mediaData?.chapters == null || mediaData?.chapters == 0) "?" else mediaData?.chapters?.toString()
            mediaDurationLayout.visibility = View.GONE
            if (mediaData?.volumes != null && mediaData?.volumes != 0) {
                mediaVolumesLayout.visibility = View.VISIBLE
                mediaVolumeText.text = mediaData?.volumes?.toString()
            } else {
                mediaVolumesLayout.visibility = View.GONE
            }
            mediaSeasonLayout.visibility = View.GONE
        }
    }

    private fun handleStudios() {
        if (viewModel.mediaData?.type == MediaType.ANIME) {
            mediaStudioLayout.visibility = View.VISIBLE

            if (viewModel.studioList.isNullOrEmpty()) {
                mediaData?.studios?.edges?.forEach {
                    if (it?.isMain == true) {
                        viewModel.studioList.add(KeyValueItem(it.node?.name!!, it.node.id))
                    } else {
                        viewModel.producerList.add(KeyValueItem(it?.node?.name!!, it.node.id))
                    }
                }

                if (viewModel.studioList.isNullOrEmpty()) {
                    viewModel.studioList.add(KeyValueItem("?", null))
                }

                if (viewModel.producerList.isNullOrEmpty()) {
                    viewModel.producerList.add(KeyValueItem("?", null))
                }
            }

            mediaStudiosRecyclerView.adapter = assignStudioAdapter(viewModel.studioList)
            mediaProducersRecyclerView.adapter = assignStudioAdapter(viewModel.producerList)
        } else {
            mediaStudioLayout.visibility = View.GONE
        }
    }

    private fun handleStats() {
        mediaAvgScoreText.text = "${mediaData?.averageScore?.toString() ?: "0"}%"
        mediaMeanScoreText.text = "${mediaData?.meanScore?.toString() ?: "0"}%"
        mediaPopularityText.text = mediaData?.popularity?.toString() ?: "0"
        mediaFavoritesText.text = mediaData?.favourites?.toString() ?: "0"
    }

    private fun handleTags() {
        mediaTagsShowSpoilerText.text =  if (viewModel.showSpoiler) getString(R.string.hide_spoiler) else getString(R.string.show_spoiler)

        mediaTagsShowSpoilerText.setOnClickListener {
            viewModel.showSpoiler = !viewModel.showSpoiler
            mediaTagsShowSpoilerText.text =  if (viewModel.showSpoiler) getString(R.string.hide_spoiler) else getString(R.string.show_spoiler)
            mediaTagsRecyclerView.adapter = assignTagsAdapter()
        }

        if (viewModel.tagsList.isNullOrEmpty()) {
            mediaData?.tags?.forEach {
                viewModel.tagsList.add(
                    MediaTags(
                        it?.id!!,
                        it.name,
                        it.rank,
                        it.isGeneralSpoiler,
                        it.isMediaSpoiler,
                        it.isAdult
                    )
                )
            }
        }

        if (!viewModel.tagsList.isNullOrEmpty()) {
            mediaTagsRecyclerView.adapter = assignTagsAdapter()
            mediaTagsLayout.visibility = View.VISIBLE
        } else {
            mediaTagsLayout.visibility = View.GONE
        }

        if (viewModel.tagsList.find { it.isGeneralSpoiler == true || it.isMediaSpoiler == true } != null) {
            mediaTagsShowSpoilerText.visibility = View.VISIBLE
        } else {
            mediaTagsShowSpoilerText.visibility = View.GONE
        }
    }

    private fun handleRelations() {
        if (viewModel.relationsList.isNullOrEmpty()) {
            mediaData?.relations?.edges?.forEach {
                viewModel.relationsList.add(
                    MediaRelations(
                        it?.node?.id!!,
                        it.node.title?.userPreferred!!,
                        it.node.coverImage?.extraLarge,
                        it.node.type!!,
                        it.node.format!!,
                        it.relationType!!
                    )
                )
            }

            viewModel.relationsList.sortBy { Constant.MEDIA_RELATION_PRIORITY[it.relationType] }
        }

        if (!viewModel.relationsList.isNullOrEmpty()) {
            mediaRelationsRecyclerView.adapter = assignRelationsAdapter()
            mediaRelationsLayout.visibility = View.VISIBLE
        } else {
            mediaRelationsLayout.visibility = View.GONE
        }
    }

    private fun handleRecommendations() {
        if (viewModel.recommendationsList.isNullOrEmpty()) {
            mediaData?.recommendations?.edges?.forEach {
                viewModel.recommendationsList.add(
                    MediaRecommendations(
                        it?.node?.mediaRecommendation?.id!!,
                        it.node.rating,
                        it.node.mediaRecommendation.title?.userPreferred,
                        it.node.mediaRecommendation.format,
                        it.node.mediaRecommendation.averageScore,
                        it.node.mediaRecommendation.favourites,
                        it.node.mediaRecommendation.coverImage?.extraLarge
                    )
                )
            }
        }

        if (!viewModel.recommendationsList.isNullOrEmpty()) {
            mediaRecommendationsRecyclerView.adapter = assignRecommendationsAdapter()
            mediaRecommendationsLayout.visibility = View.VISIBLE
        } else {
            mediaRecommendationsLayout.visibility = View.GONE
        }
    }

    private fun handleLinks() {
        if (viewModel.linksList.isNullOrEmpty()) {
            mediaData?.externalLinks?.forEach {
                viewModel.linksList.add(MediaLinks(it?.site!!, it.url))
            }
            viewModel.linksList.add(0, MediaLinks("AniList", mediaData?.siteUrl ?: "${Constant.ANILIST_URL}${mediaData?.type?.name?.toLowerCase()}/${mediaData?.id}"))
        }

        mediaLinksRecyclerView.adapter = assignLinksAdapter()
    }

    private fun assignCharactersAdapter(): OverviewCharactersRvAdapter {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val width = metrics.widthPixels / 5
        return OverviewCharactersRvAdapter(activity!!, viewModel.charactersList, width, object : OverviewCharactersRvAdapter.OverviewCharactersListener {
            override fun passSelectedCharacter(characterId: Int) {
                val fragment = CharacterFragment()
                val bundle = Bundle()
                bundle.putInt(CharacterFragment.CHARACTER_ID, characterId)
                fragment.arguments = bundle
                listener?.changeFragment(fragment)
            }
        })
    }

    private fun assignStudioAdapter(list: List<KeyValueItem>): OverviewStudiosRvAdapter {
        return OverviewStudiosRvAdapter(activity, list, object : OverviewStudiosRvAdapter.OverviewStudioListener {
            override fun passSelectedStudio(studioId: Int?) {
                if (studioId == null) return
                val fragment = StudioFragment()
                val bundle = Bundle()
                bundle.putInt(StudioFragment.STUDIO_ID, studioId)
                fragment.arguments = bundle
                listener?.changeFragment(fragment)
            }
        })
    }

    private fun assignTagsAdapter(): OverviewTagsRvAdapter {
        return OverviewTagsRvAdapter(activity!!, if (!viewModel.showSpoiler) viewModel.tagsList.filter { it.isMediaSpoiler == false && it.isGeneralSpoiler == false } else viewModel.tagsList, object : OverviewTagsRvAdapter.OverviewTagsListener {
            override fun passSelectedTag(tagId: Int) {
                // TODO: search tag
            }
        })
    }

    private fun assignRelationsAdapter(): OverviewRelationsRvAdapter {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val width = metrics.widthPixels / 3
        return OverviewRelationsRvAdapter(activity!!, viewModel.relationsList, width, object : OverviewRelationsRvAdapter.OverviewRelationsListener {
            override fun passSelectedRelations(mediaId: Int, mediaType: MediaType) {
                val fragment = MediaFragment()
                val bundle = Bundle()
                bundle.putInt(MediaFragment.MEDIA_ID, mediaId)
                bundle.putString(MediaFragment.MEDIA_TYPE, mediaType.name)
                fragment.arguments = bundle
                listener?.changeFragment(fragment)
            }
        })
    }

    private fun assignRecommendationsAdapter(): OverviewRecommendationsRvAdapter {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val width = (metrics.widthPixels / 1.3).toInt()
        return OverviewRecommendationsRvAdapter(activity!!, viewModel.recommendationsList, width, object : OverviewRecommendationsRvAdapter.OverviewRecommendationsListener {
            override fun passSelectedRecommendations(mediaId: Int) {
                val fragment = MediaFragment()
                val bundle = Bundle()
                bundle.putInt(MediaFragment.MEDIA_ID, mediaId)
                bundle.putString(MediaFragment.MEDIA_TYPE, MediaType.ANIME.name)
                fragment.arguments = bundle
                listener?.changeFragment(fragment)
            }
        })
    }

    private fun assignLinksAdapter(): OverviewLinksRvAdapter {
        return OverviewLinksRvAdapter(activity!!, viewModel.linksList, object : OverviewLinksRvAdapter.OverviewLinksListener {
            override fun openUrl(url: String) {
                CustomTabsIntent.Builder()
                    .build()
                    .launchUrl(activity!!, Uri.parse(url))
            }

            override fun copyUrl(url: String) {
                AndroidUtility.copyToClipboard(activity, url)
                DialogUtility.showToast(activity, R.string.link_copied)
            }
        })
    }
}
