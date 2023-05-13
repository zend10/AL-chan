package com.zen.alchan.data.network.retrofit

interface RetrofitHandler {
    fun gitHubRetrofitClient(): GitHubRestService
    fun jikanRetrofitClient(): JikanRestService
    fun animeThemesRetrofitClient(): AnimeThemesRestService
}