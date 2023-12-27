package com.zen.alchan.ui.media

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.appbar.AppBarLayout
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.entity.MediaFilter
import com.zen.shared.data.response.AnimeTheme
import com.zen.shared.data.response.AnimeThemeEntry
import com.zen.alchan.data.response.Genre
import com.zen.alchan.data.response.anilist.*
import com.zen.alchan.databinding.FragmentMediaBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.SearchCategory
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.SpaceItemDecoration
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.type.MediaSeason
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class MediaFragment : BaseFragment<FragmentMediaBinding, MediaViewModel>() {

    override val viewModel: MediaViewModel by viewModel()

    private var scaleUpAnimation: Animation? = null
    private var scaleDownAnimation: Animation? = null
    private var isToolbarExpanded = true

    private var mediaAdapter: MediaRvAdapter? = null
    private var menuItemMediaStats: MenuItem? = null
    private var menuItemSocial: MenuItem? = null
    private var menuItemReview: MenuItem? = null
    private var currentMedia: Media? = null
    private var appSetting = AppSetting()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaBinding {
        return FragmentMediaBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            scaleUpAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
            scaleDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
            mediaAppBarLayout.setExpanded(isToolbarExpanded)

            setUpToolbar(mediaToolbar, "", R.drawable.ic_custom_close) {
                navigation.closeBrowseScreen()
            }
            mediaToolbar.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_custom_more)

            menuItemMediaStats = mediaToolbar.menu.findItem(R.id.itemMediaStats)
            menuItemMediaStats?.setOnMenuItemClickListener {
                currentMedia?.let { navigation.navigateToMediaStats(it) }
                true
            }
            menuItemSocial = mediaToolbar.menu.findItem(R.id.itemMediaSocial)
            menuItemSocial?.setOnMenuItemClickListener {
                currentMedia?.let { navigation.navigateToMediaSocial(it) }
                true
            }
            menuItemReview = mediaToolbar.menu.findItem(R.id.itemMediaReview)
            menuItemReview?.setOnMenuItemClickListener {
                currentMedia?.let { navigation.navigateToMediaReview(it) }
                true
            }

            mediaRecyclerView.addItemDecoration(SpaceItemDecoration(top = resources.getDimensionPixelSize(R.dimen.marginFar)))
            assignAdapter(appSetting)

            mediaAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                isToolbarExpanded = verticalOffset == 0
                mediaSwipeRefresh.isEnabled = isToolbarExpanded

                if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                    if (mediaBannerContentLayout.isVisible) {
                        mediaBannerContentLayout.startAnimation(scaleDownAnimation)
                        mediaBannerContentLayout.visibility = View.INVISIBLE
                    }
                } else {
                    if (mediaBannerContentLayout.isInvisible) {
                        mediaBannerContentLayout.startAnimation(scaleUpAnimation)
                        mediaBannerContentLayout.visibility = View.VISIBLE
                    }
                }
            })

            mediaCoverImage.clicks {
                viewModel.loadCoverImage()
            }

            mediaBannerImage.clicks {
                viewModel.loadBannerImage()
            }

            mediaAddToListButton.clicks {
                arguments?.getInt(MEDIA_ID)?.let { mediaId ->
                    navigation.navigateToEditor(mediaId, false) {
                        // do nothing
                    }
                }
            }

            mediaSwipeRefresh.setOnRefreshListener {
                viewModel.reloadData()
            }
        }
    }

    override fun setUpInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mediaCollapsingToolbar, null)
        binding.mediaRecyclerView.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.isAuthenticated.subscribe {
                binding.mediaAddToListButton.isEnabled = it

                if (!it) {
                    binding.mediaAddToListButton.apply {
                        text = getString(R.string.please_login)
                        strokeWidth = 0
                        strokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
                        backgroundTintList = ColorStateList.valueOf(context.getAttrValue(R.attr.themeContentTransparentColor))
                        setTextColor(context.getAttrValue(R.attr.themeContentColor))
                    }
                }
            },
            viewModel.loading.subscribe {
                binding.mediaSwipeRefresh.isRefreshing = it
            },
            viewModel.success.subscribe {
                dialog.showToast(it)
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.mediaAdapterComponent.subscribe {
                appSetting = it
                assignAdapter(it)
            },
            viewModel.bannerImage.subscribe {
                ImageUtil.loadImage(requireContext(), it, binding.mediaBannerImage)
            },
            viewModel.coverImage.subscribe {
                ImageUtil.loadImage(requireContext(), it, binding.mediaCoverImage)
            },
            viewModel.mediaTitle.subscribe {
                binding.mediaTitleText.text = it
            },
            viewModel.mediaYear.subscribe {
                binding.mediaYearText.text = it
            },
            viewModel.mediaYearVisibility.subscribe {
                binding.mediaYearText.show(it)
            },
            viewModel.mediaFormat.subscribe {
                binding.mediaFormatText.text = it.data?.getString()
            },
            viewModel.mediaLength.subscribe { (length, mediaType) ->
                binding.mediaLengthText.text = when (mediaType) {
                    MediaType.ANIME -> length.showUnit(requireContext(), R.plurals.episode)
                    MediaType.MANGA -> length.showUnit(requireContext(), R.plurals.chapter)
                }
            },
            viewModel.mediaLengthVisibility.subscribe {
                binding.mediaLengthDividerIcon.show(it)
                binding.mediaLengthText.show(it)
            },
            viewModel.airingSchedule.subscribe {
                binding.mediaAiringLayout.show(it.data != null)

                it?.data?.let { airingSchedule ->
                    binding.mediaAiringText.text = getString(R.string.ep_x_on_y, airingSchedule.episode, TimeUtil.displayInDayDateTimeFormat(airingSchedule.airingAt))
                }
            },
            viewModel.averageScore.subscribe {
                binding.mediaScoreText.text = it.getNumberFormatting()
            },
            viewModel.favorites.subscribe {
                binding.mediaFavoritesText.text = it.getNumberFormatting()
            },
            viewModel.addToListButtonText.subscribe {
                if (it.isNotBlank()) {
                    binding.mediaAddToListButton.apply {
                        text = it
                        strokeWidth = context.resources.getDimensionPixelSize(R.dimen.lineWidth)
                        strokeColor = ColorStateList.valueOf(context.getAttrValue(R.attr.themePrimaryColor))
                        backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                        setTextColor(context.getAttrValue(R.attr.themePrimaryColor))
                    }
                } else {
                    binding.mediaAddToListButton.apply {
                        text = getString(R.string.add_to_list)
                        strokeWidth = 0
                        strokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
                        backgroundTintList = ColorStateList.valueOf(context.getAttrValue(R.attr.themePrimaryColor))
                        setTextColor(context.getAttrValue(R.attr.themeBackgroundColor))
                    }
                }
            },
            viewModel.mediaItemList.subscribe {
                currentMedia = it.firstOrNull()?.media
                mediaAdapter?.updateData(it, true)
            },
            viewModel.coverImageUrlForPreview.subscribe {
                ImageUtil.showFullScreenImage(requireContext(), it, binding.mediaCoverImage)
            },
            viewModel.bannerImageUrlForPreview.subscribe {
                ImageUtil.showFullScreenImage(requireContext(), it, binding.mediaBannerImage)
            }
        )

        arguments?.getInt(MEDIA_ID)?.let {
            viewModel.loadData(MediaParam(it))
        }
    }

    private fun assignAdapter(appSetting: AppSetting) {
        mediaAdapter = MediaRvAdapter(requireContext(), listOf(), appSetting, screenWidth, getMediaListener())
        binding.mediaRecyclerView.adapter = mediaAdapter
    }

    private fun getMediaListener(): MediaListener {
        return object : MediaListener {
            override val mediaSynopsisListener: MediaListener.MediaSynopsisListener = getMediaSynopsisListener()
            override val mediaInfoListener: MediaListener.MediaInfoListener = getMediaInfoListener()
            override val mediaGenreListener: MediaListener.MediaGenreListener = getMediaGenreListener()
            override val mediaCharacterListener: MediaListener.MediaCharacterListener = getMediaCharacterListener()
            override val mediaStudioListener: MediaListener.MediaStudioListener = getMediaStudioListener()
            override val mediaTagsListener: MediaListener.MediaTagsListener = getMediaTagsListener()
            override val mediaThemesListener: MediaListener.MediaThemesListener = getMediaThemesListener()
            override val mediaStaffListener: MediaListener.MediaStaffListener = getMediaStaffListener()
            override val mediaRelationsListener: MediaListener.MediaRelationsListener = getMediaRelationsListener()
            override val mediaRecommendationsListener: MediaListener.MediaRecommendationsListener = getMediaRecommendationsListener()
            override val mediaLinksListener: MediaListener.MediaLinksListener = getMediaLinksListener()
        }
    }

    private fun getMediaSynopsisListener(): MediaListener.MediaSynopsisListener {
        return object : MediaListener.MediaSynopsisListener {
            override fun toggleShowMore(shouldShowMore: Boolean) {
                viewModel.updateShouldShowFullDescription(shouldShowMore)
            }
        }
    }

    private fun getMediaInfoListener(): MediaListener.MediaInfoListener {
        return object : MediaListener.MediaInfoListener {
            override fun copyTitle(title: String) {
                viewModel.copyText(title)
            }

            override fun navigateToExplore(type: com.zen.alchan.type.MediaType, season: MediaSeason, seasonYear: Int) {
                navigation.navigateToExplore(
                    SearchCategory.ANIME,
                    MediaFilter(mediaSeasons = listOf(season), minYear = seasonYear, maxYear = seasonYear)
                ) {
                    it()
                }
            }
        }
    }

    private fun getMediaGenreListener(): MediaListener.MediaGenreListener {
        return object : MediaListener.MediaGenreListener {
            override fun navigateToExplore(type: com.zen.alchan.type.MediaType, genre: Genre) {
                navigation.navigateToExplore(
                    if (type == com.zen.alchan.type.MediaType.MANGA) SearchCategory.MANGA else SearchCategory.ANIME,
                    MediaFilter(includedGenres = listOf(genre.name))
                ) {
                    it()
                }
            }
        }
    }

    private fun getMediaCharacterListener(): MediaListener.MediaCharacterListener {
        return object : MediaListener.MediaCharacterListener {
            override fun navigateToMediaCharacters(media: Media) {
                navigation.navigateToMediaCharacters(media.getId())
            }

            override fun navigateToCharacter(character: Character) {
                navigation.navigateToCharacter(character.id)
            }
        }
    }

    private fun getMediaStudioListener(): MediaListener.MediaStudioListener {
        return object : MediaListener.MediaStudioListener {
            override fun navigateToStudio(studio: Studio) {
                navigation.navigateToStudio(studio.id)
            }
        }
    }

    private fun getMediaTagsListener(): MediaListener.MediaTagsListener {
        return object : MediaListener.MediaTagsListener {
            override fun shouldShowSpoilers(show: Boolean) {
                viewModel.updateShouldShowSpoilerTags(show)
            }

            override fun navigateToExplore(type: com.zen.alchan.type.MediaType, tag: MediaTag) {
                navigation.navigateToExplore(
                    if (type == com.zen.alchan.type.MediaType.MANGA) SearchCategory.MANGA else SearchCategory.ANIME,
                    MediaFilter(includedTags = listOf(tag))
                ) {
                    it()
                }
            }

            override fun showDescription(tag: MediaTag) {
                dialog.showToast(tag.description)
            }
        }
    }

    private fun getMediaThemesListener(): MediaListener.MediaThemesListener {
        return object : MediaListener.MediaThemesListener {
            override fun openThemeDialog(
                media: Media,
                animeTheme: AnimeTheme,
                animeThemeEntry: AnimeThemeEntry?
            ) {
                dialog.showAnimeThemesDialog(media, animeTheme, animeThemeEntry) { url, videoId, usePlayer ->
                    if (usePlayer && url != null)
                        navigation.openWebView(url)
                    else if (!usePlayer && videoId != null)
                        navigation.openOnYouTube(videoId)
                    else if (!usePlayer && url != null)
                        navigation.openOnSpotify(url)
                }
            }

            override fun openGroupDialog(viewType: Int, groups: List<String>) {
                dialog.showListDialog(groups.map { ListItem(it, it) }) { data, index ->
                    viewModel.changeThemeGroup(viewType, data)
                }
            }
        }
    }

    private fun getMediaStaffListener(): MediaListener.MediaStaffListener {
        return object : MediaListener.MediaStaffListener {
            override fun navigateToMediaStaff(media: Media) {
                navigation.navigateToMediaStaff(media.getId())
            }

            override fun navigateToStaff(staff: Staff) {
                navigation.navigateToStaff(staff.id)
            }
        }
    }

    private fun getMediaRelationsListener() : MediaListener.MediaRelationsListener {
        return object : MediaListener.MediaRelationsListener {
            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }
        }
    }

    private fun getMediaRecommendationsListener(): MediaListener.MediaRecommendationsListener {
        return object : MediaListener.MediaRecommendationsListener {
            override fun navigateToMedia(media: Media) {
                navigation.navigateToMedia(media.getId())
            }
        }
    }

    private fun getMediaLinksListener(): MediaListener.MediaLinksListener {
        return object : MediaListener.MediaLinksListener {
            override fun navigateToUrl(mediaExternalLink: MediaExternalLink) {
                navigation.openWebView(mediaExternalLink.url)
            }

            override fun copyExternalLink(mediaExternalLink: MediaExternalLink) {
                viewModel.copyExternalLink(mediaExternalLink)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaAdapter = null
        menuItemMediaStats = null
        menuItemSocial = null
        menuItemReview = null
    }

    companion object {
        private const val MEDIA_ID = "mediaId"

        @JvmStatic
        fun newInstance(mediaId: Int) =
            MediaFragment().apply {
                arguments = Bundle().apply {
                    putInt(MEDIA_ID, mediaId)
                }
            }
    }
}