package com.zen.alchan.ui.browse.media.overview


import android.app.Activity
import android.content.Intent
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
import androidx.lifecycle.Observer

import com.zen.alchan.R
import com.zen.alchan.data.response.AnimePromo
import com.zen.alchan.data.response.AnimeVideo
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.enums.BrowsePage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.*
import com.zen.alchan.helper.setRegularPlural
import com.zen.alchan.helper.updateBottomPadding
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.helper.utils.Utility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.BrowseActivity
import com.zen.alchan.ui.browse.media.MediaFragment
import com.zen.alchan.ui.explore.ExploreActivity
import kotlinx.android.synthetic.main.fragment_media_overview.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType
import java.net.URLEncoder

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
                    viewModel.getAnimeThemes()
                    viewModel.getMangaPublisher()
                    viewModel.getAnimeVideos()
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.animeDetailsLiveData.observe(viewLifecycleOwner, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                viewModel.animeDetails = it.data
                handleThemes()
            }
        })

        viewModel.mangaDetailsLiveData.observe(viewLifecycleOwner, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                viewModel.mangaDetails = it.data
                handleStudios()
            }
        })

        viewModel.animeVideoLiveData.observe(viewLifecycleOwner, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                handleTrailers(it.data?.promo)
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
        handleThemes()
        handleRelations()
        handleRecommendations()
        handleTrailers(null)
        handleLinks()
    }

    private fun handleGenre() {
        if (!mediaData?.genres.isNullOrEmpty()) {
            mediaGenreRecyclerView.visibility = View.VISIBLE
            mediaGenreRecyclerView.adapter = OverviewGenreRvAdapter(mediaData?.genres!!, object : OverviewGenreRvAdapter.OverviewGenreListener {
                override fun passSelectedGenre(genre: String) {
                    val intent = Intent(activity, ExploreActivity::class.java)
                    intent.putExtra(ExploreActivity.EXPLORE_PAGE, viewModel.mediaData?.type?.name)
                    intent.putExtra(ExploreActivity.SELECTED_GENRE, genre)
                    startActivityForResult(intent, ExploreActivity.ACTIVITY_EXPLORE)
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
                val seasonAndYear = "${mediaData?.season?.name} ${mediaData?.seasonYear}"
                mediaSeasonLayout.visibility = View.VISIBLE
                mediaSeasonText.text = seasonAndYear
                mediaSeasonText.setOnClickListener {
                    val intent = Intent(activity, ExploreActivity::class.java)
                    intent.putExtra(ExploreActivity.EXPLORE_PAGE, viewModel.mediaData?.type?.name)
                    intent.putExtra(ExploreActivity.SELECTED_SEASON, seasonAndYear)
                    startActivityForResult(intent, ExploreActivity.ACTIVITY_EXPLORE)
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
            mediaProducerLayout.visibility = View.VISIBLE
            mediaStudioLabel.text = getString(R.string.studios)

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
            mediaProducerLayout.visibility = View.GONE
            mediaStudioLabel.text = getString(R.string.serialization)

            if (viewModel.mangaDetails != null && viewModel.studioList.isNullOrEmpty()) {
                viewModel.mangaDetails?.serializations?.forEach {
                    viewModel.studioList.add(KeyValueItem(it.name, null))
                }
            }

            if (viewModel.studioList.isNullOrEmpty()) {
                mediaStudioLayout.visibility = View.GONE
            } else {
                mediaStudioLayout.visibility = View.VISIBLE
                mediaStudiosRecyclerView.adapter = assignStudioAdapter(viewModel.studioList)
            }
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
                        it.description,
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

    private fun handleThemes() {
        mediaOpeningThemesLayout.visibility = View.GONE
        mediaEndingThemesLayout.visibility = View.GONE

        if (viewModel.animeDetails?.openingThemes?.isNullOrEmpty() == false) {
            mediaOpeningThemesLayout.visibility = View.VISIBLE
            mediaOpeningThemesRecyclerView.adapter = OverviewThemesRvAdapter(viewModel.animeDetails?.openingThemes ?: listOf(), object : OverviewThemesRvAdapter.OverviewThemeListener {
                override fun passSelectedTheme(title: String) {
                    openThemesPlayerDialog(title)
                }
            })
        }

        if (viewModel.animeDetails?.endingThemes?.isNullOrEmpty() == false) {
            mediaEndingThemesLayout.visibility = View.VISIBLE
            mediaEndingThemesRecyclerView.adapter = OverviewThemesRvAdapter(viewModel.animeDetails?.endingThemes ?: listOf(), object : OverviewThemesRvAdapter.OverviewThemeListener {
                override fun passSelectedTheme(title: String) {
                    openThemesPlayerDialog(title)
                }
            })
        }
    }

    private fun openThemesPlayerDialog(title: String) {
        val dialog = ThemesPlayerDialog()
        val bundle = Bundle()
        bundle.putString(ThemesPlayerDialog.MEDIA_TITLE, viewModel.mediaData?.title?.romaji)
        bundle.putString(ThemesPlayerDialog.TRACK_TITLE, title)
        dialog.arguments = bundle
        dialog.show(childFragmentManager, null)
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
                        it.node.format,
                        it.relationType
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
                if (it?.node?.mediaRecommendation?.id != null) {
                    viewModel.recommendationsList.add(
                        MediaRecommendations(
                            it.node.mediaRecommendation.id,
                            it.node.rating,
                            it.node.mediaRecommendation.title?.userPreferred,
                            it.node.mediaRecommendation.format,
                            it.node.mediaRecommendation.type,
                            it.node.mediaRecommendation.averageScore,
                            it.node.mediaRecommendation.favourites,
                            it.node.mediaRecommendation.coverImage?.extraLarge
                        )
                    )
                }
            }
        }

        if (!viewModel.recommendationsList.isNullOrEmpty()) {
            mediaRecommendationsRecyclerView.adapter = assignRecommendationsAdapter()
            mediaRecommendationsLayout.visibility = View.VISIBLE
        } else {
            mediaRecommendationsLayout.visibility = View.GONE
        }
    }

    private fun handleTrailers(malVideos: List<AnimePromo>?) {
        if (viewModel.trailersList.isNullOrEmpty() && mediaData?.trailer != null) {
            viewModel.trailersList.add(
                MediaTrailer(
                    getString(R.string.trailer),
                    if (mediaData?.trailer?.site == "dailymotion") "https://www.dailymotion.com/video/${mediaData?.trailer?.id}" else "https://www.youtube.com/watch?v=${mediaData?.trailer?.id}",
                    mediaData?.trailer?.thumbnail ?: ""
                )
            )
        }

        // <= 1 because wouldn't want duplicate entry
        if (viewModel.trailersList.size <= 1) {
            malVideos?.forEach {
                viewModel.trailersList.add(
                    MediaTrailer(
                        it.title,
                        it.videoUrl,
                        it.imageUrl
                    )
                )
            }
        }

        if (!viewModel.trailersList.isNullOrEmpty()) {
            mediaTrailersRecyclerView.adapter = assignTrailersAdapter()
            mediaTrailersLayout.visibility = View.VISIBLE
        } else {
            mediaTrailersLayout.visibility = View.GONE
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
        val width = metrics.widthPixels / resources.getInteger(R.integer.horizontalListCharacterDivider)
        return OverviewCharactersRvAdapter(activity!!, viewModel.charactersList, width, object : OverviewCharactersRvAdapter.OverviewCharactersListener {
            override fun passSelectedCharacter(characterId: Int) {
                listener?.changeFragment(BrowsePage.CHARACTER, characterId)
            }
        })
    }

    private fun assignStudioAdapter(list: List<KeyValueItem>): OverviewStudiosRvAdapter {
        return OverviewStudiosRvAdapter(activity, list, object : OverviewStudiosRvAdapter.OverviewStudioListener {
            override fun passSelectedStudio(studioId: Int?) {
                if (studioId == null) return
                listener?.changeFragment(BrowsePage.STUDIO, studioId)
            }
        })
    }

    private fun assignTagsAdapter(): OverviewTagsRvAdapter {
        return OverviewTagsRvAdapter(activity!!, if (!viewModel.showSpoiler) viewModel.tagsList.filter { it.isMediaSpoiler == false && it.isGeneralSpoiler == false } else viewModel.tagsList, object : OverviewTagsRvAdapter.OverviewTagsListener {
            override fun passSelectedTag(tagName: String) {
                val intent = Intent(activity, ExploreActivity::class.java)
                intent.putExtra(ExploreActivity.EXPLORE_PAGE, viewModel.mediaData?.type?.name)
                intent.putExtra(ExploreActivity.SELECTED_TAG, tagName)
                startActivityForResult(intent, ExploreActivity.ACTIVITY_EXPLORE)
            }
        })
    }

    private fun assignRelationsAdapter(): OverviewRelationsRvAdapter {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val width = metrics.widthPixels / resources.getInteger(R.integer.horizontalListRelationDivider)
        return OverviewRelationsRvAdapter(activity!!, viewModel.relationsList, width, object : OverviewRelationsRvAdapter.OverviewRelationsListener {
            override fun passSelectedRelations(mediaId: Int, mediaType: MediaType) {
                listener?.changeFragment(BrowsePage.valueOf(mediaType.name), mediaId)
            }
        })
    }

    private fun assignRecommendationsAdapter(): OverviewRecommendationsRvAdapter {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val width = (metrics.widthPixels / 1.3).toInt()
        return OverviewRecommendationsRvAdapter(activity!!, viewModel.recommendationsList, width, object : OverviewRecommendationsRvAdapter.OverviewRecommendationsListener {
            override fun passSelectedRecommendations(mediaId: Int, mediaType: MediaType) {
                listener?.changeFragment(BrowsePage.valueOf(mediaType.name), mediaId)
            }
        })
    }

    private fun assignTrailersAdapter(): OverviewTrailersRvAdapter {
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
        val width = (metrics.widthPixels / 1.3).toInt()
        return OverviewTrailersRvAdapter(activity!!, viewModel.trailersList, width, object : OverviewTrailersRvAdapter.OverviewTrailersListener {
            override fun playTrailer(site: String) {
                DialogUtility.showOptionDialog(
                    requireActivity(),
                    R.string.watch_trailer,
                    R.string.watch_this_trailer_in_the_browser_or_the_appropriate_app,
                    R.string.watch,
                    {
                        CustomTabsIntent.Builder()
                            .build()
                            .launchUrl(activity!!, Uri.parse(site))
                    },
                    R.string.cancel,
                    { }
                )
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
                DialogUtility.showToast(activity, "${getString(R.string.link_copied)}: $url")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ExploreActivity.ACTIVITY_EXPLORE && resultCode == Activity.RESULT_OK) {
            if (data?.extras != null && data.extras?.getInt(BrowseActivity.LOAD_ID) != null) {
                val id = data.extras?.getInt(BrowseActivity.LOAD_ID)!!
                val targetPage = BrowsePage.valueOf(data.extras?.getString(BrowseActivity.TARGET_PAGE) ?: BrowsePage.ANIME.name)
                listener?.changeFragment(browsePage = targetPage, id = id)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaGenreRecyclerView.adapter = null
        mediaCharactersRecyclerView.adapter = null
        mediaStudiosRecyclerView.adapter = null
        mediaProducersRecyclerView.adapter = null
        mediaTagsRecyclerView.adapter = null
        mediaRelationsRecyclerView.adapter = null
        mediaRecommendationsRecyclerView.adapter = null
        mediaLinksRecyclerView.adapter = null
    }
}
