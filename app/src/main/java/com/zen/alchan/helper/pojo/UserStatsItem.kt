package com.zen.alchan.helper.pojo

import com.zen.alchan.data.response.anilist.UserStatisticsDetail

data class UserStatsItem(
    val chart: List<Chart>? = null,
    val stats: UserStatisticsDetail? = null,
    val viewType: Int = 0
) {
    companion object {
        const val VIEW_TYPE_PIE_CHART = 100
        const val VIEW_TYPE_LINE_CHART = 101
        const val VIEW_TYPE_BAR_CHART = 102
        const val VIEW_TYPE_INFO = 200
    }
}