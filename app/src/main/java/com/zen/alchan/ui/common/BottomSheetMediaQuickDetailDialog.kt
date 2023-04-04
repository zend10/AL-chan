package com.zen.alchan.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.entity.AppSetting
import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.databinding.DialogBottomSheetMediaQuickDetailBinding
import com.zen.alchan.helper.extensions.getNumberFormatting
import com.zen.alchan.helper.extensions.getString
import com.zen.alchan.helper.utils.MarkdownUtil
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaListStatus
import type.MediaType

class BottomSheetMediaQuickDetailDialog : BaseDialogFragment<DialogBottomSheetMediaQuickDetailBinding>() {

    private val viewModel by viewModel<BottomSheetMediaQuickDetailViewModel>()

    private lateinit var media: Media

    private var genreAdapter: GenreRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogBottomSheetMediaQuickDetailBinding {
        return DialogBottomSheetMediaQuickDetailBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        genreAdapter = GenreRvAdapter(requireContext(), listOf(), null)
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.appSetting.subscribe { appSetting ->
                with(binding) {
                    dialogMediaTitle.text = media.getTitle(appSetting)
                    dialogMediaFormat.text = media.getFormattedMediaFormat(true)

                    genreAdapter = GenreRvAdapter(requireContext(), media.genres, null)
                    dialogMediaGenre.adapter = genreAdapter

                    dialogMediaStartDate.text = getString(R.string.starts_on_x, TimeUtil.getReadableDateFromFuzzyDate(media.startDate))
                    dialogMediaSource.text = getString(R.string.adapted_from_x, media.source?.getString() ?: "-")

                    dialogMediaScore.text = media.averageScore.getNumberFormatting()
                    dialogMediaFavorite.text = media.favourites.getNumberFormatting()
                    dialogMediaPopularity.text = media.popularity.getNumberFormatting()

                    dialogMediaCurrentLabel.text = when (media.type) {
                        MediaType.ANIME -> getString(R.string.watching)
                        MediaType.MANGA -> getString(R.string.reading)
                        else -> getString(R.string.watching)
                    }
                    dialogMediaCurrentCount.text = media.stats?.statusDistribution?.find { it.status == MediaListStatus.CURRENT }?.amount?.getNumberFormatting()
                    dialogMediaPlanningCount.text = media.stats?.statusDistribution?.find { it.status == MediaListStatus.PLANNING }?.amount?.getNumberFormatting()
                    dialogMediaCompletedCount.text = media.stats?.statusDistribution?.find { it.status == MediaListStatus.COMPLETED }?.amount?.getNumberFormatting()
                    dialogMediaDroppedCount.text = media.stats?.statusDistribution?.find { it.status == MediaListStatus.DROPPED }?.amount?.getNumberFormatting()
                    dialogMediaPausedCount.text = media.stats?.statusDistribution?.find { it.status == MediaListStatus.PAUSED }?.amount?.getNumberFormatting()

                    MarkdownUtil.applyMarkdown(requireContext(), dialogMediaDescription, media.description)
                }
            }
        )

        viewModel.loadData(Unit)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        genreAdapter = null
    }

    companion object {
        fun newInstance(media: Media) = BottomSheetMediaQuickDetailDialog().apply {
            this.media = media
        }
    }
}