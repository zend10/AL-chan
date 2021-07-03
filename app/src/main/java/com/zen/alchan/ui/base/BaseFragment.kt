package com.zen.alchan.ui.base

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.ui.common.BottomSheetListDialog
import com.zen.alchan.ui.common.BottomSheetListRvAdapter
import com.zen.alchan.ui.common.BottomSheetTextInputDialog
import com.zen.alchan.ui.launch.LaunchActivity
import com.zen.alchan.ui.root.RootActivity
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment<VB: ViewBinding, VM: BaseViewModel> : Fragment(), ViewContract {

    private val rootActivity: RootActivity
        get() = activity as RootActivity

    protected val navigation by lazy {
        rootActivity.navigationManager
    }

    protected val dialog by lazy {
        rootActivity.dialogManager
    }

    protected abstract val viewModel: VM

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    protected abstract fun generateViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    protected val disposables = CompositeDisposable()
    protected val sharedDisposables = CompositeDisposable()

    protected var screenWidth = 0

    protected var bottomSheetListDialog: BottomSheetListDialog? = null
    protected var bottomSheetTextInputDialog: BottomSheetTextInputDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = generateViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels

        setUpLayout()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpInsets()
    }

    override fun onStart() {
        super.onStart()
        setUpObserver()
    }

    override fun onResume() {
        super.onResume()
        if (disposables.isDisposed || disposables.size() == 0) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        bottomSheetListDialog = null
        bottomSheetTextInputDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
        sharedDisposables.clear()
    }

    protected fun goBack() {
        rootActivity.onBackPressed()
    }

    protected fun setUpToolbar(
        toolbar: Toolbar,
        title: String,
        icon: Int = R.drawable.ic_left,
        action: () -> Unit = { goBack() }
    ) {
        toolbar.apply {
            setTitle(title)
            setNavigationIcon(icon)
            setNavigationOnClickListener { action() }
        }
    }

    protected fun <T> showListDialog(list: List<ListItem<T>>, action: (data: T, index: Int) -> Unit) {
        val adapter = BottomSheetListRvAdapter(requireContext(), list, object : BottomSheetListRvAdapter.BottomSheetListListener<T> {
            override fun getSelectedItem(data: T, index: Int) {
                dismissListDialog()
                action(data, index)
            }
        })
        bottomSheetListDialog = BottomSheetListDialog.newInstance(adapter)
        bottomSheetListDialog?.show(childFragmentManager, null)
    }

    protected fun showListDialog(adapter: BaseRecyclerViewAdapter<*, *>) {
        bottomSheetListDialog = BottomSheetListDialog.newInstance(adapter)
        bottomSheetListDialog?.show(childFragmentManager, null)
    }

    protected fun dismissListDialog() {
        bottomSheetListDialog?.dismiss()
    }

//
//    protected fun showTextInputDialog(currentText: String, textInputSetting: TextInputSetting, listener: BottomSheetTextInputDialog.BottomSheetTextInputListener) {
//        bottomSheetTextInputDialog = BottomSheetTextInputDialog.newInstance(currentText, textInputSetting, listener)
//        bottomSheetTextInputDialog?.show(childFragmentManager, null)
//    }
//
//    protected fun dismissTextInputDialog() {
//        bottomSheetTextInputDialog?.dismiss()
//    }

    protected fun restartApp() {
        val intent = Intent(rootActivity, LaunchActivity::class.java)
        startActivity(intent)
        rootActivity.overridePendingTransition(0, 0)
        rootActivity.finish()
    }
}
