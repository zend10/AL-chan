package com.zen.alchan.ui.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zen.alchan.R
import io.reactivex.disposables.CompositeDisposable

abstract class BaseDialogFragment<VB: ViewBinding> : BottomSheetDialogFragment(), ViewContract {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    abstract fun generateViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    protected val disposables = CompositeDisposable()
    protected var inputMethodManager: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = generateViewBinding(activity?.layoutInflater ?: inflater, container)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLayout()
    }

    override fun onStart() {
        super.onStart()
        setUpObserver()
    }

    override fun onResume() {
        super.onResume()
        if (disposables.isDisposed) {
            setUpObserver()
        }
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        inputMethodManager?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        inputMethodManager = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        inputMethodManager?.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    protected fun openKeyboard() {
        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}