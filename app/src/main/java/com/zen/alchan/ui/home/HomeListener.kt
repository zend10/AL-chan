package com.zen.alchan.ui.home

interface HomeListener {

    interface HeaderListener {
        fun navigateToSearch()
    }

    interface MenuListener {
        fun navigateToSeasonal()
        fun showExploreDialog()
        fun navigateToReviews()
        fun navigateToCalendar()
    }

    interface ReleasingTodayListener {

    }

    interface TrendingMediaListener {
        fun navigateToMedia()
    }

    interface NewMediaListener {

    }

    interface RecentReviewsListener {

    }

    val headerListener: HeaderListener
    val menuListener: MenuListener
    val releasingTodayListener: ReleasingTodayListener
    val trendingMediaListener: TrendingMediaListener
    val newMediaListener: NewMediaListener
    val recentReviewsListener: RecentReviewsListener
}