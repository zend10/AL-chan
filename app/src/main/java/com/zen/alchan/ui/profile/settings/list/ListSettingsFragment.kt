package com.zen.alchan.ui.profile.settings.list


import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.zen.alchan.R
import com.zen.alchan.helper.enums.ResponseStatus
import com.zen.alchan.helper.utils.AndroidUtility
import com.zen.alchan.helper.utils.DialogUtility
import com.zen.alchan.helper.utils.Utility
import kotlinx.android.synthetic.main.dialog_input.*
import kotlinx.android.synthetic.main.dialog_input.view.*
import kotlinx.android.synthetic.main.fragment_list_settings.*
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.ScoreFormat

/**
 * A simple [Fragment] subclass.
 */
class ListSettingsFragment : Fragment() {

    private val viewModel by viewModel<ListSettingsViewModel>()

    private lateinit var itemSave: MenuItem
    private lateinit var advancedScoringAdapter: ListSettingsRvAdapter
    private lateinit var customAnimeListsAdapter: ListSettingsRvAdapter
    private lateinit var customMangaListsAdapter: ListSettingsRvAdapter

    companion object {
        private const val LIST_ADVANCED = 1
        private const val LIST_CUSTOM_ANIME = 2
        private const val LIST_CUSTOM_MANGA = 3

        private const val CHARACTER_LIMIT = 30
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbarLayout.apply {
            title = getString(R.string.list_settings)
            navigationIcon = ContextCompat.getDrawable(activity!!, R.drawable.ic_left)
            setNavigationOnClickListener { activity?.onBackPressed() }

            inflateMenu(R.menu.menu_save)
            itemSave = menu.findItem(R.id.itemSave)
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

        viewModel.updateListSettingsResponse.observe(viewLifecycleOwner, Observer {
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
        val mediaListOptions = viewModel.viewerData.value?.mediaListOptions

        if (!viewModel.isInit) {
            advancedScoringCheckBox.isChecked = mediaListOptions?.animeList?.advancedScoringEnabled == true
            splitAnimeCheckBox.isChecked = mediaListOptions?.animeList?.splitCompletedSectionByFormat == true
            splitMangaCheckBox.isChecked = mediaListOptions?.mangaList?.splitCompletedSectionByFormat == true
            viewModel.isInit = true
        }

        // Scoring
        checkAdvancedScoringLayout()

        scoringSystemText.text = Utility.getScoreFormatString(viewModel.selectedScoringSystem)

        scoringSystemText.setOnClickListener {
            MaterialAlertDialogBuilder(activity)
                .setItems(viewModel.scoringFormatStringArray) { _, which ->
                    viewModel.selectedScoringSystem = viewModel.scoringFormatList[which]
                    scoringSystemText.text = viewModel.scoringFormatStringArray[which]
                    checkAdvancedScoringLayout()
                }
                .show()
        }

        advancedScoringCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            checkAdvancedScoringLayout()
        }

        advancedScoringAdapter = assignAdapter(viewModel.advancedScoringLists, LIST_ADVANCED)
        advancedScoringRecyclerView.adapter = advancedScoringAdapter

        addMoreAdvancedScoringText.setOnClickListener {
            val inputDialogView = layoutInflater.inflate(R.layout.dialog_input, inputDialogLayout, false)
            inputDialogView.inputField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            inputDialogView.inputField.filters = arrayOf(InputFilter.LengthFilter(CHARACTER_LIMIT))

            DialogUtility.showCustomViewDialog(
                activity,
                R.string.add_new_criteria,
                inputDialogView,
                R.string.add,
                {
                    val newEntry = inputDialogView.inputField.text.toString().trim()
                    if (newEntry.isNotBlank()) {
                        viewModel.advancedScoringLists.add(newEntry)
                        advancedScoringAdapter.notifyDataSetChanged()
                        checkRecyclerViews()
                    }
                },
                R.string.cancel,
                { }
            )
        }

        // List
        defaultListOrderText.text = Utility.getMediaListOrderByString(viewModel.selectedDefaultListOrder)

        defaultListOrderText.setOnClickListener {
            MaterialAlertDialogBuilder(activity)
                .setItems(viewModel.defaultListOrderStringArray) { _, which ->
                    viewModel.selectedDefaultListOrder = viewModel.defaultListOrderList[which]
                    defaultListOrderText.text = viewModel.defaultListOrderStringArray[which]
                }
                .show()
        }

        // Custom Anime Lists
        customAnimeListsAdapter = assignAdapter(viewModel.customAnimeLists, LIST_CUSTOM_ANIME)
        customAnimeListsRecyclerView.adapter = customAnimeListsAdapter

        addMoreCustomAnimeListsText.setOnClickListener {
            val inputDialogView = layoutInflater.inflate(R.layout.dialog_input, inputDialogLayout, false)
            inputDialogView.inputField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            inputDialogView.inputField.filters = arrayOf(InputFilter.LengthFilter(CHARACTER_LIMIT))

            DialogUtility.showCustomViewDialog(
                activity,
                R.string.add_new_list,
                inputDialogView,
                R.string.add,
                {
                    val newEntry = inputDialogView.inputField.text.toString().trim()
                    if (newEntry.isNotBlank()) {
                        viewModel.customAnimeLists.add(newEntry)
                        customAnimeListsAdapter.notifyDataSetChanged()
                        checkRecyclerViews()
                    }
                },
                R.string.cancel,
                { }
            )
        }

        // Custom Manga Lists
        customMangaListsAdapter = assignAdapter(viewModel.customMangaLists, LIST_CUSTOM_MANGA)
        customMangaListsRecyclerView.adapter = customMangaListsAdapter

        addMoreCustomMangaListsText.setOnClickListener {
            val inputDialogView = layoutInflater.inflate(R.layout.dialog_input, inputDialogLayout, false)
            inputDialogView.inputField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            inputDialogView.inputField.filters = arrayOf(InputFilter.LengthFilter(CHARACTER_LIMIT))

            DialogUtility.showCustomViewDialog(
                activity,
                R.string.add_new_list,
                inputDialogView,
                R.string.add,
                {
                    val newEntry = inputDialogView.inputField.text.toString().trim()
                    if (newEntry.isNotBlank()) {
                        viewModel.customMangaLists.add(newEntry)
                        customMangaListsAdapter.notifyDataSetChanged()
                        checkRecyclerViews()
                    }
                },
                R.string.cancel,
                { }
            )
        }

        checkRecyclerViews()

        // Save
        itemSave.setOnMenuItemClickListener {
            loadingLayout.visibility = View.VISIBLE
            viewModel.updateListSettings(
                useAdvancedScoring = advancedScoringCheckBox.isChecked,
                splitAnimeList = splitAnimeCheckBox.isChecked,
                splitMangaList = splitMangaCheckBox.isChecked
            )
            true
        }
    }

    private fun assignAdapter(list: List<String>, code: Int): ListSettingsRvAdapter {
        return ListSettingsRvAdapter(list, code, object : ListSettingsRvAdapter.ListSettingsListener {
            override fun editItem(position: Int, code: Int) {
                val inputDialogView = layoutInflater.inflate(R.layout.dialog_input, inputDialogLayout, false)
                inputDialogView.inputField.inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
                inputDialogView.inputField.filters = arrayOf(InputFilter.LengthFilter(CHARACTER_LIMIT))

                var title = R.string.edit_list

                when (code) {
                    LIST_ADVANCED -> {
                        title = R.string.edit_criteria
                        inputDialogView.inputField.setText(viewModel.advancedScoringLists[position])
                    }
                    LIST_CUSTOM_ANIME -> inputDialogView.inputField.setText(viewModel.customAnimeLists[position])
                    LIST_CUSTOM_MANGA -> inputDialogView.inputField.setText(viewModel.customMangaLists[position])
                }

                inputDialogView.inputField.setSelection(inputDialogView.inputField.length())

                DialogUtility.showCustomViewDialog(
                    activity,
                    title,
                    inputDialogView,
                    R.string.edit,
                    {
                        val newEntry = inputDialogView.inputField.text.toString().trim()
                        if (newEntry.isNotBlank()) {
                            when (code) {
                                LIST_ADVANCED -> {
                                    viewModel.advancedScoringLists[position] = newEntry
                                    advancedScoringAdapter.notifyDataSetChanged()
                                }
                                LIST_CUSTOM_ANIME -> {
                                    viewModel.customAnimeLists[position] = newEntry
                                    customAnimeListsAdapter.notifyDataSetChanged()
                                }
                                LIST_CUSTOM_MANGA -> {
                                    viewModel.customMangaLists[position] = newEntry
                                    customMangaListsAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    },
                    R.string.cancel,
                    { }
                )
            }

            override fun deleteItem(position: Int, code: Int) {
                when (code) {
                    LIST_ADVANCED -> {
                        viewModel.advancedScoringLists.removeAt(position)
                        advancedScoringAdapter.notifyDataSetChanged()
                    }
                    LIST_CUSTOM_ANIME -> {
                        viewModel.customAnimeLists.removeAt(position)
                        customAnimeListsAdapter.notifyDataSetChanged()
                    }
                    LIST_CUSTOM_MANGA -> {
                        viewModel.customMangaLists.removeAt(position)
                        customMangaListsAdapter.notifyDataSetChanged()
                    }
                }

                checkRecyclerViews()
            }
        })
    }

    private fun checkAdvancedScoringLayout() {
        if (viewModel.selectedScoringSystem == ScoreFormat.POINT_100 || viewModel.selectedScoringSystem == ScoreFormat.POINT_10_DECIMAL) {
            advancedScoringCheckBoxLayout.visibility = View.VISIBLE

            if (advancedScoringCheckBox.isChecked) {
                advancedScoringLayout.visibility = View.VISIBLE
            } else {
                advancedScoringLayout.visibility = View.GONE
            }
        } else {
            advancedScoringCheckBoxLayout.visibility = View.GONE
            advancedScoringLayout.visibility = View.GONE
        }
    }

    private fun checkRecyclerViews() {
        if (advancedScoringAdapter.itemCount == 0) {
            advancedScoringNoItemText.visibility = View.VISIBLE
        } else {
            advancedScoringNoItemText.visibility = View.GONE
        }

        if (customAnimeListsAdapter.itemCount == 0) {
            customAnimeListsNoItemText.visibility = View.VISIBLE
        } else {
            customAnimeListsNoItemText.visibility = View.GONE
        }

        if (customMangaListsAdapter.itemCount == 0) {
            customMangaListsNoItemText.visibility = View.VISIBLE
        } else {
            customMangaListsNoItemText.visibility = View.GONE
        }
    }
}
