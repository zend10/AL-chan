package com.zen.alchan.ui.settings.anilist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.helper.enums.getString
import com.zen.alchan.helper.extensions.applyTopPaddingInsets
import com.zen.alchan.helper.extensions.clicks
import com.zen.alchan.helper.extensions.convertFromSnakeCase
import com.zen.alchan.helper.extensions.show
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.common.BottomSheetListDialog
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_ani_list_settings.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.toolbar_default.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.UserTitleLanguage


class AniListSettingsFragment : BaseFragment(R.layout.fragment_ani_list_settings) {

    private val viewModel by viewModel<AniListSettingsViewModel>()

    private var mediaTitleLanguageAdapter: MediaTitleLanguageRvAdapter? = null
    private var activityMergeTimeAdapter: ActivityMergeTimeRvAdapter? = null

    override fun setUpLayout() {
        setUpToolbar(defaultToolbar, getString(R.string.anilist_settings))

        aniListSettingsSelectedLanguageLayout.clicks {
            viewModel.getMediaTitleLanguages()
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

    override fun setUpInsets() {
        defaultToolbar.applyTopPaddingInsets()
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
            viewModel.mediaTitleLanguage.subscribe {
                aniListSettingsSelectedLanguageText.text = it.name.convertFromSnakeCase()
            }
        )

        disposables.add(
            viewModel.progressActivityMergeTime.subscribe {
                aniListSettingsSelectedMergeTimeText.text = it.getString(requireContext())
            }
        )

        disposables.add(
            viewModel.showAdultContent.subscribe {
                aniListSettingsShowAdultContentCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.receiveAiringNotifications.subscribe {
                aniListSettingsReceiveAiringNotificationsCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.mediaTitleLanguages.subscribe {
                mediaTitleLanguageAdapter = MediaTitleLanguageRvAdapter(requireContext(), it, object : MediaTitleLanguageRvAdapter.MediaTitleLanguageListener {
                    override fun getSelectedLanguage(userTitleLanguage: UserTitleLanguage) {
                        dismissListDialog()
                        viewModel.updateMediaTitleLanguage(userTitleLanguage)
                    }
                }).also { adapter ->
                    showListDialog(adapter)
                }
            }
        )

        disposables.add(
            viewModel.activityMergeTimes.subscribe {
                activityMergeTimeAdapter = ActivityMergeTimeRvAdapter(requireContext(), it, object : ActivityMergeTimeRvAdapter.ActivityMergeTimeListener {
                    override fun passSelectedMinute(minute: Int) {
                        dismissListDialog()
                        viewModel.updateProgressActivityMergeTime(minute)
                    }
                }).also { adapter ->
                    showListDialog(adapter)
                }
            }
        )

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