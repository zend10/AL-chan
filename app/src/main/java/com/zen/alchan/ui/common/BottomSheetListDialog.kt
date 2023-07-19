package com.zen.alchan.ui.common

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zen.alchan.databinding.DialogBottomSheetListBinding
import com.zen.alchan.ui.base.BaseDialogFragment
import com.zen.alchan.ui.base.BaseRecyclerViewAdapter

class BottomSheetListDialog : BaseDialogFragment<DialogBottomSheetListBinding>() {

    private var adapter: BaseRecyclerViewAdapter<*, *>? = null

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogBottomSheetListBinding {
        return DialogBottomSheetListBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        binding.dialogRecyclerView.adapter = adapter
    }

    override fun setUpObserver() {
        // do nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    companion object {
        fun newInstance(adapter: BaseRecyclerViewAdapter<*, *>) =
            BottomSheetListDialog().apply {
                this.adapter = adapter
            }
    }
}