package com.zen.alchan.ui.media.overview


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager

import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.libs.GlideApp
import com.zen.alchan.helper.pojo.KeyValueItem
import com.zen.alchan.helper.pojo.MediaRelations
import com.zen.alchan.helper.setRegularPlural
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.helper.utils.Utility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.media.MediaFragment
import kotlinx.android.synthetic.main.fragment_media_overview.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaType

/**
 * A simple [Fragment] subclass.
 */
class MediaOverviewFragment : BaseFragment() {

    private val viewModel by viewModel<MediaOverviewViewModel>()

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
        initLayout()
    }

    private fun initLayout() {
        val mediaData = viewModel.mediaData

        if (!mediaData?.genres().isNullOrEmpty()) {
            mediaGenreRecyclerView.visibility = View.VISIBLE
            mediaGenreRecyclerView.adapter = OverviewGenreRvAdapter(mediaData?.genres()!!, object : OverviewGenreRvAdapter.OverviewGenreListener {
                override fun passSelectedGenre(genre: String) {
                    // TODO: open genre search
                }
            })
        } else {
            mediaGenreRecyclerView.visibility = View.GONE
        }

        // description
        val spanned = HtmlCompat.fromHtml(mediaData?.description() ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
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

        // titles
        mediaRomajiText.text = mediaData?.title()?.romaji()
        mediaRomajiText.setOnClickListener {
            AndroidUtility.copyToClipboard(activity, mediaRomajiText.text.toString())
            DialogUtility.showToast(activity, R.string.text_copied)
        }

        mediaEnglishText.text = mediaData?.title()?.english() ?: mediaData?.title()?.romaji()
        mediaEnglishText.setOnClickListener {
            AndroidUtility.copyToClipboard(activity, mediaEnglishText.text.toString())
            DialogUtility.showToast(activity, R.string.text_copied)
        }

        mediaNativeText.text = mediaData?.title()?.native_()
        mediaNativeText.setOnClickListener {
            AndroidUtility.copyToClipboard(activity, mediaNativeText.text.toString())
            DialogUtility.showToast(activity, R.string.text_copied)
        }

        val synonymsList = mediaData?.synonyms()
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

        // info
        mediaFormatText.text = mediaData?.format()?.name?.replace("_", " ")
        mediaSourceText.text = mediaData?.source()?.name?.replace("_", " ") ?: "-"
        mediaStatusText.text = mediaData?.status()?.name?.replace("_", " ")

        mediaStartDateText.text = if (mediaData?.startDate() != null) {
            Utility.convertToDateFormat(mediaData.startDate()?.year(), mediaData.startDate()?.month(), mediaData.startDate()?.day()) ?: "?"
        } else {
            "?"
        }

        mediaEndDateText.text = if (mediaData?.endDate() != null) {
            Utility.convertToDateFormat(mediaData.endDate()?.year(), mediaData.endDate()?.month(), mediaData.endDate()?.day()) ?: "?"
        } else {
            "?"
        }

        if (mediaData?.type() == MediaType.ANIME) {
            mediaProgressLabel.text = getString(R.string.episodes)
            mediaProgressText.text = if (mediaData.episodes() == null || mediaData.episodes() == 0) "?" else mediaData.episodes()?.toString()
            if (mediaData.duration() != null && mediaData.duration() != 0) {
                mediaDurationLayout.visibility = View.VISIBLE
                mediaDurationText.text = "${mediaData.duration()} ${getString(R.string.min).setRegularPlural(mediaData.duration())}"
            } else {
                mediaDurationLayout.visibility = View.GONE
            }
            mediaVolumesLayout.visibility = View.GONE
            if (mediaData.season() != null) {
                mediaSeasonLayout.visibility = View.VISIBLE
                mediaSeasonText.text = "${mediaData.season()?.name} ${mediaData.seasonYear()}"
                mediaSeasonText.setOnClickListener {
                    // TODO: open seasonal chart
                }
            } else {
                mediaSeasonLayout.visibility = View.GONE
            }
        } else if (mediaData?.type() == MediaType.MANGA) {
            mediaProgressLabel.text = getString(R.string.chapters)
            mediaProgressText.text = if (mediaData.chapters() == null || mediaData.chapters() == 0) "?" else mediaData.chapters()?.toString()
            mediaDurationLayout.visibility = View.GONE
            if (mediaData.volumes() != null && mediaData.volumes() != 0) {
                mediaVolumesLayout.visibility = View.VISIBLE
                mediaVolumeText.text = mediaData.volumes()?.toString()
            } else {
                mediaVolumesLayout.visibility = View.GONE
            }
            mediaSeasonLayout.visibility = View.GONE
        }

        // stats
        mediaAvgScoreText.text = "${mediaData?.averageScore()?.toString() ?: "0"}%"
        mediaMeanScoreText.text = "${mediaData?.meanScore()?.toString() ?: "0"}%"
        mediaPopularityText.text = mediaData?.popularity()?.toString() ?: "0"
        mediaFavoritesText.text = mediaData?.favourites()?.toString() ?: "0"

        // studio
        if (viewModel.mediaData?.type() == MediaType.ANIME) {
            mediaStudioLayout.visibility = View.VISIBLE

            if (viewModel.studioList.isNullOrEmpty()) {
                mediaData?.studios()?.edges()?.forEach {
                    if (it.isMain) {
                        viewModel.studioList.add(KeyValueItem(it.node()?.name()!!, it.node()?.id()))
                    } else {
                        viewModel.producerList.add(KeyValueItem(it.node()?.name()!!, it.node()?.id()))
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

        // relations
        if (viewModel.relationsList.isNullOrEmpty()) {
            mediaData?.relations()?.edges()?.forEach {
                viewModel.relationsList.add(
                    MediaRelations(
                        it.node()?.id()!!,
                        it.node()?.title()?.userPreferred()!!,
                        it.node()?.coverImage()?.extraLarge(),
                        it.node()?.type()!!,
                        it.node()?.format()!!,
                        it.relationType()!!
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

        // characters
        // staffs
        // recommendations
        // tags
        // links
    }

    private fun assignStudioAdapter(list: List<KeyValueItem>): OverviewStudiosRvAdapter {
        return OverviewStudiosRvAdapter(activity, list, object : OverviewStudiosRvAdapter.OverviewStudioListener {
            override fun passSelectedStudio(studioId: Int?) {
                if (studioId == null) return
                // TODO: go to studio
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
}
