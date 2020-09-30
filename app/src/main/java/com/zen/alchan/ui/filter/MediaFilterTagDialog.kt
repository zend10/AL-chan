package com.zen.alchan.ui.filter

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.zen.alchan.R
import com.zen.alchan.ui.common.filter.MediaFilterRvAdapter
import kotlinx.android.synthetic.main.dialog_tag.view.*
import kotlinx.android.synthetic.main.fragment_media_overview.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFilterTagDialog : DialogFragment() {

    private val viewModel by viewModel<MediaFilterTagViewModel>()
    private lateinit var adapter: MediaFilterTagRvAdapter
    private lateinit var listener: MediaFilterTagListener

    companion object {
        const val SELECTED_TAGS = "selectedTags"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_tag, null)

        if (!this::listener.isInitialized) {
            dismiss()
        }

        if (arguments?.getStringArrayList(SELECTED_TAGS) != null) {
            viewModel.selectedTags.addAll(arguments?.getStringArrayList(SELECTED_TAGS)!!)
        }

        viewModel.initTagList()

        dialogView.searchTagEditText.addTextChangedListener {
            viewModel.filterTagList(it?.trim().toString())
            adapter.notifyDataSetChanged()
        }

        adapter = MediaFilterTagRvAdapter(viewModel.filteredTagList, object : MediaFilterTagListener {
            override fun passSelectedTag(name: String) {
                val findTag = viewModel.filteredTagList.find { it.name == name }
                if (findTag != null) {
                    findTag.isChecked = !findTag.isChecked
                    val index = viewModel.filteredTagList.indexOf(findTag)
                    adapter.notifyItemChanged(index)
                    listener.passSelectedTag(name)
                }
            }
        })

        dialogView.listTagRecyclerView.adapter = adapter

        builder.setView(dialogView)
        builder.setPositiveButton(R.string.close, null)
        return builder.create()
    }

    fun setListener(mediaFilterTagListener: MediaFilterTagListener) {
        listener = mediaFilterTagListener
    }

    class MediaFilterTagItem(val name: String, val isCategory: Boolean, var isChecked: Boolean)
}