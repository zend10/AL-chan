package com.zen.alchan.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.BuildConfig
import com.zen.alchan.R
import com.zen.alchan.helper.Constant
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.helper.utils.Utility
import com.zen.alchan.ui.main.MainActivity
import com.zen.alchan.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : BaseActivity() {

    private val viewModel by viewModel<SplashViewModel>()

    private val isTrue = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (viewModel.appSettings.showSocialTabAutomatically == null) {
            viewModel.setDefaultAppSetting(AndroidUtility.isLowOnMemory(this))
        }

        setupObserver()
    }

    private fun setupObserver() {
        viewModel.announcementResponse.observe(this, Observer {
            if (it.responseStatus == ResponseStatus.SUCCESS) {
                try {
                    if (it.data == null || it.data.id.isBlank()) {
                        moveToNextPage()
                        return@Observer
                    }

                    // Handle update announcement
                    // Show update dialog if app_version has a value
                    if (it.data.app_version.isNotBlank() && BuildConfig.VERSION_CODE < it.data.app_version.toInt()) {
                        MaterialAlertDialogBuilder(this).apply {
                            setTitle(R.string.new_update_is_available)
                            setMessage(it.data.message)
                            setCancelable(false)
                            setPositiveButton(R.string.go_to_play_store) { _, _ ->
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(Constant.PLAY_STORE_URL)))
                                finish()
                            }
                            // show later if required_update is not "1"
                            if (it.data.required_update != isTrue) {
                                setNegativeButton(R.string.later) { _, _ ->
                                    moveToNextPage()
                                }
                            }
                            show()
                        }
                        return@Observer
                    }

                    // Handle message announcement
                    // Show announcement if last saved announcement id is not the same as new id and if dates exist
                    if (viewModel.lastAnnouncementId == it.data.id.toInt()) {
                        moveToNextPage()
                        return@Observer
                    }

                    if (it.data.from_date.isNotBlank() &&
                        it.data.until_date.isNotBlank() &&
                        Utility.isBetweenTwoDates(it.data.from_date, it.data.until_date)
                    ) {
                        MaterialAlertDialogBuilder(this).apply {
                            setMessage(it.data.message)
                            setCancelable(false)
                            setPositiveButton(R.string.ok) { _, _ ->
                                moveToNextPage()
                            }
                            setNegativeButton(R.string.dont_show_again) { _, _ ->
                                viewModel.setNeverShowAgain(it.data.id.toInt())
                                moveToNextPage()
                            }
                            show()
                        }
                        return@Observer
                    }

                    moveToNextPage()
                } catch (e: Exception) {
                    moveToNextPage()
                }
            } else {
                moveToNextPage()
            }
        })

        viewModel.getAnnouncement()
    }

    private fun moveToNextPage() {
        if (viewModel.isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}
