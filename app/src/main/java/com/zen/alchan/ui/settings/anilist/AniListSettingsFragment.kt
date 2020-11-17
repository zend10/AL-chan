package com.zen.alchan.ui.settings.anilist


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer

import com.zen.alchan.R
import com.zen.alchan.helper.doOnApplyWindowInsets
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.updateBottomPadding
import com.zen.alchan.helper.utils.DialogUtility
import kotlinx.android.synthetic.main.fragment_ani_list_settings.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.UserTitleLanguage

/**
 * A simple [Fragment] subclass.
 */
class AniListSettingsFragment : Fragment() {

    private val viewModel by viewModel<AniListSettingsViewModel>()

    private lateinit var itemSave: MenuItem
    private lateinit var adapter: TitleLanguageRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ani_list_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarLayout.apply {
            title = getString(R.string.anilist_settings)
            navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_left)
            setNavigationOnClickListener { activity?.onBackPressed() }

            inflateMenu(R.menu.menu_save)
            itemSave = menu.findItem(R.id.itemSave)
        }

        anilistSettingsLayout.doOnApplyWindowInsets { view, windowInsets, initialPadding ->
            view.updateBottomPadding(windowInsets, initialPadding)
        }

        setupObserver()
        initLayout()
    }

    private fun setupObserver() {
        viewModel.viewerData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                initLayout()
            }
        })

        viewModel.updateAniListSettingsResponse.observe(viewLifecycleOwner, Observer {
            when (it.responseStatus) {
                ResponseStatus.LOADING -> {
                    loadingLayout.visibility = View.VISIBLE
                }
                ResponseStatus.SUCCESS -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, R.string.settings_saved)
                }
                ResponseStatus.ERROR -> {
                    loadingLayout.visibility = View.GONE
                    DialogUtility.showToast(activity, it.message)
                }
            }
        })

        viewModel.initData()
    }

    private fun initLayout() {
        val options = viewModel.viewerData.value?.options

        if (options?.titleLanguage != null) {
            adapter = assignAdapter()
            titleLanguageRecyclerView.adapter = adapter
        }

        if (!viewModel.isInit) {
            adultContentCheckBox.isChecked = options?.displayAdultContent == true
            airingAnimeNotifCheckBox.isChecked = options?.airingNotifications == true
            viewModel.isInit = true
        }

        itemSave.setOnMenuItemClickListener {
            viewModel.updateAniListSettings(adultContentCheckBox.isChecked, airingAnimeNotifCheckBox.isChecked)
            true
        }
    }

    private fun assignAdapter(): TitleLanguageRvAdapter {
        return TitleLanguageRvAdapter(activity!!, viewModel.languageList, viewModel.selectedTitleLanguage!!,
            object : TitleLanguageRvAdapter.TitleLanguageListener {
                override fun passSelectedLanguage(language: UserTitleLanguage) {
                    viewModel.selectedTitleLanguage = language
                    adapter = assignAdapter()
                    titleLanguageRecyclerView.adapter = adapter
                }
            })
    }
}
