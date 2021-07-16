package com.zen.alchan.ui.settings.list

import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentListSettingsBinding
import com.zen.alchan.helper.enums.ListOrder
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.common.BottomSheetTextInputDialog
import com.zen.alchan.ui.common.ChipRvAdapter
import com.zen.alchan.ui.reorder.ReorderRvAdapter
import com.zen.alchan.ui.reorder.SharedReorderViewModel
import io.reactivex.Observable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

import type.ScoreFormat

class ListSettingsFragment : BaseFragment<FragmentListSettingsBinding, ListSettingsViewModel>() {

    override val viewModel: ListSettingsViewModel by viewModel()
    private val sharedViewModel by sharedViewModel<SharedReorderViewModel>()

    private var advancedScoringAdapter: ChipRvAdapter? = null
    private var animeCustomListsAdapter: ChipRvAdapter? = null
    private var mangaCustomListsAdapter: ChipRvAdapter? = null
    private var animeSectionOrderAdapter: ReorderRvAdapter? = null
    private var mangaSectionOrderAdapter: ReorderRvAdapter? = null

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

            advancedScoringAdapter = ChipRvAdapter(listOf(), object : ChipRvAdapter.ChipListener {
                override fun getSelectedItem(item: String, index: Int) {
                    showAdvancedScoringDialog(item, index)
                }

                override fun deleteItem(index: Int) {
                    viewModel.deleteAdvancedScoringCriteria(index)
                }
            })
            listSettingsAdvancedScoringCriteriaRecyclerView.adapter = advancedScoringAdapter

            animeCustomListsAdapter = ChipRvAdapter(listOf(), object : ChipRvAdapter.ChipListener {
                override fun getSelectedItem(item: String, index: Int) {
                    showCustomListsDialog(MediaType.ANIME, item, index)
                }

                override fun deleteItem(index: Int) {
                    viewModel.deleteCustomLists(MediaType.ANIME, index)
                }
            })
            listSettingsCustomAnimeListsRecyclerView.adapter = animeCustomListsAdapter

            mangaCustomListsAdapter = ChipRvAdapter(listOf(), object : ChipRvAdapter.ChipListener {
                override fun getSelectedItem(item: String, index: Int) {
                    showCustomListsDialog(MediaType.MANGA, item, index)
                }

                override fun deleteItem(index: Int) {
                    viewModel.deleteCustomLists(MediaType.MANGA, index)
                }
            })
            listSettingsCustomMangaListsRecyclerView.adapter = mangaCustomListsAdapter

            animeSectionOrderAdapter = ReorderRvAdapter(listOf(), null)
            listSettingsAnimeSectionOrderRecyclerView.adapter = animeSectionOrderAdapter

            mangaSectionOrderAdapter = ReorderRvAdapter(listOf(), null)
            listSettingsMangaSectionOrderRecyclerView.adapter = mangaSectionOrderAdapter

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
                viewModel.loadListOrderItems()
            }

            listSettingsSplitAnimeCompletedCheckBox.setOnClickListener {
                dialog.showConfirmationDialog(
                    R.string.reset_anime_section_order,
                    R.string.by_changing_this_setting_your_anime_section_order_will_be_reset,
                    R.string.proceed,
                    {
                        viewModel.updateSplitCompletedAnimeSectionByFormat(listSettingsSplitAnimeCompletedCheckBox.isChecked)
                        viewModel.resetSectionOrder(MediaType.ANIME)
                    },
                    R.string.cancel,
                    {
                        listSettingsSplitAnimeCompletedCheckBox.isChecked = !listSettingsSplitAnimeCompletedCheckBox.isChecked
                    }
                )
            }

            listSettingsSplitMangaCompletedCheckBox.setOnClickListener {
                dialog.showConfirmationDialog(
                    R.string.reset_manga_section_order,
                    R.string.by_changing_this_setting_your_manga_section_order_will_be_reset,
                    R.string.proceed,
                    {
                        viewModel.updateSplitCompletedMangaSectionByFormat(listSettingsSplitMangaCompletedCheckBox.isChecked)
                        viewModel.resetSectionOrder(MediaType.MANGA)
                    },
                    R.string.cancel,
                    {
                        listSettingsSplitMangaCompletedCheckBox.isChecked = !listSettingsSplitMangaCompletedCheckBox.isChecked
                    }
                )
            }

            listSettingsCustomAnimeListsAddMoreText.clicks {
                showCustomListsDialog(MediaType.ANIME, "")
            }

            listSettingsCustomMangaListsAddMoreText.clicks {
                showCustomListsDialog(MediaType.MANGA, "")
            }

            listSettingsAnimeSectionOrderResetText.clicks {
                dialog.showConfirmationDialog(
                    R.string.reset_anime_section_order,
                    R.string.are_you_sure_you_want_to_reset_your_anime_section_order,
                    R.string.reset,
                    { viewModel.resetSectionOrder(MediaType.ANIME) },
                    R.string.cancel,
                    { }
                )
            }

            listSettingsAnimeSectionOrderReorderText.clicks {
                viewModel.loadSectionOrderItems(MediaType.ANIME)
            }

            listSettingsMangaSectionOrderResetText.clicks {
                dialog.showConfirmationDialog(
                    R.string.reset_manga_section_order,
                    R.string.are_you_sure_you_want_to_reset_your_manga_section_order,
                    R.string.reset,
                    { viewModel.resetSectionOrder(MediaType.MANGA) },
                    R.string.cancel,
                    { }
                )
            }

            listSettingsMangaSectionOrderReorderText.clicks {
                viewModel.loadSectionOrderItems(MediaType.MANGA)
            }

            listSettingsSaveLayout.positiveButton.text = getString(R.string.save_changes)
            listSettingsSaveLayout.positiveButton.clicks {
                viewModel.saveListSettings()
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.listSettingsSaveLayout.oneButtonLayout.applyBottomPaddingInsets()
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
                advancedScoringAdapter?.updateData(it)
            },
            viewModel.rowOrder.subscribe {
                binding.listSettingsDefaultListOrderText.text = getString(viewModel.getListOrderStringResource(it))
            },
            viewModel.splitCompletedAnimeSectionByFormat.subscribe {
                binding.listSettingsSplitAnimeCompletedCheckBox.isChecked = it
            },
            viewModel.splitCompletedMangaSectionByFormat.subscribe {
                binding.listSettingsSplitMangaCompletedCheckBox.isChecked = it
            },
            viewModel.animeCustomLists.subscribe {
                animeCustomListsAdapter?.updateData(it)
            },
            viewModel.mangaCustomLists.subscribe {
                mangaCustomListsAdapter?.updateData(it)
            },
            viewModel.animeSectionOrder.subscribe {
                animeSectionOrderAdapter?.updateData(it)
            },
            viewModel.mangaSectionOrder.subscribe {
                mangaSectionOrderAdapter?.updateData(it)
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
            },
            viewModel.animeCustomListsNoItemTextVisibility.subscribe {
                binding.listSettingsCustomAnimeListsNoItemText.show(it)
            },
            viewModel.mangaCustomListsNoItemTextVisibility.subscribe {
                binding.listSettingsCustomMangaListsNoItemText.show(it)
            },
            viewModel.animeSectionOrderItems.subscribe {
                sharedViewModel.updateUnorderedList(it, SharedReorderViewModel.ReorderList.ANIME_SECTION_ORDER)
                navigation.navigateToReorder()
            },
            viewModel.mangaSectionOrderItems.subscribe {
                sharedViewModel.updateUnorderedList(it, SharedReorderViewModel.ReorderList.MANGA_SECTION_ORDER)
                navigation.navigateToReorder()
            }
        )

        sharedDisposables.addAll(
            sharedViewModel.orderedList.subscribe { (orderedList, reorderList) ->
                when (reorderList) {
                    SharedReorderViewModel.ReorderList.ANIME_SECTION_ORDER -> {
                        viewModel.updateSectionOrder(MediaType.ANIME, orderedList)
                    }
                    SharedReorderViewModel.ReorderList.MANGA_SECTION_ORDER -> {
                        viewModel.updateSectionOrder(MediaType.MANGA, orderedList)
                    }
                }
            }
        )

        viewModel.loadData()
    }

    private fun showAdvancedScoringDialog(currentText: String, index: Int? = null) {
        showTextInputDialog(currentText, textInputSetting, object : BottomSheetTextInputDialog.BottomSheetTextInputListener {
            override fun getNewText(newText: String) {
                if (newText.isNotBlank()) {
                    if (index != null)
                        viewModel.editAdvancedScoring(newText, index)
                    else
                        viewModel.addAdvancedScoring(newText)
                }


                dismissTextInputDialog()
            }
        })
    }

    private fun showCustomListsDialog(mediaType: MediaType, currentText: String, index: Int? = null) {
        showTextInputDialog(currentText, textInputSetting, object : BottomSheetTextInputDialog.BottomSheetTextInputListener {
            override fun getNewText(newText: String) {
                if (newText.isNotBlank()) {
                    if (index != null)
                        viewModel.editCustomLists(mediaType, newText, index)
                    else
                        viewModel.addCustomLists(mediaType, newText)
                }

                dismissTextInputDialog()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        advancedScoringAdapter = null
        animeCustomListsAdapter = null
        mangaCustomListsAdapter = null
        animeSectionOrderAdapter = null
        mangaSectionOrderAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListSettingsFragment()
    }
}