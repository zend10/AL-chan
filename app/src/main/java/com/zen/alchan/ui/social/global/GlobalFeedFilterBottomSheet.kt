package com.zen.alchan.ui.social.global

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.BestFriend
import com.zen.alchan.helper.utils.AndroidUtility
import kotlinx.android.synthetic.main.bottomsheet_filter_global_activity.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import type.ActivityType

class GlobalFeedFilterBottomSheet : BottomSheetDialogFragment() {

    interface GlobalFeedFilterListener {
        fun passFilterData(selectedFilterIndex: Int, activityTypes: ArrayList<ActivityType>?)
    }

    private val viewModel by viewModel<GlobalFeedFilterViewModel>()
    private lateinit var listener: GlobalFeedFilterListener

    private lateinit var dialogView: View

    private lateinit var filterAdapter: GlobalFeedFilterRvAdapter

    companion object {
        const val SELECTED_FILTER = "selectedFilter"
        const val SELECTED_TYPE = "selectedType"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialogView = inflater.inflate(R.layout.bottomsheet_filter_global_activity, container, false)

        viewModel.reinitBestFriends()

        viewModel.selectedFilterIndex = arguments?.getInt(SELECTED_FILTER, 0) ?: 0
        viewModel.selectedActivityType = viewModel.activityTypeList[arguments?.getInt(SELECTED_TYPE, 0) ?: 0]

        viewModel.bestFriends[viewModel.selectedFilterIndex!!].isSelected = true

        initLayout()

        return dialogView
    }

    private fun initLayout() {
        filterAdapter = assignFilterAdapter()
        dialogView.globalFeedFilterRecyclerView.adapter = filterAdapter

        dialogView.activityTypeText.text = getString(viewModel.activityTypeArray[viewModel.activityTypeList.indexOf(viewModel.selectedActivityType)])
        dialogView.activityTypeLayout.setOnClickListener {
            val activityTypeStringArray = viewModel.activityTypeArray.map { getString(it) }.toTypedArray()
            MaterialAlertDialogBuilder(activity)
                .setItems(activityTypeStringArray) { _, which ->
                    viewModel.selectedActivityType = viewModel.activityTypeList[which]
                    dialogView.activityTypeText.text = getString(viewModel.activityTypeArray[which])
                }
                .show()
        }

        dialogView.filterApplyButton.setOnClickListener {
            listener.passFilterData(viewModel.selectedFilterIndex ?: 0, viewModel.selectedActivityType)
            dismiss()
        }
    }

    private fun assignFilterAdapter(): GlobalFeedFilterRvAdapter {
        return GlobalFeedFilterRvAdapter(activity!!, viewModel.bestFriends, AndroidUtility.getScreenWidth(activity) / resources.getInteger(R.integer.horizontalListCharacterDivider), object : GlobalFeedFilterRvAdapter.BestFriendListener {
            override fun passSelectedBestFriend(position: Int, id: Int?) {
                viewModel.selectedFilterIndex = position

                viewModel.bestFriends.forEachIndexed { index, _ ->
                    viewModel.bestFriends[index].isSelected = false
                }

                viewModel.bestFriends[position].isSelected = !viewModel.bestFriends[position].isSelected

                filterAdapter.notifyDataSetChanged()
            }
        })
    }

    fun setListener(globalFeedFilterListener: GlobalFeedFilterListener) {
        listener = globalFeedFilterListener
    }
}