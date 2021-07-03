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

    private var scoreFormatAdapter: ScoreFormatRvAdapter? = null
    private var advancedScoringCriteriaAdapter: ChipRvAdapter? = null
    private var listOrderAdapter: ListOrderRvAdapter? = null

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
                viewModel.getScoreFormats()
            }

            listSettingsUseAdvancedScoringCheckBox.setOnClickListener {
                viewModel.updateUseAdvancedScoring(listSettingsUseAdvancedScoringCheckBox.isChecked)
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
        disposables.add(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
            }
        )

        disposables.add(
            Observable.merge(viewModel.success, viewModel.error).subscribe {
                dialog.showToast(it)
            }
        )

        disposables.add(
            viewModel.scoringSystem.subscribe {
                binding.listSettingsScoringSystemText.text = it.getString(requireContext())
            }
        )

        disposables.add(
            viewModel.useAdvancedScoring.subscribe {
                binding.listSettingsUseAdvancedScoringCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.advancedScoringCriteria.subscribe {
                advancedScoringCriteriaAdapter?.updateData(it)

            }
        )

        disposables.add(
            viewModel.defaultListOrder.subscribe {
                binding.listSettingsDefaultListOrderText.text = it.name.convertFromSnakeCase()
            }
        )




//        disposables.add(
//            viewModel.scoreFormats.subscribe {
//                scoreFormatAdapter = ScoreFormatRvAdapter(requireContext(), it, object : ScoreFormatRvAdapter.ScoreFormatListener {
//                    override fun getSelectedScoreFormat(scoreFormat: ScoreFormat) {
//                        dismissListDialog()
//                        viewModel.updateScoringSystem(scoreFormat)
//                    }
//                }).also { adapter ->
//                    showListDialog(adapter)
//                }
//            }
//        )

        disposables.add(
            viewModel.useAdvancedScoringVisibility.subscribe {
                binding.listSettingsUseAdvancedScoringLayout.show(it)
            }
        )

        disposables.add(
            viewModel.advancedScoringCriteriaVisibility.subscribe {
                binding.listSettingsAdvancedScoringCriteriaLayout.show(it)
            }
        )

        disposables.add(
            viewModel.advancedScoringNoItemTextVisibility.subscribe {
                binding.listSettingsAdvancedScoringCriteriaNoItemText.show(it)
            }
        )

//        disposables.add(
//            viewModel.listOrders.subscribe {
//                listOrderAdapter = ListOrderRvAdapter(requireContext(), it, object : ListOrderRvAdapter.ListOrderListener {
//                    override fun getSelectedListOrder(listOrder: ListOrder) {
//                        dismissListDialog()
//                        viewModel.updateDefaultListOrder(listOrder)
//                    }
//                }).also { adapter ->
//                    showListDialog(adapter)
//                }
//            }
//        )

        viewModel.loadData()
    }

    private fun showAdvancedScoringDialog(currentText: String, index: Int? = null) {
//        showTextInputDialog(currentText, textInputSetting, object : BottomSheetTextInputDialog.BottomSheetTextInputListener {
//            override fun getNewText(newText: String) {
//                if (index != null)
//                    viewModel.editAdvancedScoringCriteria(newText, index)
//                else
//                    viewModel.addAdvancedScoringCriteria(newText)
//
//                dismissTextInputDialog()
//            }
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scoreFormatAdapter = null
        advancedScoringCriteriaAdapter = null
        listOrderAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListSettingsFragment()
    }
}