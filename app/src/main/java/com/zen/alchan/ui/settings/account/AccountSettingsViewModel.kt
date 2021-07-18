package com.zen.alchan.ui.settings.account

import com.zen.alchan.data.repository.UserRepository
import com.zen.alchan.ui.base.BaseViewModel

class AccountSettingsViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    override fun loadData() {
        // do nothing
    }

    fun logout() {
        userRepository.logout()
    }
}