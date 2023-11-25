package com.zen.alchan.ui.medialist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.data.network.apollo.adapter.JsonAdapter
import com.zen.alchan.data.response.anilist.MediaList
import com.zen.alchan.databinding.DialogBottomSheetMediaListQuickDetailBinding
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseDialogFragment
import com.zen.alchan.ui.editor.AdvancedScoringRvAdapter
import com.zen.alchan.ui.editor.CustomListsRvAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.zen.alchan.type.MediaType
import com.zen.alchan.type.ScoreFormat

class BottomSheetMediaListQuickDetailDialog : BaseDialogFragment<DialogBottomSheetMediaListQuickDetailBinding>() {

    private val viewModel by viewModel<BottomSheetMediaListQuickDetailViewModel>()

    private lateinit var mediaList: MediaList

    private var advancedScoringAdapter: AdvancedScoringRvAdapter? = null
    private var customListsAdapter: CustomListsRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogBottomSheetMediaListQuickDetailBinding {
        return DialogBottomSheetMediaListQuickDetailBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {

    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.settings.subscribe { (mediaListOptions, appSetting) ->
                val media = mediaList.media

                with(binding) {
                    dialogMediaListTitle.text = media.getTitle(appSetting)
                    dialogMediaListFormat.text = media.getFormattedMediaFormat(true)

                    dialogMediaListStatus.text = mediaList.status?.getString(media.type?.getMediaType() ?: com.zen.alchan.helper.enums.MediaType.ANIME)

                    if (mediaList.progress == 0) {
                        dialogMediaListProgressDivider.show(false)
                        dialogMediaListProgress.show(false)
                    } else {
                        dialogMediaListProgressDivider.show(true)
                        dialogMediaListProgress.show(true)
                        dialogMediaListProgress.text = mediaList.progress.showUnit(requireContext(),
                            when (media.type) {
                                MediaType.ANIME -> R.plurals.episode
                                MediaType.MANGA -> R.plurals.chapter
                                else -> R.plurals.episode
                            }
                        )
                    }

                    if (mediaList.progressVolumes == null || mediaList.progressVolumes == 0) {
                        dialogMediaListProgressVolumeDivider.show(false)
                        dialogMediaListProgressVolume.show(false)
                    } else {
                        dialogMediaListProgressVolumeDivider.show(true)
                        dialogMediaListProgressVolume.show(true)
                        dialogMediaListProgressVolume.text = mediaList.progressVolumes?.showUnit(requireContext(), R.plurals.volume)
                    }

                    if (mediaListOptions.scoreFormat == ScoreFormat.POINT_3) {
                        dialogMediaListScoreIcon.show(false)
                        dialogMediaListScoreText.show(false)
                        dialogMediaListScoreSmiley.show(true)
                        ImageUtil.loadImage(requireContext(), mediaList.getScoreSmiley(), dialogMediaListScoreSmiley)
                    } else {
                        dialogMediaListScoreIcon.show(true)
                        dialogMediaListScoreText.show(true)
                        dialogMediaListScoreSmiley.show(false)
                        dialogMediaListScoreText.text = mediaList.getScore()
                    }

                    val useAdvancedScoring = when (media.type) {
                        MediaType.ANIME -> mediaListOptions.animeList.advancedScoringEnabled && mediaListOptions.animeList.advancedScoring.isNotEmpty()
                        MediaType.MANGA -> mediaListOptions.mangaList.advancedScoringEnabled && mediaListOptions.mangaList.advancedScoring.isNotEmpty()
                        else -> mediaListOptions.animeList.advancedScoringEnabled && mediaListOptions.animeList.advancedScoring.isNotEmpty()
                    }
                    if (!useAdvancedScoring) {
                        dialogMediaListAdvancedScoring.show(false)
                    } else {
                        val advancedScores = (mediaList.advancedScores as? LinkedHashMap<String, Double>?)?.toList() ?: listOf()
                        dialogMediaListAdvancedScoring.show(true)
                        advancedScoringAdapter = AdvancedScoringRvAdapter(
                            advancedScores,
                            mediaListOptions.scoreFormat ?: ScoreFormat.POINT_100,
                            true,
                            object : AdvancedScoringRvAdapter.AdvancedScoringListener {
                                override fun getNewAdvancedScore(newScore: Pair<String, Double>) {
                                    // do nothing
                                }
                            }
                        )
                        dialogMediaListAdvancedScoring.adapter = advancedScoringAdapter
                    }

                    dialogMediaListStartDate.text = TimeUtil.getReadableDateFromFuzzyDate(mediaList.startedAt)
                    dialogMediaListFinishDate.text = TimeUtil.getReadableDateFromFuzzyDate(mediaList.completedAt)
                    dialogMediaListRepeatingLabel.text = getString(
                        when (media.type) {
                            MediaType.ANIME -> R.string.total_rewatches
                            MediaType.MANGA -> R.string.total_rereads
                            else -> R.string.total_rewatches
                        }
                    )
                    dialogMediaListRepeating.text = mediaList.repeat.getNumberFormatting()
                    dialogMediaListNotes.text = mediaList.notes.ifBlank { "-" }
                    dialogMediaListPriority.text = getString(mediaList.getPriorityStringRes())

                    val useCustomLists = when (media.type) {
                        MediaType.ANIME -> mediaListOptions.animeList.customLists.isNotEmpty()
                        MediaType.MANGA -> mediaListOptions.mangaList.customLists.isNotEmpty()
                        else -> mediaListOptions.animeList.customLists.isNotEmpty()
                    }
                    if (!useCustomLists) {
                        dialogMediaListCustomListsLayout.show(false)
                    } else {
                        val customLists = (mediaList.customLists as? LinkedHashMap<String, Boolean>?)?.toList() ?: listOf()
                        dialogMediaListCustomListsLayout.show(true)
                        customListsAdapter = CustomListsRvAdapter(customLists, true, object : CustomListsRvAdapter.CustomListsListener {
                            override fun getNewCustomList(newCustomList: Pair<String, Boolean>) {
                                // do nothing
                            }
                        })
                        dialogMediaListCustomListRecyclerView.adapter = customListsAdapter
                    }
                }
            }
        )

        arguments?.getInt(USER_ID)?.let {
            viewModel.loadData(BottomSheetMediaListQuickDetailParam(it))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        advancedScoringAdapter = null
        customListsAdapter = null
    }

    companion object {
        private const val USER_ID = "userId"

        fun newInstance(userId: Int, mediaList: MediaList) = BottomSheetMediaListQuickDetailDialog().apply {
            arguments = Bundle().apply {
                putInt(USER_ID, userId)
            }

            this.mediaList = mediaList
        }
    }
}