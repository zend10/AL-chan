package com.zen.alchan.ui.common

import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.R
import com.zen.alchan.ui.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_bottom_sheet_list.*

class BottomSheetListDialog : BaseDialogFragment(R.layout.dialog_bottom_sheet_list) {

    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

    override fun setUpLayout() {
        dialogRecyclerView.adapter = adapter
    }

    override fun setUpObserver() {
        // do nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        fun newInstance(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) =
            BottomSheetListDialog().apply {
                this.adapter = adapter
            }
    }
}