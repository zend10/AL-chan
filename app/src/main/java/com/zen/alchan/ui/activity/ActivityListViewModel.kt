package com.zen.alchan.ui.activity

import com.zen.alchan.data.repository.SocialRepository
import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.helper.service.clipboard.ClipboardService
import com.zen.alchan.ui.base.BaseViewModel

class ActivityListViewModel(
    private val socialRepository: SocialRepository,
    private val userRepository: UserRepository,
    private val clipboardService: ClipboardService
) : BaseViewModel<ActivityListParam>() {

    private var userId: Int = 0

    override fun loadData(param: ActivityListParam) {
        loadOnce {
            userId = param.userId
        }
    }
}