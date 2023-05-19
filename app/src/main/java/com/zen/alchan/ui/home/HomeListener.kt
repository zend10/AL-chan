package com.zen.alchan.ui.home

import com.zen.alchan.data.response.anilist.Media
import com.zen.alchan.data.response.anilist.MediaList

interface HomeListener {

    interface HeaderListener {
        fun navigateToSearch()
    }

    interface MenuListener {
        fun navigateToSeasonal()
        fun showExploreDialog()
        fun navigateToReview()
        fun navigateToCalendar()
    }

    interface ReleasingTodayListener {
        fun navigateToMedia(media: Media)
        fun navigateToListEditor(mediaList: MediaList)
        fun showProgressDialog(mediaList: MediaList)
    }

    interface SocialListener {
        fun navigateToSocial()
    }

    interface TrendingMediaListener {
        fun navigateToMedia(media: Media)
    }

    interface NewMediaListener {

    }

    interface RecentReviewsListener {

    }

    val headerListener: HeaderListener
    val menuListener: MenuListener
    val releasingTodayListener: ReleasingTodayListener
    val socialListener: SocialListener
    val trendingMediaListener: TrendingMediaListener
    val newMediaListener: NewMediaListener
    val recentReviewsListener: RecentReviewsListener
}