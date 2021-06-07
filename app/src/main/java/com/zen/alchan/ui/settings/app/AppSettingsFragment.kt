package com.zen.alchan.ui.settings.app

import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.zen.alchan.R
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_app_settings.*
import kotlinx.android.synthetic.main.toolbar_default.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppSettingsFragment : BaseFragment(R.layout.fragment_app_settings) {

    private val viewModel by viewModel<AppSettingsViewModel>()

    private var appThemeAdapter: AppThemeRvAdapter? = null
    private var allListPositionAdapter: AllListPositionRvAdapter? = null
    private var namingAdapter: NamingRvAdapter? = null
    private var pushNotificationsIntervalAdapter: PushNotificationsIntervalRvAdapter? = null

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
            viewModel.getCharacterNamings()
        }

        appSettingsStaffNameLayout.clicks {
            viewModel.getStaffNamings()
        }

        appSettingsJapaneseMediaLayout.clicks {
            viewModel.getMediaNamings(Country.JAPAN)
        }

        appSettingsKoreanMediaLayout.clicks {
            viewModel.getMediaNamings(Country.SOUTH_KOREA)
        }

        appSettingsChineseMediaLayout.clicks {
            viewModel.getMediaNamings(Country.CHINA)
        }

        appSettingsTaiwaneseMediaLayout.clicks {
            viewModel.getMediaNamings(Country.TAIWAN)
        }

        appSettingsAiringPushNotificationsCheckBox.clicks {
            viewModel.updateSendAiringPushNotifications(appSettingsAiringPushNotificationsCheckBox.isChecked)
        }

        appSettingsActivityPushNotificationsCheckBox.clicks {
            viewModel.updateSendActivityPushNotifications(appSettingsActivityPushNotificationsCheckBox.isChecked)
        }

        appSettingsForumPushNotificationsCheckBox.clicks {
            viewModel.updateSendForumPushNotifications(appSettingsForumPushNotificationsCheckBox.isChecked)
        }

        appSettingsFollowsPushNotificationsCheckBox.clicks {
            viewModel.updateSendFollowsPushNotifications(appSettingsFollowsPushNotificationsCheckBox.isChecked)
        }

        appSettingsRelationsPushNotificationsCheckBox.clicks {
            viewModel.updateSendRelationsPushNotifications(appSettingsRelationsPushNotificationsCheckBox.isChecked)
        }

        appSettingsMergePushNotificationsCheckBox.clicks {
            viewModel.updateMergePushNotifications(appSettingsMergePushNotificationsCheckBox.isChecked)
        }

        appSettingsShowPushNotificationsEveryHourLayout.clicks {
            viewModel.getPushNotificationsIntervals()
        }

        appSettingsHighestQualityImageCheckBox.clicks {
            viewModel.updateUseHighestQualityImage(appSettingsHighestQualityImageCheckBox.isChecked)
        }

        appSettingsSocialFeatureCheckBox.clicks {
            viewModel.updateEnableSocialFeature(appSettingsSocialFeatureCheckBox.isChecked)
        }

        appSettingsShowBioAutomaticallyCheckBox.clicks {
            viewModel.updateShowBioAutomatically(appSettingsShowBioAutomaticallyCheckBox.isChecked)
        }

        appSettingsShowStatsChartAutomaticallyCheckBox.clicks {
            viewModel.updateShowStatsChartAutomatically(appSettingsShowStatsChartAutomaticallyCheckBox.isChecked)
        }

        appSettingsSaveButton.clicks {
            dialog.showConfirmationDialog(
                R.string.save_changes,
                R.string.the_app_will_be_restarted_to_apply_the_change,
                R.string.save,
                {
                    viewModel.saveAppSettings()
                },
                R.string.cancel,
                {}
            )
        }

        appSettingsResetButton.clicks {
            dialog.showConfirmationDialog(
                R.string.reset_to_default,
                R.string.the_app_will_be_restarted_to_apply_the_change,
                R.string.reset,
                {
                    viewModel.resetAppSettings()
                },
                R.string.cancel,
                {}
            )
        }

        setPushNotificationsInfoTextLink()
    }

    override fun setUpInsets() {
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
                appSettingsCharacterNameText.text = it.name.convertFromSnakeCase()
            }
        )

        disposables.add(
            viewModel.staffNaming.subscribe {
                appSettingsStaffNameText.text = it.name.convertFromSnakeCase()
            }
        )

        disposables.add(
            viewModel.japaneseMediaNaming.subscribe {
                appSettingsJapaneseMediaText.text = it.name.convertFromSnakeCase()
            }
        )

        disposables.add(
            viewModel.koreanMediaNaming.subscribe {
                appSettingsKoreanMediaText.text = it.name.convertFromSnakeCase()
            }
        )

        disposables.add(
            viewModel.chineseMediaNaming.subscribe {
                appSettingsChineseMediaText.text = it.name.convertFromSnakeCase()
            }
        )

        disposables.add(
            viewModel.taiwaneseMediaNaming.subscribe {
                appSettingsTaiwaneseMediaText.text = it.name.convertFromSnakeCase()
            }
        )

        disposables.add(
            viewModel.sendAiringPushNotifications.subscribe {
                appSettingsAiringPushNotificationsCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.sendActivityPushNotifications.subscribe {
                appSettingsActivityPushNotificationsCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.sendForumPushNotifications.subscribe {
                appSettingsForumPushNotificationsCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.sendFollowsPushNotifications.subscribe {
                appSettingsFollowsPushNotificationsCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.sendRelationsPushNotifications.subscribe {
                appSettingsRelationsPushNotificationsCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.mergePushNotifications.subscribe {
                appSettingsMergePushNotificationsCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.showPushNotificationsInterval.subscribe {
                appSettingsShowPushNotificationsEveryHourText.text = it.showUnit(requireContext(), R.plurals.hour)
            }
        )

        disposables.add(
            viewModel.useHighestQualityImage.subscribe {
                appSettingsHighestQualityImageCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.enableSocialFeature.subscribe {
                appSettingsSocialFeatureCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.showBioAutomatically.subscribe {
                appSettingsShowBioAutomaticallyCheckBox.isChecked = it
            }
        )

        disposables.add(
            viewModel.showStatsChartAutomatically.subscribe {
                appSettingsShowStatsChartAutomaticallyCheckBox.isChecked = it
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

        disposables.add(
            viewModel.characterNamings.subscribe {
                namingAdapter = NamingRvAdapter(requireContext(), it, object : NamingRvAdapter.NamingListener {
                    override fun getSelectedNaming(naming: Naming) {
                        if (naming is CharacterNaming) {
                            dismissListDialog()
                            viewModel.updateCharacterNaming(naming)
                        }
                    }
                }).also { adapter ->
                    showListDialog(adapter)
                }
            }
        )

        disposables.add(
            viewModel.staffNamings.subscribe {
                namingAdapter = NamingRvAdapter(requireContext(), it, object : NamingRvAdapter.NamingListener {
                    override fun getSelectedNaming(naming: Naming) {
                        if (naming is StaffNaming) {
                            dismissListDialog()
                            viewModel.updateStaffNaming(naming)
                        }
                    }
                }).also { adapter ->
                    showListDialog(adapter)
                }
            }
        )

        disposables.add(
            viewModel.mediaNamings.subscribe { (list, country) ->
                namingAdapter = NamingRvAdapter(requireContext(), list, object : NamingRvAdapter.NamingListener {
                    override fun getSelectedNaming(naming: Naming) {
                        if (naming is MediaNaming) {
                            dismissListDialog()
                            viewModel.updateMediaNaming(naming, country)
                        }
                    }
                }).also { adapter ->
                    showListDialog(adapter)
                }
            }
        )

        disposables.add(
            viewModel.pushNotificationsIntervals.subscribe {
                pushNotificationsIntervalAdapter = PushNotificationsIntervalRvAdapter(requireContext(), it, object : PushNotificationsIntervalRvAdapter.PushNotificationsIntervalListener {
                    override fun getSelectedInterval(interval: Int) {
                        dismissListDialog()
                        viewModel.updateShowPushNotificationsInterval(interval)
                    }
                }).also { adapter ->
                    showListDialog(adapter)
                }
            }
        )

        disposables.add(
            viewModel.appSettingsSaved.subscribe {
                restartApp()
            }
        )

        viewModel.loadData()
    }

    private fun setPushNotificationsInfoTextLink() {
        val dontKillMyAppText = "https://dontkillmyapp.com/"
        val pushNotificationsInfoText = SpannableString(getString(R.string.important_to_know_n1_push_notifications_will_show_up_periodically_not_real_time_n2_depending_on_your_rom_and_phone_setting_it_might_not_show_up_at_all_reference_https_dontkillmyapp_com))
        val startIndex = pushNotificationsInfoText.indexOf(dontKillMyAppText)
        val endIndex = startIndex + dontKillMyAppText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                navigation.openWebView(dontKillMyAppText)
            }
        }

        pushNotificationsInfoText.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        appSettingsPushNotificationsInfoText.movementMethod = LinkMovementMethod.getInstance()
        appSettingsPushNotificationsInfoText.text = pushNotificationsInfoText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appThemeAdapter = null
        namingAdapter = null
        pushNotificationsIntervalAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AppSettingsFragment()
    }
}