package com.zen.alchan.ui.settings.notifications


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zen.alchan.R
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.fragment_notifications_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class NotificationsSettingsFragment : Fragment() {

    private val viewModel by viewModel<NotificationsSettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: need more research

        initLayout()
    }

    private fun initLayout() {
        pushNotificationsInfoIcon.setOnClickListener {
            DialogUtility.showInfoDialog(
                activity,
                R.string.these_push_notifications_will_only_work_if_the_notifications_settings_in_anilist_settings_are_also_enabled
            )
        }
    }
}
