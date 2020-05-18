package com.zen.alchan.ui.profile.stats

import androidx.lifecycle.ViewModel
import com.zen.alchan.data.repository.UserRepository

class StatsViewModel(private val userRepository: UserRepository) : ViewModel() {

    var userStats: UserStatisticsQuery.Statistics? = null

    val userStatisticsResponse by lazy {
        userRepository.userStatisticsResponse
    }

    fun getStatistics() {
        userRepository.getStatistics()
    }
}