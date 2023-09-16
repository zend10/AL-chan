package com.zen.alchan.ui.settings.app

import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zen.alchan.R
import com.zen.alchan.databinding.FragmentAppSettingsBinding
import com.zen.alchan.helper.enums.*
import com.zen.alchan.helper.extensions.*
import com.zen.alchan.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.zen.alchan.helper.enums.MediaType
import com.zen.alchan.helper.utils.DeepLink

class AppSettingsFragment : BaseFragment<FragmentAppSettingsBinding, AppSettingsViewModel>() {

    override val viewModel: AppSettingsViewModel by viewModel()

    private var appThemeAdapter: AppThemeRvAdapter? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAppSettingsBinding {
        return FragmentAppSettingsBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.apply {
            setUpToolbar(defaultToolbar.defaultToolbar, getString(R.string.app_settings))

            appSettingsSelectedThemeLayout.clicks {
                viewModel.loadAppThemeItems()
            }

            appSettingsCircularAvatarCheckBox.setOnClickListener {
                viewModel.updateUseCircularAvatarForProfile(appSettingsCircularAvatarCheckBox.isChecked)
            }

            appSettingsAllAnimeLayout.clicks {
                viewModel.loadAllListPositionItems(MediaType.ANIME)
            }

            appSettingsAllMangaLayout.clicks {
                viewModel.loadAllListPositionItems(MediaType.MANGA)
            }

            appSettingsRelativeDateCheckBox.setOnClickListener {
                viewModel.updateUseRelativeDateForNextAiringEpisode(appSettingsRelativeDateCheckBox.isChecked)
            }

            appSettingsJapaneseMediaLayout.clicks {
                viewModel.loadMediaNamingItems(Country.JAPAN)
            }

            appSettingsKoreanMediaLayout.clicks {
                viewModel.loadMediaNamingItems(Country.SOUTH_KOREA)
            }

            appSettingsChineseMediaLayout.clicks {
                viewModel.loadMediaNamingItems(Country.CHINA)
            }

            appSettingsTaiwaneseMediaLayout.clicks {
                viewModel.loadMediaNamingItems(Country.TAIWAN)
            }

            appSettingsAiringPushNotificationsCheckBox.setOnClickListener {
                viewModel.updateSendAiringPushNotifications(appSettingsAiringPushNotificationsCheckBox.isChecked)
            }

            appSettingsActivityPushNotificationsCheckBox.setOnClickListener {
                viewModel.updateSendActivityPushNotifications(appSettingsActivityPushNotificationsCheckBox.isChecked)
            }

            appSettingsForumPushNotificationsCheckBox.setOnClickListener {
                viewModel.updateSendForumPushNotifications(appSettingsForumPushNotificationsCheckBox.isChecked)
            }

            appSettingsFollowsPushNotificationsCheckBox.setOnClickListener {
                viewModel.updateSendFollowsPushNotifications(appSettingsFollowsPushNotificationsCheckBox.isChecked)
            }

            appSettingsRelationsPushNotificationsCheckBox.setOnClickListener {
                viewModel.updateSendRelationsPushNotifications(appSettingsRelationsPushNotificationsCheckBox.isChecked)
            }

            appSettingsMergePushNotificationsCheckBox.setOnClickListener {
                viewModel.updateMergePushNotifications(appSettingsMergePushNotificationsCheckBox.isChecked)
            }

            appSettingsHighestQualityImageCheckBox.setOnClickListener {
                viewModel.updateUseHighestQualityImage(appSettingsHighestQualityImageCheckBox.isChecked)
            }

            appSettingsSaveLayout.positiveButton.text = getString(R.string.save_changes)
            appSettingsSaveLayout.positiveButton.clicks {
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

            appSettingsSaveLayout.negativeButton.text = getString(R.string.reset_to_default)
            appSettingsSaveLayout.negativeButton.clicks {
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
    }

    override fun setUpInsets() {
        binding.defaultToolbar.defaultToolbar.applyTopPaddingInsets()
        binding.appSettingsLayout.applySidePaddingInsets()
        binding.appSettingsSaveLayout.twoButtonsLayout.applyBottomSidePaddingInsets()
    }

    override fun setUpObserver() {
        disposables.addAll(
            viewModel.success.subscribe {
                dialog.showToast(it)
                restartApp(DeepLink.generateAppSettings(), false)
            },
            viewModel.appTheme.subscribe {
                binding.appSettingsSelectedThemeText.text = it.getString()
            },
            viewModel.useCircularAvatarForProfile.subscribe {
                binding.appSettingsCircularAvatarCheckBox.isChecked = it
            },
            viewModel.isAllAnimeListPositionAtTop.subscribe {
                binding.appSettingsAllAnimeText.text = if (it) {
                    getString(R.string.top_of_the_list)
                } else {
                    getString(R.string.bottom_of_the_list)
                }
            },
            viewModel.isAllMangaListPositionAtTop.subscribe {
                binding.appSettingsAllMangaText.text = if (it) {
                    getString(R.string.top_of_the_list)
                } else {
                    getString(R.string.bottom_of_the_list)
                }
            },
            viewModel.useRelativeDateForNextAiringEpisode.subscribe {
                binding.appSettingsRelativeDateCheckBox.isChecked = it
            },
            viewModel.japaneseMediaNaming.subscribe {
                binding.appSettingsJapaneseMediaText.text = it.getString()
            },
            viewModel.koreanMediaNaming.subscribe {
                binding.appSettingsKoreanMediaText.text = it.getString()
            },
            viewModel.chineseMediaNaming.subscribe {
                binding.appSettingsChineseMediaText.text = it.getString()
            },
            viewModel.taiwaneseMediaNaming.subscribe {
                binding.appSettingsTaiwaneseMediaText.text = it.getString()
            },
            viewModel.sendAiringPushNotifications.subscribe {
                binding.appSettingsAiringPushNotificationsCheckBox.isChecked = it
            },
            viewModel.sendActivityPushNotifications.subscribe {
                binding.appSettingsActivityPushNotificationsCheckBox.isChecked = it
            },
            viewModel.sendForumPushNotifications.subscribe {
                binding.appSettingsForumPushNotificationsCheckBox.isChecked = it
            },
            viewModel.sendFollowsPushNotifications.subscribe {
                binding.appSettingsFollowsPushNotificationsCheckBox.isChecked = it
            },
            viewModel.sendRelationsPushNotifications.subscribe {
                binding.appSettingsRelationsPushNotificationsCheckBox.isChecked = it
            },
            viewModel.mergePushNotifications.subscribe {
                binding.appSettingsMergePushNotificationsCheckBox.isChecked = it
            },
            viewModel.useHighestQualityImage.subscribe {
                binding.appSettingsHighestQualityImageCheckBox.isChecked = it
            },
            viewModel.appThemeItems.subscribe {
                appThemeAdapter = AppThemeRvAdapter(requireContext(), it, object : AppThemeRvAdapter.AppThemeListener {
                    override fun getSelectedAppTheme(appTheme: AppTheme) {
                        dialog.dismissListDialog()
                        viewModel.updateAppTheme(appTheme)
                    }
                }).also { adapter ->
                    dialog.showListDialog(adapter)
                }
            },
            viewModel.allAnimeListPositionItems.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateIsAllAnimeListPositionAtTop(data)
                }
            },
            viewModel.allMangaListPositionItems.subscribe {
                dialog.showListDialog(it) { data, _ ->
                    viewModel.updateIsAllMangaListPositionAtTop(data)
                }
            },
            viewModel.mediaNamingItems.subscribe { (list, country) ->
                dialog.showListDialog(list) { data, _ ->
                    viewModel.updateMediaNaming(data, country)
                }
            }
        )

        viewModel.loadData(Unit)
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
        binding.appSettingsPushNotificationsInfoText.movementMethod = LinkMovementMethod.getInstance()
        binding.appSettingsPushNotificationsInfoText.text = pushNotificationsInfoText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appThemeAdapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = AppSettingsFragment()
    }
}