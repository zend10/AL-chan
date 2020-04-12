package com.zen.alchan.ui.media


import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.zen.alchan.ui.mangalist.editor.MangaListEditorActivity
import com.zen.alchan.ui.media.overview.MediaOverviewFragment
import com.zen.alchan.ui.media.social.MediaSocialFragment
import com.zen.alchan.ui.media.stats.MediaStatsFragment
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

    private lateinit var scaleUpAnim: Animation
    private lateinit var scaleDownAnim: Animation

    private val floatingMenuIconMap = hashMapOf(
        Pair(MediaPage.OVERVIEW, R.drawable.ic_contacts),
        Pair(MediaPage.STATS, R.drawable.ic_bar_chart),
        Pair(MediaPage.REVIEWS, R.drawable.ic_inscription),
        Pair(MediaPage.SOCIAL, R.drawable.ic_chat)
    )

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

        scaleUpAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_up)
        scaleDownAnim = AnimationUtils.loadAnimation(activity, R.anim.scale_down)

        mediaToolbar.setNavigationOnClickListener { activity?.finish() }
        mediaToolbar.navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_delete)

        initLayout()
        setupObserver()
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkMediaStatus()
    }

    private fun setupObserver() {
        viewModel.mediaData.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> loadingLayout.visibility = View.VISIBLE
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    if (it.data?.Media() != null) {
                        viewModel.notifyMediaData(it.data.Media())
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

        viewModel.getMedia()
    }

    private fun initLayout() {
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

        mediaScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY > oldScrollY) {
                mediaFloatingMenu.hide()
            } else {
                mediaFloatingMenu.show()
            }

            handleFloatingMenuVisibility(false)
        }

        mediaFloatingMenu.text = viewModel.currentMediaPage.name.toLowerCase().capitalize()

        mediaFloatingMenu.setOnClickListener {
            handleFloatingMenuVisibility(!mediaOverviewMenu.isVisible)
        }

        mediaOverviewMenu.setOnClickListener { changeMediaPage(MediaPage.OVERVIEW) }
        mediaStatsMenu.setOnClickListener { changeMediaPage(MediaPage.STATS) }
        mediaReviewsMenu.setOnClickListener { changeMediaPage(MediaPage.REVIEWS) }
        mediaSocialMenu.setOnClickListener { changeMediaPage(MediaPage.SOCIAL) }

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

        changeMediaPage(viewModel.currentMediaPage)
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

        changeMediaPage(viewModel.currentMediaPage)
    }

    private fun changeMediaPage(targetMediaPage: MediaPage) {
        viewModel.currentMediaPage = targetMediaPage

        mediaFloatingMenu.text = viewModel.currentMediaPage.name.toLowerCase().capitalize()
        mediaFloatingMenu.icon = ContextCompat.getDrawable(activity!!, floatingMenuIconMap[viewModel.currentMediaPage] ?: R.drawable.ic_contacts)

        handleFloatingMenuVisibility(false)

        val fragment = when (viewModel.currentMediaPage) {
            MediaPage.OVERVIEW -> MediaOverviewFragment()
            MediaPage.STATS -> MediaStatsFragment()
            MediaPage.SOCIAL -> MediaSocialFragment()
            else -> MediaOverviewFragment()
        }

        val bundle = Bundle()
        bundle.putInt(MEDIA_ID, viewModel.mediaId!!)

        fragment.arguments = bundle

        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(mediaFrameLayout.id, fragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.commit()
    }

    private fun handleFloatingMenuVisibility(shouldVisible: Boolean) {
        if (!shouldVisible) {
            mediaOverviewMenu.hide()
            mediaStatsMenu.hide()
            mediaReviewsMenu.hide()
            mediaSocialMenu.hide()
            mediaFloatingMenu.backgroundTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeSecondaryColor))
        } else {
            mediaOverviewMenu.show()
            mediaStatsMenu.show()
            mediaReviewsMenu.show()
            mediaSocialMenu.show()
            mediaFloatingMenu.backgroundTintList = ColorStateList.valueOf(AndroidUtility.getResValueFromRefAttr(activity, R.attr.themeNegativeColor))
        }
    }
}
