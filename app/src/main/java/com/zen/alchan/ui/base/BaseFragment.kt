package com.zen.alchan.ui.base

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.zen.alchan.R
import com.zen.alchan.ui.root.RootActivity
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment(private val layout: Int) : Fragment(), ViewContract {

    private val rootActivity: RootActivity
        get() = activity as RootActivity

    protected val navigation by lazy {
        rootActivity.navigationManager
    }

    protected val dialog by lazy {
        rootActivity.dialogManager
    }

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
}
