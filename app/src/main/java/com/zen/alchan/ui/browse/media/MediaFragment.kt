package com.zen.alchan.ui.browse.media


import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.stfalcon.imageviewer.StfalconImageViewer

import com.zen.alchan.R
import com.zen.alchan.helper.enums.MediaPage
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.secondsToDateTime
import com.zen.alchan.helper.setRegularPlural
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.ui.animelist.editor.AnimeListEditorActivity
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.browse.media.characters.MediaCharactersFragment
import com.zen.alchan.ui.mangalist.editor.MangaListEditorActivity
import com.zen.alchan.ui.browse.media.overview.MediaOverviewFragment
import com.zen.alchan.ui.browse.media.reviews.MediaReviewsFragment
import com.zen.alchan.ui.browse.media.social.MediaSocialFragment
import com.zen.alchan.ui.browse.media.staffs.MediaStaffsFragment
import com.zen.alchan.ui.browse.media.stats.MediaStatsFragment
import kotlinx.android.synthetic.main.fragment_media.*
import kotlinx.android.synthetic.main.layout_loading.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaListStatus
import type.MediaType
import kotlin.math.abs

/**
 * A simple [Fragment] subclass.
 */
class MediaFragment : BaseFragment() {

    private val viewModel by viewModel<MediaViewModel>()

    private lateinit var mediaSectionMap: HashMap<MediaPage, Pair<ImageView, TextView>>
    private lateinit var mediaFragmentList: List<Fragment>

    private lateinit var scaleUpAnim: Animation
    private lateinit var scaleDownAnim: Animation

    companion object {
        const val MEDIA_ID = "mediaId"
        const val MEDIA_TYPE = "mediaType"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.mediaId = arguments?.getInt(MEDIA_ID)
        viewModel.mediaType = MediaType.valueOf(arguments?.getString(MEDIA_TYPE)!!)

        mediaSectionMap = hashMapOf(
            Pair(MediaPage.OVERVIEW, Pair(mediaOverviewIcon, mediaOverviewText)),
            Pair(MediaPage.CHARACTERS, Pair(mediaCharactersIcon, mediaCharactersText)),
            Pair(MediaPage.STAFFS, Pair(mediaStaffsIcon, mediaStaffsText)),
            Pair(MediaPage.STATS, Pair(mediaStatsIcon, mediaStatsText)),
            Pair(MediaPage.REVIEWS, Pair(mediaReviewsIcon, mediaReviewsText)),
            Pair(MediaPage.SOCIAL, Pair(mediaSocialIcon, mediaSocialText))
        )

        mediaFragmentList = arrayListOf(MediaOverviewFragment(), MediaCharactersFragment(), MediaStaffsFragment(), MediaStatsFragment(), MediaReviewsFragment(), MediaSocialFragment())

        scaleUpAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
        scaleDownAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)

        mediaToolbar.setNavigationOnClickListener { activity?.finish() }
        mediaToolbar.navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_delete)

        initLayout()
        setupObserver()
        setupSection()
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkMediaStatus()
    }

    private fun setupObserver() {
        viewModel.currentSection.observe(viewLifecycleOwner, Observer {
            setupSection()
        })

        viewModel.mediaData.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (it.data?.Media() != null) {
                        viewModel.currentMediaData = it.data.Media()
                        setupHeader()
                    }
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.mediaStatus.observe(viewLifecycleOwner, Observer {
            if (it.data?.MediaList() != null) {
                if (it.data.MediaList()?.status() == MediaListStatus.CURRENT) {
                    mediaManageListButton.text = if (viewModel.mediaType == MediaType.MANGA) {
                        getString(R.string.reading_caps)
                    } else {
                        getString(R.string.watching_caps)
                    }
                } else {
                    mediaManageListButton.text = it.data.MediaList()?.status()?.name
                }
                mediaManageListButton.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                mediaManageListButton.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.transparent))
                mediaManageListButton.strokeColor = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                mediaManageListButton.strokeWidth = 2
            } else {
                mediaManageListButton.text = getString(R.string.add_to_list)
                mediaManageListButton.setTextColor(AndroidUtility.getResValueFromRefAttr(context, R.attr.themeBackgroundColor))
                mediaManageListButton.backgroundTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(context, R.attr.themePrimaryColor))
                mediaManageListButton.strokeWidth = 0
            }

            mediaManageListButton.isEnabled = true
        })

        viewModel.initData()
    }

    private fun initLayout() {
        mediaRefreshLayout.setOnRefreshListener {
            mediaRefreshLayout.isRefreshing = false
            viewModel.getMedia()
            viewModel.checkMediaStatus()
            viewModel.getMediaOverview()
        }

        mediaAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            // disable refresh when toolbar is not fully expanded
            mediaRefreshLayout.isEnabled = verticalOffset == 0

            // 50 is magic number gotten from trial and error
            if (abs(verticalOffset) - appBarLayout.totalScrollRange >= -50) {
                if (mediaBannerContentLayout.isVisible) {
                    mediaBannerContentLayout.startAnimation(scaleDownAnim)
                    mediaBannerContentLayout.visibility = View.INVISIBLE
                }
            } else {
                if (mediaBannerContentLayout.isInvisible) {
                    mediaBannerContentLayout.startAnimation(scaleUpAnim)
                    mediaBannerContentLayout.visibility = View.VISIBLE
                }
            }
        })

        mediaManageListButton.setOnClickListener {
            val entryId = viewModel.mediaStatus.value?.data?.MediaList()?.id()
            if (viewModel.mediaType == MediaType.ANIME) {
                val intent = Intent(activity, AnimeListEditorActivity::class.java)
                if (entryId != null) {
                    intent.putExtra(AnimeListEditorActivity.INTENT_ENTRY_ID, entryId)
                } else {
                    intent.putExtra(AnimeListEditorActivity.INTENT_MEDIA_ID, viewModel.mediaId)
                    intent.putExtra(AnimeListEditorActivity.INTENT_MEDIA_TITLE, viewModel.currentMediaData?.title()?.userPreferred())
                    intent.putExtra(AnimeListEditorActivity.INTENT_MEDIA_EPISODE, viewModel.currentMediaData?.episodes())
                    intent.putExtra(AnimeListEditorActivity.INTENT_IS_FAVOURITE, viewModel.currentMediaData?.isFavourite)
                }
                startActivity(intent)
            } else if (viewModel.mediaType == MediaType.MANGA) {
                val intent = Intent(activity, MangaListEditorActivity::class.java)
                if (entryId != null) {
                    intent.putExtra(MangaListEditorActivity.INTENT_ENTRY_ID, entryId)
                } else {
                    intent.putExtra(MangaListEditorActivity.INTENT_MEDIA_ID, viewModel.mediaId)
                    intent.putExtra(MangaListEditorActivity.INTENT_MEDIA_TITLE, viewModel.currentMediaData?.title()?.userPreferred())
                    intent.putExtra(MangaListEditorActivity.INTENT_MEDIA_CHAPTER, viewModel.currentMediaData?.chapters())
                    intent.putExtra(MangaListEditorActivity.INTENT_MEDIA_VOLUME, viewModel.currentMediaData?.volumes())
                    intent.putExtra(MangaListEditorActivity.INTENT_IS_FAVOURITE, viewModel.currentMediaData?.isFavourite)
                }
                startActivity(intent)
            }
        }

        mediaOverviewLayout.setOnClickListener { viewModel.setMediaSection(MediaPage.OVERVIEW) }
        mediaCharactersLayout.setOnClickListener { viewModel.setMediaSection(MediaPage.CHARACTERS) }
        mediaStaffsLayout.setOnClickListener { viewModel.setMediaSection(MediaPage.STAFFS) }
        mediaStatsLayout.setOnClickListener { viewModel.setMediaSection(MediaPage.STATS) }
        mediaReviewsLayout.setOnClickListener { viewModel.setMediaSection(MediaPage.REVIEWS) }
        mediaSocialLayout.setOnClickListener { viewModel.setMediaSection(MediaPage.SOCIAL) }

        mediaViewPager.setPagingEnabled(false)
        mediaViewPager.offscreenPageLimit = mediaSectionMap.size
        mediaViewPager.adapter = MediaViewPagerAdapter(childFragmentManager, viewModel.mediaId!!, viewModel.mediaType!!, mediaFragmentList)
    }

    private fun setupHeader() {
        GlideApp.with(this).load(viewModel.currentMediaData?.bannerImage()).into(mediaBannerImage)
        GlideApp.with(this).load(viewModel.currentMediaData?.coverImage()?.extraLarge()).into(mediaCoverImage)

        if (viewModel.currentMediaData?.coverImage()?.extraLarge() != null) {
            mediaCoverImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(context, arrayOf(viewModel.currentMediaData?.coverImage()?.extraLarge())) { view, image ->
                    GlideApp.with(context!!).load(image).into(view)
                }.withTransitionFrom(mediaCoverImage).show(true)
            }
        }

        if (viewModel.currentMediaData?.bannerImage() != null) {
            mediaBannerImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(context, arrayOf(viewModel.currentMediaData?.bannerImage())) { view, image ->
                    GlideApp.with(context!!).load(image).into(view)
                }.withTransitionFrom(mediaBannerImage).show(true)
            }
        }

        mediaTitleText.text = viewModel.currentMediaData?.title()?.userPreferred()
        mediaYearText.text = viewModel.currentMediaData?.startDate()?.year().toString()

        if (viewModel.mediaType == MediaType.ANIME) {
            if (viewModel.currentMediaData?.episodes() != null && viewModel.currentMediaData?.episodes() != 0) {
                mediaTotalCountDividerIcon.visibility = View.VISIBLE
                mediaTotalCountText.visibility = View.VISIBLE
                mediaTotalCountText.text =
                    "${viewModel.currentMediaData?.episodes()} ${getString(R.string.episode).setRegularPlural(viewModel.currentMediaData?.episodes())}"
            } else {
                mediaTotalCountDividerIcon.visibility = View.GONE
                mediaTotalCountText.visibility = View.GONE
            }
        } else if (viewModel.mediaType == MediaType.MANGA) {
            if (viewModel.currentMediaData?.chapters() != null && viewModel.currentMediaData?.chapters() != 0) {
                mediaTotalCountDividerIcon.visibility = View.VISIBLE
                mediaTotalCountText.visibility = View.VISIBLE
                mediaTotalCountText.text = "${viewModel.currentMediaData?.chapters()} ${getString(R.string.chapter).setRegularPlural(viewModel.currentMediaData?.chapters())}"
            } else {
                mediaTotalCountDividerIcon.visibility = View.GONE
                mediaTotalCountText.visibility = View.GONE
            }
        }

        mediaFormatText.text = viewModel.currentMediaData?.format()?.name?.replace("_", " ")
        mediaRatingText.text = viewModel.currentMediaData?.averageScore()?.toString() ?: "0"
        mediaFavText.text = viewModel.currentMediaData?.favourites()?.toString() ?: "0"

        if (viewModel.currentMediaData?.nextAiringEpisode() != null) {
            mediaAiringIcon.visibility = View.VISIBLE
            mediaAiringText.visibility = View.VISIBLE
            mediaAiringText.text = "Ep ${viewModel.currentMediaData?.nextAiringEpisode()?.episode()} on ${viewModel.currentMediaData?.nextAiringEpisode()?.airingAt()?.secondsToDateTime()}"

            mediaAiringIcon.setOnClickListener {
                DialogUtility.showToast(activity, mediaAiringText.text.toString())
            }

            mediaAiringText.setOnClickListener {
                DialogUtility.showToast(activity, mediaAiringText.text.toString())
            }
        } else {
            mediaAiringIcon.visibility = View.GONE
            mediaAiringText.visibility = View.GONE
        }

        viewModel.setMediaSection(viewModel.currentSection.value ?: MediaPage.OVERVIEW)
    }

    private fun setupSection() {
        mediaSectionMap.forEach {
            if (it.key == viewModel.currentSection.value) {
                it.value.first.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themePrimaryColor))
                it.value.second.setTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themePrimaryColor))
            } else {
                it.value.first.imageTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
                it.value.second.setTextColor(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeContentColor))
            }
        }

        mediaViewPager.currentItem = when (viewModel.currentSection.value) {
            MediaPage.OVERVIEW -> 0
            MediaPage.CHARACTERS -> 1
            MediaPage.STAFFS -> 2
            MediaPage.STATS -> 3
            MediaPage.REVIEWS -> 4
            MediaPage.SOCIAL -> 5
            else -> 0
        }
    }
}
