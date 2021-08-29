package com.zen.alchan.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.disposables.CompositeDisposable

abstract class BaseDialogFragment<VB: ViewBinding> : BottomSheetDialogFragment(), ViewContract {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    abstract fun generateViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    protected val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = generateViewBinding(activity?.layoutInflater ?: inflater, container)
        return binding.root
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
}