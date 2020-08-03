package com.zen.alchan.ui.seasonal

import com.zen.alchan.data.response.SeasonalAnime

interface SeasonalListener {
    fun openDetail(seasonalAnime: SeasonalAnime)
    fun openAnime(id: Int)
    fun addToPlanning(id: Int)
}