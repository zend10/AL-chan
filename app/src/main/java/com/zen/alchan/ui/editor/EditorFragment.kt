package com.zen.alchan.ui.editor

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.zen.alchan.R
import com.zen.alchan.data.response.anilist.FuzzyDate
import com.zen.alchan.databinding.FragmentEditorBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.utils.ImageUtil
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.ScoreFormat

class EditorFragment : BaseFragment<FragmentEditorBinding, EditorViewModel>() {

    override val viewModel: EditorViewModel by viewModel()

    private var listener: EditorListener? = null

    private var customListsAdapter: CustomListsRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditorBinding {
        return FragmentEditorBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.mediaId = it.getInt(MEDIA_ID)
        }
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.list_editor))

            customListsAdapter = CustomListsRvAdapter(listOf(), object : CustomListsRvAdapter.CustomListsListener {
                override fun getNewCustomList(newCustomList: Pair<String, Boolean>) {
                    viewModel.updateCustomList(newCustomList)
                }
            })
            editorCustomListsRecyclerView.adapter = customListsAdapter

            editorMediaTitle.clicks {
                arguments?.getBoolean(FROM_MEDIA_LIST)?.let { fromMediaList ->
                    goBack()
                    if (fromMediaList) {
                        arguments?.getInt(MEDIA_ID)?.let { navigation.navigateToMedia(it) }
                    }
                }
            }

            editorFavoriteIcon.setOnClickListener {
                viewModel.updateIsFavorite()
            }

            editorStatusText.clicks {
                viewModel.loadMediaListStatuses()
            }

            editorScoreText.clicks {
                viewModel.loadScoreValues()
            }

            editorScoreSmiley.clicks {
                viewModel.loadScoreValues()
            }

            editorProgressText.clicks {
                viewModel.loadProgressValues(false)
            }

            editorProgressVolumeText.clicks {
                viewModel.loadProgressValues(true)
            }

            editorStartDateText.clicks {
                viewModel.loadCalendarStartDate()
            }

            editorStartDateRemoveIcon.clicks {
                viewModel.updateStartDate(null)
            }

            editorFinishDateText.clicks {
                viewModel.loadCalendarFinishDate()
            }

            editorFinishDateRemoveIcon.clicks {
                viewModel.updateFinishDate(null)
            }

            editorTotalRewatchesText.clicks {
                viewModel.loadRewatchesTextInputSetting()
            }

            editorTotalNotesText.clicks {
                viewModel.loadNotesTextInputSetting()
            }

            editorPriorityText.clicks {
                viewModel.loadPrioritySliderItem()
            }

            editorHideFromStatusListsCheckBox.setOnClickListener {
                viewModel.updateShouldHideFromStatusLists(editorHideFromStatusListsCheckBox.isChecked)
            }

            editorPrivateCheckBox.setOnClickListener {
                viewModel.updateIsPrivate(editorPrivateCheckBox.isChecked)
            }

            editorSaveLayout.positiveButton.text = getString(R.string.save)
            editorSaveLayout.positiveButton.clicks {
                viewModel.saveMediaList()
            }

            editorSaveLayout.negativeButton.text = getString(R.string.remove_from_list)
            editorSaveLayout.negativeButton.clicks {
                dialog.showConfirmationDialog(
                    R.string.remove_from_list,
                    R.string.are_you_sure_you_want_to_remove_this_entry_from_your_list,
                    R.string.remove,
                    {
                        viewModel.deleteMediaList()
                    },
                    R.string.cancel,
                    {}
                )
            }
        }
    }

    override fun setUpInsets() {
        binding.apply {
            defaultToolbar.defaultToolbar.applyTopPaddingInsets()
            editorLayout.applySidePaddingInsets()
            editorSaveLayout.twoButtonsLayout.applyBottomSidePaddingInsets()
        }
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
            },
            viewModel.success.subscribe {
                dialog.showToast(it)
                listener?.onEntryEdited()
            },
            viewModel.deleteSuccess.subscribe {
                dialog.showToast(it)
                listener?.onEntryEdited()
                goBack()
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.closePage.subscribe {
                goBack()
            },
            viewModel.title.subscribe {
                binding.editorMediaTitle.text = it
            },
            viewModel.isFavorite.subscribe {
                binding.editorFavoriteIcon.imageTintList = if (it)
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.brightRed))
                else
                    ColorStateList.valueOf(requireContext().getAttrValue(R.attr.themeContentColor))
            },
            viewModel.status.subscribe {
                binding.editorStatusText.text = it.getString(viewModel.mediaType)
            },
            viewModel.score.subscribe {
                setScore(it)
            },
            viewModel.progress.subscribe {
                binding.editorProgressText.text = it.toString()
            },
            viewModel.progressVolume.subscribe {
                binding.editorProgressVolumeText.text = it.toString()
            },
            viewModel.startDate.subscribe {
                binding.editorStartDateText.text = TimeUtil.getReadableDateFromFuzzyDate(it.data)
            },
            viewModel.finishDate.subscribe {
                binding.editorFinishDateText.text = TimeUtil.getReadableDateFromFuzzyDate(it.data)
            },
            viewModel.totalRewatches.subscribe {
                binding.editorTotalRewatchesText.text = it.toString()
            },
            viewModel.notes.subscribe {
                binding.editorTotalNotesText.text = if (it.isNotBlank()) it else "-"
            },
            viewModel.priority.subscribe {
                binding.editorPriorityText.text = getPriorityText(it)
            },
            viewModel.customLists.subscribe {
                customListsAdapter?.updateData(it.data?.toList() ?: listOf())
            },
            viewModel.hideFromStatusList.subscribe {
                binding.editorHideFromStatusListsCheckBox.isChecked = it
            },
            viewModel.isPrivate.subscribe {
                binding.editorPrivateCheckBox.isChecked = it
            },
            viewModel.customListsVisibility.subscribe {
                binding.editorCustomListsLayout.show(it)
            },
            viewModel.progressVolumeVisibility.subscribe {
                binding.editorProgressVolumeLayout.show(it)
            },
            viewModel.scoreTextVisibility.subscribe {
                binding.editorScoreText.show(it)
            },
            viewModel.scoreSmileyVisibility.subscribe {
                binding.editorScoreSmiley.show(it)
            },
            viewModel.startDateRemoveIconVisibility.subscribe {
                binding.editorStartDateRemoveIcon.show(it)
            },
            viewModel.finishDateRemoveIconVisibility.subscribe {
                binding.editorFinishDateRemoveIcon.show(it)
            },
            viewModel.removeFromListButtonVisibility.subscribe {
                binding.editorSaveLayout.negativeButton.show(it)
            },
            viewModel.mediaListStatuses.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateStatus(data)
                }
            },
            viewModel.scoreValues.subscribe { (scoreFormat: ScoreFormat, currentScore: Double, advancedScores: LinkedHashMap<String, Double>?) ->
                dialog.showScoreDialog(scoreFormat, currentScore, advancedScores) { newScore, newAdvancedScores ->
                    viewModel.updateScore(newScore)
                    viewModel.updateAdvancedScores(newAdvancedScores)
                }
            },
            viewModel.progressValues.subscribe { (progress, maxProgress, isProgressVolume) ->
                dialog.showProgressDialog(viewModel.mediaType, progress, maxProgress, isProgressVolume) { newProgress ->
                    if (isProgressVolume)
                        viewModel.updateProgressVolume(newProgress)
                    else
                        viewModel.updateProgress(newProgress)
                }
            },
            viewModel.calendarStartDate.subscribe {
                dialog.showDatePicker(it) { year, month, dayOfMonth ->
                    viewModel.updateStartDate(FuzzyDate(year, month, dayOfMonth))
                }
            },
            viewModel.calendarFinishDate.subscribe {
                dialog.showDatePicker(it) { year, month, dayOfMonth ->
                    viewModel.updateFinishDate(FuzzyDate(year, month, dayOfMonth))
                }
            },
            viewModel.rewatchesTextInputSetting.subscribe {
                dialog.showTextInputDialog(it.first.toString(), it.second) { newText ->
                    viewModel.updateTotalRewatches(newText.toIntOrNull() ?: 0)
                }
            },
            viewModel.notesTextInputSetting.subscribe {
                dialog.showTextInputDialog(it.first, it.second) { newText ->
                    viewModel.updateNotes(newText)
                }
            },
            viewModel.prioritySliderItem.subscribe {
                dialog.showSliderDialog(it, true) { minValue, _ ->
                    viewModel.updatePriority(minValue ?: 0)
                }
            }
        )

        viewModel.loadData()
    }

    private fun setScore(score: Double) {
        if (binding.editorScoreText.isVisible) {
            binding.editorScoreText.text = score.roundToOneDecimal()
        } else {
            val smiley = when (score) {
                1.0 -> R.drawable.ic_sad
                2.0 -> R.drawable.ic_neutral
                3.0 -> R.drawable.ic_happy
                else -> R.drawable.ic_puzzled
            }
            binding.editorScoreSmiley.setImageResource(smiley)
        }
    }

    private fun getPriorityText(priority: Int): String {
        return getString(when (priority) {
            1 -> R.string.very_low
            2 -> R.string.low
            3 -> R.string.medium
            4 -> R.string.high
            5 -> R.string.very_high
            else -> R.string.no_priority
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        customListsAdapter = null
    }

    companion object {
        const val MEDIA_ID = "mediaId"
        const val FROM_MEDIA_LIST = "fromMediaList"

        @JvmStatic
        fun newInstance(mediaId: Int, fromMediaList: Boolean, listener: EditorListener) =
            EditorFragment().apply {
                arguments = Bundle().apply {
                    putInt(MEDIA_ID, mediaId)
                    putBoolean(FROM_MEDIA_LIST, fromMediaList)
                }
                this.listener = listener
            }
    }

    interface EditorListener {
        fun onEntryEdited()
    }
}