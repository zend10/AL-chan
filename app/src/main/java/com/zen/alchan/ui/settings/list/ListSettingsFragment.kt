package com.zen.alchan.ui.settings.list

import android.text.InputType
import com.zen.alchan.R
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.common.BottomSheetTextInputDialog
import com.zen.alchan.ui.common.ChipRvAdapter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_list_settings.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.toolbar_default.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.ScoreFormat

class ListSettingsFragment : BaseFragment(R.layout.fragment_list_settings) {

    private val viewModel by viewModel<ListSettingsViewModel>()

    private var scoreFormatAdapter: ScoreFormatRvAdapter? = null
    private var advancedScoringCriteriaAdapter: ChipRvAdapter? = null

    private var textInputSetting = TextInputSetting(InputType.TYPE_TEXT_FLAG_CAP_WORDS, true, 30)

    override fun setUpLayout() {
        setUpToolbar(defaultToolbar, getString(R.string.list_settings))

        advancedScoringCriteriaAdapter = ChipRvAdapter(listOf(), object : ChipRvAdapter.ChipListener {
            override fun getSelectedItem(item: String, index: Int) {
                showAdvancedScoringDialog(item, index)
            }

            override fun deleteItem(index: Int) {
                viewModel.deleteAdvancedScoringCriteria(index)
            }
        })
        listSettingsAdvancedScoringCriteriaRecyclerView.adapter = advancedScoringCriteriaAdapter

        listSettingsScoringSystemLayout.clicks {
            viewModel.getScoreFormats()
        }

        listSettingsUseAdvancedScoringCheckBox.setOnClickListener {
            viewModel.updateUseAdvancedScoring(listSettingsUseAdvancedScoringCheckBox.isChecked)
        }

        listSettingsAdvancedScoringCriteriaAddMoreText.clicks {
            showAdvancedScoringDialog("")
        }

        listSettingsDefaultListOrderLayout.clicks {

        }

        listSettingsSplitAnimeCompletedCheckBox.clicks {

        }

        listSettingsSplitMangaCompletedCheckBox.clicks {

        }

        listSettingsCustomAnimeListsAddMoreText.clicks {

        }

        listSettingsCustomMangaListsAddMoreText.clicks {

        }

        listSettingsAnimeSectionOrderResetText.clicks {

        }

        listSettingsAnimeSectionOrderReorderText.clicks {

        }

        listSettingsMangaSectionOrderResetText.clicks {

        }

        listSettingsMangaSectionOrderReorderText.clicks {

        }

        listSettingsSaveButton.clicks {

        }
    }

    override fun setUpInsets() {
        defaultToolbar.applyTopPaddingInsets()
        listSettingsLayout.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.loading.subscribe {
                loadingLayout.show(it)
            }
        )

        disposables.add(
            Observable.merge(viewModel.success, viewModel.error).subscribe {
                dialog.showToast(it)
            }
        )

        disposables.add(
            viewModel.scoringSystem.subscribe {
                listSettingsScoringSystemText.text = it.getString(requireContext())
            }
        )

        disposables.add(
            viewModel.useAdvancedScoring.subscribe {
                listSettingsUseAdvancedScoringCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.advancedScoringCriteria.subscribe {
                advancedScoringCriteriaAdapter?.updateData(it)

            }
        )





        disposables.add(
            viewModel.scoreFormats.subscribe {
                scoreFormatAdapter = ScoreFormatRvAdapter(requireContext(), it, object : ScoreFormatRvAdapter.ScoreFormatListener {
                    override fun getSelectedScoreFormat(scoreFormat: ScoreFormat) {
                        dismissListDialog()
                        viewModel.updateScoringSystem(scoreFormat)
                    }
                }).also { adapter ->
                    showListDialog(adapter)
                }
            }
        )

        disposables.add(
            viewModel.useAdvancedScoringVisibility.subscribe {
                listSettingsUseAdvancedScoringLayout.show(it)
            }
        )

        disposables.add(
            viewModel.advancedScoringCriteriaVisibility.subscribe {
                listSettingsAdvancedScoringCriteriaLayout.show(it)
            }
        )

        disposables.add(
            viewModel.advancedScoringNoItemTextVisibility.subscribe {
                listSettingsAdvancedScoringCriteriaNoItemText.show(it)
            }
        )

        viewModel.loadData()
    }

    private fun showAdvancedScoringDialog(currentText: String, index: Int? = null) {
        showTextInputDialog(currentText, textInputSetting, object : BottomSheetTextInputDialog.BottomSheetTextInputListener {
            override fun getNewText(newText: String) {
                if (index != null)
                    viewModel.editAdvancedScoringCriteria(newText, index)
                else
                    viewModel.addAdvancedScoringCriteria(newText)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scoreFormatAdapter = null
        advancedScoringCriteriaAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListSettingsFragment()
    }
}