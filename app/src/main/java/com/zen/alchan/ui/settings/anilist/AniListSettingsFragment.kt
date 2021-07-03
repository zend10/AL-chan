package com.zen.alchan.ui.settings.anilist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentAnilistSettingsBinding
import com.zen.alchan.helper.enums.getString
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.ui.base.BaseFragment
import io.reactivex.Observable
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.UserStaffNameLanguage
import type.UserTitleLanguage


class AniListSettingsFragment : BaseFragment<FragmentAnilistSettingsBinding, AniListSettingsViewModel>() {

    override val viewModel: AniListSettingsViewModel by viewModel()

    private var mediaTitleLanguageAdapter: MediaTitleLanguageRvAdapter? = null
    private var staffCharacterNamingAdapter: StaffCharacterNamingRvAdapter? = null
    private var activityMergeTimeAdapter: ActivityMergeTimeRvAdapter? = null

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
                viewModel.getMediaTitleLanguages()
            }

            aniListSettingsSelectedNamingLayout.clicks {
                viewModel.getStaffCharacterNamings()
            }

            aniListSettingsSelectedMergeTimeLayout.clicks {
                viewModel.getActivityMergeTimes()
            }

            aniListSettingsShowAdultContentCheckBox.clicks {
                viewModel.updateShowAdultContent(aniListSettingsShowAdultContentCheckBox.isChecked)
            }

            aniListSettingsReceiveAiringNotificationsCheckBox.clicks {
                viewModel.updateReceiveAiringNotifications(aniListSettingsReceiveAiringNotificationsCheckBox.isChecked)
            }

            aniListSettingsSaveButton.clicks {
                viewModel.saveAniListSettings()
            }
        }
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.aniListSettingsLayout.applyBottomPaddingInsets()
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
            viewModel.mediaTitleLanguage.subscribe {
                binding.aniListSettingsSelectedLanguageText.text = it.name.convertFromSnakeCase()
            }
        )

        disposables.add(
            viewModel.staffCharacterNaming.subscribe {
                binding.aniListSettingsSelectedNamingText.text = it.name.convertFromSnakeCase()
            }
        )

        disposables.add(
            viewModel.progressActivityMergeTime.subscribe {
                binding.aniListSettingsSelectedMergeTimeText.text = it.getString(requireContext())
            }
        )

        disposables.add(
            viewModel.showAdultContent.subscribe {
                binding.aniListSettingsShowAdultContentCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.receiveAiringNotifications.subscribe {
                binding.aniListSettingsReceiveAiringNotificationsCheckBox.isChecked = it
            }
        )

//        disposables.add(
//            viewModel.mediaTitleLanguages.subscribe {
//                mediaTitleLanguageAdapter = MediaTitleLanguageRvAdapter(requireContext(), it, object : MediaTitleLanguageRvAdapter.MediaTitleLanguageListener {
//                    override fun getSelectedLanguage(userTitleLanguage: UserTitleLanguage) {
//                        dismissListDialog()
//                        viewModel.updateMediaTitleLanguage(userTitleLanguage)
//                    }
//                }).also { adapter ->
//                    showListDialog(adapter)
//                }
//            }
//        )
//
//        disposables.add(
//            viewModel.staffCharacterNameLanguages.subscribe {
//                staffCharacterNamingAdapter = StaffCharacterNamingRvAdapter(requireContext(), it, object : StaffCharacterNamingRvAdapter.StaffCharacterNamingListener {
//                    override fun getSelectedNaming(userStaffNameLanguage: UserStaffNameLanguage) {
//                        dismissListDialog()
//                        viewModel.updateStaffCharacterNaming(userStaffNameLanguage)
//                    }
//                }).also { adapter ->
//                    showListDialog(adapter)
//                }
//            }
//        )
//
//        disposables.add(
//            viewModel.activityMergeTimes.subscribe {
//                activityMergeTimeAdapter = ActivityMergeTimeRvAdapter(requireContext(), it, object : ActivityMergeTimeRvAdapter.ActivityMergeTimeListener {
//                    override fun passSelectedMinute(minute: Int) {
//                        dismissListDialog()
//                        viewModel.updateProgressActivityMergeTime(minute)
//                    }
//                }).also { adapter ->
//                    showListDialog(adapter)
//                }
//            }
//        )

        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaTitleLanguageAdapter = null
        activityMergeTimeAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AniListSettingsFragment()
    }
}