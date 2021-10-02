package com.zen.alchan.ui.editor

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentEditorBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.utils.TimeUtil
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditorFragment : BaseFragment<FragmentEditorBinding, EditorViewModel>() {

    override val viewModel: EditorViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditorBinding {
        return FragmentEditorBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.mediaType = MediaType.valueOf(it.getString(MEDIA_TYPE) ?: MediaType.ANIME.name)
            viewModel.mediaId = it.getInt(MEDIA_ID)
        }
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.list_editor))

            editorMediaTitle.clicks {

            }

            editorFavoriteIcon.setOnClickListener {

            }

            editorStatusText.clicks {

            }

            editorScoreText.clicks {

            }

            editorProgressText.clicks {

            }

            editorProgressVolumeText.clicks {

            }

            editorStartDateText.clicks {

            }

            editorFinishDateText.clicks {

            }

            editorTotalRewatchesText.clicks {

            }

            editorTotalNotesText.clicks {

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
//            editorSaveLayout.positiveButton.clicks {
//
//            }

            editorSaveLayout.negativeButton.text = getString(R.string.remove_from_list)
//            editorSaveLayout.negativeButton.clicks {
//
//            }
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
            viewModel.isFavorite.subscribe {
                binding.editorFavoriteIcon.imageTintList = if (it)
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.brightRed))
                else
                    ColorStateList.valueOf(requireContext().getAttrValue(R.attr.themeContentColor))
            },
            viewModel.status.subscribe {
                binding.editorStatusText.text = it.name
            },
            viewModel.score.subscribe {
                binding.editorScoreText.text = it.toString()
            },
            viewModel.progress.subscribe {
                binding.editorProgressText.text = it.toString()
            },
            viewModel.progressVolume.subscribe {
                binding.editorProgressVolumeText.text = it.toString()
            },
            viewModel.startDate.subscribe {
                binding.editorStartDateText.text = if (it.data != null)
                    TimeUtil.getMillisFromFuzzyDate(it.data).toString()
                else
                    "-"
            },
            viewModel.finishDate.subscribe {
                binding.editorFinishDateText.text = if (it.data != null)
                    TimeUtil.getMillisFromFuzzyDate(it.data).toString()
                else
                    "-"
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
            viewModel.hideFromStatusList.subscribe {
                binding.editorHideFromStatusListsCheckBox.isChecked = it
            },
            viewModel.isPrivate.subscribe {
                binding.editorPrivateCheckBox.isChecked = it
            },
            viewModel.prioritySliderItem.subscribe {
                dialog.showSliderDialog(it, true) { minValue, _ ->
                    viewModel.updatePriority(minValue ?: 0)
                }
            }
        )

        viewModel.loadData()
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

    companion object {
        const val MEDIA_TYPE = "mediaType"
        const val MEDIA_ID = "mediaId"

        @JvmStatic
        fun newInstance(mediaType: MediaType, mediaId: Int) =
            EditorFragment().apply {
                arguments = Bundle().apply {
                    putString(MEDIA_TYPE, mediaType.name)
                    putInt(MEDIA_ID, mediaId)
                }
            }
    }
}