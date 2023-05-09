package com.zen.alchan.ui.browse

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import com.zen.alchan.databinding.FragmentBrowseBinding
import com.zen.alchan.ui.base.BaseFragment
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.koin.androidx.viewmodel.ext.android.viewModel

class BrowseFragment : BaseFragment<FragmentBrowseBinding, BrowseViewModel>() {

    override val viewModel: BrowseViewModel by viewModel()

    lateinit var layout: FragmentContainerView
        private set

    private val _layoutSet = PublishSubject.create<Unit>()
    val layoutSet: Observable<Unit>
        get() = _layoutSet

    override fun generateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBrowseBinding {
        return FragmentBrowseBinding.inflate(inflater, container, false)
    }

    override fun setUpLayout() {
        layout = binding.browseLayout
        _layoutSet.onNext(Unit)
        _layoutSet.onComplete()
    }

    override fun setUpObserver() {

    }

    companion object {
        const val ID = "id"

        @JvmStatic
        fun newInstance() = BrowseFragment()
    }
}