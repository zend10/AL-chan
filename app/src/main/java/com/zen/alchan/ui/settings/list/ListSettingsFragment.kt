package com.zen.alchan.ui.settings.list

import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentListSettingsBinding
import com.zen.alchan.helper.enums.ListOrder
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.common.BottomSheetTextInputDialog
import com.zen.alchan.ui.common.ChipRvAdapter
import io.reactivex.Observable
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.ScoreFormat

class ListSettingsFragment : BaseFragment<FragmentListSettingsBinding, ListSettingsViewModel>() {

    override val viewModel: ListSettingsViewModel by viewModel()

    private var advancedScoringCriteriaAdapter: ChipRvAdapter? = null

    private var textInputSetting = TextInputSetting(InputType.TYPE_TEXT_FLAG_CAP_WORDS, true, 30)

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListSettingsBinding {
        return FragmentListSettingsBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.list_settings))

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
                viewModel.loadScoreFormatItems()
            }

            listSettingsUseAdvancedScoringCheckBox.setOnClickListener {
                viewModel.updateAdvancedScoringEnabled(listSettingsUseAdvancedScoringCheckBox.isChecked)
            }

            listSettingsAdvancedScoringCriteriaAddMoreText.clicks {
                showAdvancedScoringDialog("")
            }

            listSettingsDefaultListOrderLayout.clicks {
                viewModel.getListOrders()
            }

            listSettingsSplitAnimeCompletedCheckBox.setOnClickListener {

            }

            listSettingsSplitMangaCompletedCheckBox.setOnClickListener {

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
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.listSettingsLayout.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
            },
            Observable.merge(viewModel.success, viewModel.error).subscribe {
                dialog.showToast(it)
            },
            viewModel.scoreFormat.subscribe {
                binding.listSettingsScoringSystemText.text = getString(viewModel.getScoreFormatStringResource(it))
            },
            viewModel.advancedScoringEnabled.subscribe {
                binding.listSettingsUseAdvancedScoringCheckBox.isChecked = it
            },
            viewModel.advancedScoring.subscribe {
                advancedScoringCriteriaAdapter?.updateData(it)
            },
            viewModel.rowOrder.subscribe {
                binding.listSettingsDefaultListOrderText.text = getString(viewModel.getListOrderStringResource(it))
            },
            viewModel.scoreFormatItems.subscribe {
                showListDialog(it) { data, _ ->
                    viewModel.updateScoreFormat(data)
                }
            },
            viewModel.useAdvancedScoringVisibility.subscribe {
                binding.listSettingsUseAdvancedScoringLayout.show(it)
            },
            viewModel.advancedScoringVisibility.subscribe {
                binding.listSettingsAdvancedScoringCriteriaLayout.show(it)
            },
            viewModel.advancedScoringNoItemTextVisibility.subscribe {
                binding.listSettingsAdvancedScoringCriteriaNoItemText.show(it)
            },
            viewModel.listOrderItems.subscribe {
                showListDialog(it) { data, _ ->
                    viewModel.updateRowOrder(data)
                }
            }
        )

        viewModel.loadData()
    }

    private fun showAdvancedScoringDialog(currentText: String, index: Int? = null) {
        showTextInputDialog(currentText, textInputSetting, object : BottomSheetTextInputDialog.BottomSheetTextInputListener {
            override fun getNewText(newText: String) {
                if (index != null)
                    viewModel.editAdvancedScoring(newText, index)
                else
                    viewModel.addAdvancedScoring(newText)

                dismissTextInputDialog()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        advancedScoringCriteriaAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListSettingsFragment()
    }
}