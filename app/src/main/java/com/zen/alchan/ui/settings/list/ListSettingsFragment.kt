package com.zen.alchan.ui.settings.list

import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentListSettingsBinding
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.enums.getString
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.common.ChipRvAdapter
import com.zen.alchan.ui.reorder.ReorderRvAdapter
import io.reactivex.Observable
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.MediaListStatus

class ListSettingsFragment : BaseFragment<FragmentListSettingsBinding, ListSettingsViewModel>() {

    override val viewModel: ListSettingsViewModel by viewModel()

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

            listSettingsStopWatchingReadingCheckBox.setOnClickListener {
                viewModel.updateDisableListActivity(MediaListStatus.CURRENT, listSettingsStopWatchingReadingCheckBox.isChecked)
            }

            listSettingsStopPlanningCheckBox.setOnClickListener {
                viewModel.updateDisableListActivity(MediaListStatus.PLANNING, listSettingsStopPlanningCheckBox.isChecked)
            }

            listSettingsStopCompletedCheckBox.setOnClickListener {
                viewModel.updateDisableListActivity(MediaListStatus.COMPLETED, listSettingsStopCompletedCheckBox.isChecked)
            }

            listSettingsStopDroppedCheckBox.setOnClickListener {
                viewModel.updateDisableListActivity(MediaListStatus.DROPPED, listSettingsStopDroppedCheckBox.isChecked)
            }

            listSettingsStopPausedCheckBox.setOnClickListener {
                viewModel.updateDisableListActivity(MediaListStatus.PAUSED, listSettingsStopPausedCheckBox.isChecked)
            }

            listSettingsStopRepeatingCheckBox.setOnClickListener {
                viewModel.updateDisableListActivity(MediaListStatus.REPEATING, listSettingsStopRepeatingCheckBox.isChecked)
            }

            listSettingsSaveLayout.positiveButton.text = getString(R.string.save_changes)
            listSettingsSaveLayout.positiveButton.clicks {
                dialog.showConfirmationDialog(
                    R.string.save_changes,
                    R.string.the_app_will_be_restarted_to_apply_the_change,
                    R.string.save,
                    {
                        viewModel.saveListSettings()
                    },
                    R.string.cancel,
                    {}
                )
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.listSettingsLayout.applySidePaddingInsets()
        binding.listSettingsSaveLayout.oneButtonLayout.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
            },
            viewModel.success.subscribe {
                dialog.showToast(it)
                restartApp(DeepLink.generateListSettings(), false)
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.scoreFormat.subscribe {
                binding.listSettingsScoringSystemText.text = it.getString(requireContext())
            },
            viewModel.advancedScoringEnabled.subscribe {
                binding.listSettingsUseAdvancedScoringCheckBox.isChecked = it
            },
            viewModel.advancedScoring.subscribe {
                advancedScoringAdapter?.updateData(it)
            },
            viewModel.rowOrder.subscribe {
                binding.listSettingsDefaultListOrderText.text = it.getString(requireContext())
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
                dialog.showListDialog(it) { data, _ ->
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
                dialog.showListDialog(it) { data, _ ->
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
                navigation.navigateToReorder(it) { orderedList ->
                    viewModel.updateSectionOrder(MediaType.ANIME, orderedList)
                }
            },
            viewModel.mangaSectionOrderItems.subscribe {
                navigation.navigateToReorder(it) { orderedList ->
                    viewModel.updateSectionOrder(MediaType.MANGA, orderedList)
                }
            },
            viewModel.disableWatchingActivity.subscribe {
                binding.listSettingsStopWatchingReadingCheckBox.isChecked = it
            },
            viewModel.disablePlanningActivity.subscribe {
                binding.listSettingsStopPlanningCheckBox.isChecked = it
            },
            viewModel.disableCompletedActivity.subscribe {
                binding.listSettingsStopCompletedCheckBox.isChecked = it
            },
            viewModel.disableDroppedActivity.subscribe {
                binding.listSettingsStopDroppedCheckBox.isChecked = it
            },
            viewModel.disablePausedActivity.subscribe {
                binding.listSettingsStopPausedCheckBox.isChecked = it
            },
            viewModel.disableRepeatingActivity.subscribe {
                binding.listSettingsStopRepeatingCheckBox.isChecked = it
            }
        )

        viewModel.loadData(Unit)
    }

    private fun showAdvancedScoringDialog(currentText: String, index: Int? = null) {
        dialog.showTextInputDialog(currentText, textInputSetting) { newText ->
            if (newText.isNotBlank()) {
                if (index != null)
                    viewModel.editAdvancedScoring(newText, index)
                else
                    viewModel.addAdvancedScoring(newText)
            }
        }
    }

    private fun showCustomListsDialog(mediaType: MediaType, currentText: String, index: Int? = null) {
        dialog.showTextInputDialog(currentText, textInputSetting) { newText ->
            if (newText.isNotBlank()) {
                if (index != null)
                    viewModel.editCustomLists(mediaType, newText, index)
                else
                    viewModel.addCustomLists(mediaType, newText)
            }
        }
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