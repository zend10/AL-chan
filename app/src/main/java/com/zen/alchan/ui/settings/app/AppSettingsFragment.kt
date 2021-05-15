package com.zen.alchan.ui.settings.app

import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.helper.enums.AppTheme
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.common.BottomSheetListDialog
import com.zen.alchan.ui.common.TextRvAdapter
import kotlinx.android.synthetic.main.fragment_app_settings.*
import kotlinx.android.synthetic.main.toolbar_default.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppSettingsFragment : BaseFragment(R.layout.fragment_app_settings) {

    private val viewModel by viewModel<AppSettingsViewModel>()

    private var bottomSheetListDialog: BottomSheetListDialog? = null
    private var appThemeAdapter: AppThemeRvAdapter? = null
    private var allListPositionAdapter: AllListPositionRvAdapter? = null

    override fun setUpLayout() {
        setUpToolbar(defaultToolbar, getString(R.string.app_settings))

        appSettingsSelectedThemeLayout.clicks {
            viewModel.getAppThemeItems()
        }

        appSettingsCircularAvatarCheckBox.clicks {
            viewModel.updateUseCircularAvatarForProfile(appSettingsCircularAvatarCheckBox.isChecked)
        }

        appSettingsRecentReviewsCheckBox.clicks {
            viewModel.updateShowRecentReviewsAtHome(appSettingsRecentReviewsCheckBox.isChecked)
        }

        appSettingsAllAnimeLayout.clicks {
            viewModel.getAllAnimeListNumbers()
        }

        appSettingsAllMangaLayout.clicks {
            viewModel.getAllMangaListNumbers()
        }

        appSettingsRelativeDateCheckBox.clicks {
            viewModel.updateUseRelativeDateForNextAiringEpisode(appSettingsRelativeDateCheckBox.isChecked)
        }

        appSettingsCharacterNameLayout.clicks {

        }

        appSettingsStaffNameLayout.clicks {

        }

        appSettingsJapaneseMediaLayout.clicks {

        }

        appSettingsKoreanMediaLayout.clicks {

        }

        appSettingsChineseMediaLayout.clicks {

        }

        appSettingsTaiwaneseMediaLayout.clicks {

        }

        appSettingsSaveButton.clicks {
            viewModel.saveAppSettings()
            activity?.recreate()
        }

        appSettingsResetButton.clicks {
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
                appSettingsSelectedThemeText.text = it.name.convertFromSnakeCase()
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
                appThemeAdapter = AppThemeRvAdapter(requireContext(), it, object : AppThemeRvAdapter.AppThemeListener {
                    override fun getSelectedAppTheme(appTheme: AppTheme) {
                        dismissListDialog()
                        viewModel.updateAppTheme(appTheme)
                    }
                }).also { adapter ->
                    showListDialog(adapter)
                }
            }
        )

        disposables.add(
            viewModel.allAnimeListItems.subscribe {
                allListPositionAdapter = AllListPositionRvAdapter(requireContext(), it, object : AllListPositionRvAdapter.AllListPositionListener {
                    override fun getSelectedIndex(index: Int) {
                        dismissListDialog()
                        viewModel.updateAllAnimeListPosition(index)
                    }
                }).also { adapter ->
                    showListDialog(adapter)
                }
            }
        )

        disposables.add(
            viewModel.allMangaListItems.subscribe {
                allListPositionAdapter = AllListPositionRvAdapter(requireContext(), it, object : AllListPositionRvAdapter.AllListPositionListener {
                    override fun getSelectedIndex(index: Int) {
                        dismissListDialog()
                        viewModel.updateAllMangaListPosition(index)
                    }
                }).also { adapter ->
                    showListDialog(adapter)
                }
            }
        )

        viewModel.loadData()
    }

    private fun showListDialog(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        bottomSheetListDialog = BottomSheetListDialog.newInstance(adapter)
        bottomSheetListDialog?.show(childFragmentManager, null)
    }

    private fun dismissListDialog() {
        bottomSheetListDialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bottomSheetListDialog = null
        appThemeAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AppSettingsFragment()
    }
}