package com.zen.alchan.ui.settings.app

import android.content.Intent
import android.net.Uri
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.common.BottomSheetListDialog
import kotlinx.android.synthetic.main.fragment_app_settings.*
import kotlinx.android.synthetic.main.toolbar_default.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppSettingsFragment : BaseFragment(R.layout.fragment_app_settings) {

    private val viewModel by viewModel<AppSettingsViewModel>()

    private var appThemeListDialog: BottomSheetListDialog? = null
    private var appThemeAdapter: AppThemeAdapter? = null

    override fun setUpLayout() {
        setUpToolbar(defaultToolbar, getString(R.string.app_settings))

        appSettingsThemeText.clicks {
            appThemeAdapter?.let { adapter ->
                appThemeListDialog = BottomSheetListDialog.newInstance(adapter)
                appThemeListDialog?.show(childFragmentManager, null)
            }
        }

        appSettingsCircularAvatarCheckBox.setOnClickListener {
            viewModel.updateUseCircularAvatarForProfile(appSettingsCircularAvatarCheckBox.isChecked)
        }

        appSettingsRecentReviewsCheckBox.setOnClickListener {
            viewModel.updateShowRecentReviewsAtHome(appSettingsRecentReviewsCheckBox.isChecked)
        }

        appSettingsAllAnimeText.setOnClickListener {

        }

        appSettingsAllMangaText.setOnClickListener {

        }

        appSettingsRelativeDateCheckBox.setOnClickListener {
            viewModel.updateUseRelativeDateForNextAiringEpisode(appSettingsRelativeDateCheckBox.isChecked)
        }

        appSettingsCharacterNameText.setOnClickListener {

        }

        appSettingsStaffNameText.setOnClickListener {

        }

        appSettingsJapaneseMediaText.setOnClickListener {

        }

        appSettingsKoreanMediaText.setOnClickListener {

        }

        appSettingsChineseMediaText.setOnClickListener {

        }

        appSettingsTaiwaneseMediaText.setOnClickListener {

        }

        appSettingsSaveButton.setOnClickListener {
            viewModel.saveAppSettings()
            activity?.recreate()
        }

        appSettingsResetButton.setOnClickListener {
            viewModel.resetAppSettings()
            activity?.recreate()
        }
    }

    override fun setUpInsets() {
        super.setUpInsets()
        defaultToolbar.applyTopPaddingInsets()
        appSettingsLayout.applyBottomPaddingInsets()
    }

    override fun setUpObserver() {
        disposables.add(
            viewModel.appTheme.subscribe {
                appSettingsThemeText.text = it.name.convertFromSnakeCase()
            }
        )

        disposables.add(
            viewModel.useCircularAvatarForProfile.subscribe {
                appSettingsCircularAvatarCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.showRecentReviewsAtHome.subscribe {
                appSettingsRecentReviewsCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.allAnimeListPosition.subscribe {
                appSettingsAllAnimeText.text = it.toString()
            }
        )

        disposables.add(
            viewModel.allMangaListPosition.subscribe {
                appSettingsAllMangaText.text = it.toString()
            }
        )

        disposables.add(
            viewModel.useRelativeDateForNextAiringEpisode.subscribe {
                appSettingsRelativeDateCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.characterNaming.subscribe {
                appSettingsCharacterNameText.text = it.name
            }
        )

        disposables.add(
            viewModel.staffNaming.subscribe {
                appSettingsStaffNameText.text = it.name
            }
        )

        disposables.add(
            viewModel.japaneseMediaNaming.subscribe {
                appSettingsJapaneseMediaText.text = it.name
            }
        )

        disposables.add(
            viewModel.koreanMediaNaming.subscribe {
                appSettingsKoreanMediaText.text = it.name
            }
        )

        disposables.add(
            viewModel.chineseMediaNaming.subscribe {
                appSettingsChineseMediaText.text = it.name
            }
        )

        disposables.add(
            viewModel.taiwaneseMediaNaming.subscribe {
                appSettingsTaiwaneseMediaText.text = it.name
            }
        )

        disposables.add(
            viewModel.appThemeItems.subscribe {
                appThemeAdapter = AppThemeAdapter(requireContext(), it, object : AppThemeAdapter.AppThemeListener {
                    override fun getSelectedAppTheme(appTheme: AppTheme) {
                        appThemeListDialog?.dismiss()
                        viewModel.updateAppTheme(appTheme)
                    }
                })
            }
        )

        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appThemeListDialog = null
        appThemeAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AppSettingsFragment()
    }
}