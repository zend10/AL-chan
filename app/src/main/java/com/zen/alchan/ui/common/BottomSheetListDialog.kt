package com.zen.alchan.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zen.alchan.databinding.DialogBottomSheetListBinding
import com.zen.alchan.ui.base.BaseDialogFragment

class BottomSheetListDialog : BaseDialogFragment<DialogBottomSheetListBinding>() {

    private var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = null

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
        fun newInstance(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) =
            BottomSheetListDialog().apply {
                this.adapter = adapter
            }
    }
}