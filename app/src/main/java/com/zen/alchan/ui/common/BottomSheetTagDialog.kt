package com.zen.alchan.ui.common

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zen.alchan.data.response.anilist.MediaTag
import com.zen.databinding.DialogBottomSheetTagBinding
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.ui.base.BaseDialogFragment
import kotlinx.coroutines.selects.select


class BottomSheetTagDialog : BaseDialogFragment<DialogBottomSheetTagBinding>() {

    private var adapter: TagRvAdapter? = null
    private var list: List<ListItem<MediaTag?>> = listOf()
    private var selectedTagIds: ArrayList<Int> = arrayListOf()
    private var listener: TagDialogListener? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogBottomSheetTagBinding {
        return DialogBottomSheetTagBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        adapter = TagRvAdapter(requireContext(), list, selectedTagIds, object : TagRvAdapter.TagListener {
            override fun getSelectedItems(selectedTagIds: ArrayList<Int>) {
                this@BottomSheetTagDialog.selectedTagIds = selectedTagIds

                val selectedTags = list
                    .filter {
                        it.data != null && selectedTagIds.contains(it.data.id)
                    }
                    .mapNotNull {
                        it.data
                    }

                listener?.getSelectedTags(selectedTags)
            }
        })
        binding.dialogEditText.addTextChangedListener {
            if (it.isNullOrBlank())
                adapter?.updateData(list)
            else {
                val filteredItems = list.filter { listItem ->
                    listItem.data == null || listItem.data.name.contains(it, true)
                }
                val filteredTags = ArrayList<ListItem<MediaTag?>>()
                filteredItems.forEachIndexed { index, listItem ->
                    val hasNextItem = index + 1 < filteredItems.size
                    val isCategory = listItem.data == null
                    var shouldAdd = true
                    if (isCategory) {
                        if (hasNextItem) {
                            if (filteredItems[index + 1].data == null) {
                                shouldAdd = false
                            }
                        } else {
                            shouldAdd = false
                        }
                    }
                    if (shouldAdd) {
                        filteredTags.add(listItem)
                    }
                }
                adapter?.updateData(
                    filteredTags
                )
            }

        }
        binding.dialogRecyclerView.adapter = adapter

    }

    override fun setUpObserver() {
        // do nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    interface TagDialogListener {
        fun getSelectedTags(list: List<MediaTag>)
    }

    companion object {
        fun newInstance(list: List<ListItem<MediaTag?>>, selectedTagIds: ArrayList<Int>, listener: TagDialogListener) =
            BottomSheetTagDialog().apply {
                this.list = list
                this.selectedTagIds = selectedTagIds
                this.listener = listener
            }
    }
}