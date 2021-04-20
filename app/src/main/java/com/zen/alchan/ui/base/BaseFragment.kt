package com.zen.alchan.ui.base

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zen.alchan.ui.root.RootActivity
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment(private val layout: Int) : Fragment(), ViewContract {

    private val rootActivity: RootActivity
        get() = activity as RootActivity

    protected val disposables = CompositeDisposable()

    protected var screenWidth = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels

        setupLayout()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupInsets()
    }

    override fun onStart() {
        super.onStart()
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        if (disposables.isDisposed) {
            setupObserver()
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

    protected fun navigate(page: NavigationManager.Page, vararg params: String = arrayOf()) {
        rootActivity.navigationManager.navigate(page, params.toList())
    }

    protected fun openWebView(url: String) {
        rootActivity.navigationManager.openWebView(url)
    }

    protected fun openWebView(url: NavigationManager.Url) {
        rootActivity.navigationManager.openWebView(url)
    }
}
