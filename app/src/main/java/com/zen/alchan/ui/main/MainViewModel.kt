package com.zen.alchan.ui.main

import com.zen.alchan.data.repository.ContentRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.extensions.applyScheduler
import com.zen.alchan.ui.base.BaseViewModel

class MainViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : BaseViewModel<Unit>() {

    val isViewerAuthenticated: Boolean
        get() = userRepository.getIsAuthenticated().blockingFirst()

    override fun loadData(param: Unit) {
        loadOnce {
            disposables.add(
                contentRepository.getGenres().subscribe({}, {})
            )

            disposables.add(
                contentRepository.getTags().subscribe({}, {})
            )
        }
    }
}