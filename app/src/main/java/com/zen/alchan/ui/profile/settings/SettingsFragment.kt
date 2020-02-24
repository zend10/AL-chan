package com.zen.alchan.ui.profile.settings


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.zen.alchan.R
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.ui.base.BaseFragment
import com.zen.alchan.ui.profile.settings.about.AboutFragment
import com.zen.alchan.ui.profile.settings.account.AccountSettingsFragment
import com.zen.alchan.ui.profile.settings.anilist.AniListSettingsFragment
import com.zen.alchan.ui.profile.settings.app.AppSettingsFragment
import com.zen.alchan.ui.profile.settings.list.ListSettingsFragment
import com.zen.alchan.ui.profile.settings.notifications.NotificationsSettingsFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.layout_toolbar.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarLayout.title = getString(R.string.settings)
        toolbarLayout.setNavigationOnClickListener { activity?.onBackPressed() }
        toolbarLayout.navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_delete)

        appSettingsMenu.setOnClickListener { listener?.changeFragment(AppSettingsFragment()) }
        anilistSettingsMenu.setOnClickListener { listener?.changeFragment(AniListSettingsFragment()) }
        listSettingsMenu.setOnClickListener { listener?.changeFragment(ListSettingsFragment()) }
        notificationsSettingsMenu.setOnClickListener { listener?.changeFragment(NotificationsSettingsFragment()) }
        accountSettingsMenu.setOnClickListener { listener?.changeFragment(AccountSettingsFragment()) }
        aboutMenu.setOnClickListener { listener?.changeFragment(AboutFragment()) }
    }
}
