package com.zen.alchan.ui.base

import android.os.Bundle
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    protected fun navigate(page: NavigationManager.Page) {
        rootActivity.navigationManager.navigate(page)
    }
}
