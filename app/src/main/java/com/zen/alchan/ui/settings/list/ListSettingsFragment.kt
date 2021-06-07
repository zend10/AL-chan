package com.zen.alchan.ui.settings.list

import com.zen.alchan.R
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.ui.base.BaseFragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_list_settings.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.toolbar_default.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.ScoreFormat

class ListSettingsFragment : BaseFragment(R.layout.fragment_list_settings) {

    private val viewModel by viewModel<ListSettingsViewModel>()

    private var scoreFormatAdapter: ScoreFormatRvAdapter? = null

    override fun setUpLayout() {
        setUpToolbar(defaultToolbar, getString(R.string.list_settings))

        listSettingsScoringSystemLayout.clicks {
            viewModel.getScoreFormats()
        }

        listSettingsUseAdvancedScoringCheckBox.clicks {
            viewModel.updateUseAdvancedScoring(listSettingsUseAdvancedScoringCheckBox.isChecked)
        }

        listSettingsAdvancedScoringCriteriaAddMoreText.clicks {

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
                listSettingsScoringSystemText.text = it.name.convertFromSnakeCase()
            }
        )

        disposables.add(
            viewModel.useAdvancedScoring.subscribe {
                listSettingsUseAdvancedScoringCheckBox.isChecked = it
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

        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scoreFormatAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListSettingsFragment()
    }
}