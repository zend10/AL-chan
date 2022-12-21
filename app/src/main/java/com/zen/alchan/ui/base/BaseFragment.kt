package com.zen.alchan.ui.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.zen.alchan.R
import com.zen.alchan.helper.pojo.ListItem
import com.zen.alchan.helper.pojo.TextInputSetting
import com.zen.alchan.helper.utils.DeepLink
import com.zen.alchan.ui.common.BottomSheetListDialog
import com.zen.alchan.ui.common.BottomSheetListRvAdapter
import com.zen.alchan.ui.common.BottomSheetTextInputDialog
import com.zen.alchan.ui.launch.LaunchActivity
import com.zen.alchan.ui.root.RootActivity
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment<VB: ViewBinding, VM: BaseViewModel<*>> : Fragment(), ViewContract {

    private val className = javaClass.simpleName

    private val parentActivity: BaseActivity<*>
        get() = activity as BaseActivity<*>

    protected val navigation by lazy {
        parentActivity.navigationManager
    }

    protected val dialog by lazy {
        parentActivity.dialogManager
    }

    protected val incomingDeepLink by lazy {
        parentActivity.incomingDeepLink
    }

    protected abstract val viewModel: VM

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    protected abstract fun generateViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    protected val disposables = CompositeDisposable()
    protected val sharedDisposables = CompositeDisposable()

    protected var screenWidth = 0

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
        Log.d(className, "onStart")
        setUpObserver()
    }

    override fun onResume() {
        super.onResume()
        Log.d(className, "onResume")
        if (disposables.isDisposed || disposables.size() == 0) {
            setUpObserver()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(className, "onPause")
        disposables.clear()
    }

    override fun onStop() {
        super.onStop()
        Log.d(className, "onStop")
        disposables.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(className, "onDestroy")
        disposables.clear()
        sharedDisposables.clear()
    }

    protected fun goBack() {
        parentActivity.onBackPressed()
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

    protected fun restartApp(deepLink: DeepLink? = null, skipSplashScreen: Boolean = true) {
        val intent = Intent(parentActivity, LaunchActivity::class.java)
        if (deepLink?.uri != null) {
            intent.data = deepLink.uri
            intent.putExtra("RESTART", skipSplashScreen)
        }
        startActivity(intent)
        parentActivity.overridePendingTransition(0, 0)
        parentActivity.finish()
    }

    protected fun toggleKeyboard(shouldOpen: Boolean) {
        activity?.window?.let { window ->
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            if (shouldOpen)
                controller.show(WindowInsetsCompat.Type.ime())
            else
                controller.hide(WindowInsetsCompat.Type.ime())
        }
    }
}
