package com.zen.alchan.ui.settings.anilist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentAnilistSettingsBinding
import com.zen.alchan.helper.enums.getString
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.ui.base.BaseFragment
import io.reactivex.rxjava3.core.Observable
import org.koin.androidx.viewmodel.ext.android.viewModel


class AniListSettingsFragment : BaseFragment<FragmentAnilistSettingsBinding, AniListSettingsViewModel>() {

    override val viewModel: AniListSettingsViewModel by viewModel()

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAnilistSettingsBinding {
        return FragmentAnilistSettingsBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.anilist_settings))

            aniListSettingsSelectedLanguageLayout.clicks {
                viewModel.loadUserTitleLanguageItems()
            }

            aniListSettingsSelectedNamingLayout.clicks {
                viewModel.loadUserStaffNameLanguageItems()
            }

            aniListSettingsSelectedMergeTimeLayout.clicks {
                viewModel.loadActivityMergeTimeItems()
            }

            aniListSettingsShowAdultContentCheckBox.setOnClickListener {
                viewModel.updateDisplayAdultContent(aniListSettingsShowAdultContentCheckBox.isChecked)
            }

            aniListSettingsReceiveAiringNotificationsCheckBox.setOnClickListener {
                viewModel.updateAiringNotifications(aniListSettingsReceiveAiringNotificationsCheckBox.isChecked)
            }

            aniListSettingsSaveLayout.positiveButton.text = getString(R.string.save_changes)
            aniListSettingsSaveLayout.positiveButton.clicks {
                dialog.showConfirmationDialog(
                    R.string.save_changes,
                    R.string.the_app_will_be_restarted_to_apply_the_change,
                    R.string.save,
                    {
                        viewModel.saveAniListSettings()
                    },
                    R.string.cancel,
                    {}
                )
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.aniListSettingsLayout.applySidePaddingInsets()
        binding.aniListSettingsSaveLayout.oneButtonLayout.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.loading.subscribe {
                binding.loadingLayout.loadingLayout.show(it)
            },
            viewModel.success.subscribe {
                dialog.showToast(it)
                restartApp(DeepLink.generateAniListSettings(), false)
            },
            viewModel.error.subscribe {
                dialog.showToast(it)
            },
            viewModel.titleLanguage.subscribe {
                binding.aniListSettingsSelectedLanguageText.text = it.name.convertFromSnakeCase()
            },
            viewModel.staffNameLanguage.subscribe {
                binding.aniListSettingsSelectedNamingText.text = it.name.convertFromSnakeCase()
            },
            viewModel.activityMergeTime.subscribe {
                binding.aniListSettingsSelectedMergeTimeText.text = it.getString(requireContext())
            },
            viewModel.displayAdultContent.subscribe {
                binding.aniListSettingsShowAdultContentCheckBox.isChecked = it
            },
            viewModel.airingNotifications.subscribe {
                binding.aniListSettingsReceiveAiringNotificationsCheckBox.isChecked = it
            },
            viewModel.userTitleLanguageItems.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateTitleLanguage(data)
                }
            },
            viewModel.userStaffNameLanguageItems.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateStaffNameLanguage(data)
                }
            },
            viewModel.activityMergeTimeItems.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateActivityMergeTime(data.minute)
                }
            }
        )

        viewModel.loadData(Unit)
    }

    companion object {
        @JvmStatic
        fun newInstance() = AniListSettingsFragment()
    }
}