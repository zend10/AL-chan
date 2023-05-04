package com.zen.alchan.data.network.retrofit

import com.zen.alchan.data.network.OkHttpHandler
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class DefaultRetrofitHandler(
    private val okHttpHandler: OkHttpHandler,
    private val gitHubBaseUrl: String,
    private val jikanBaseUrl: String
) : RetrofitHandler {

    private var gitHubRestService: GitHubRestService? = null
    private var jikanRestService: JikanRestService? = null

    override fun gitHubRetrofitClient(): GitHubRestService {
        if (gitHubRestService == null) {
            val okHttpClient = okHttpHandler.okHttpClientBuilder
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(gitHubBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()

            gitHubRestService = retrofit.create(GitHubRestService::class.java)

            return gitHubRestService!!
        }
        return gitHubRestService!!
    }

    override fun jikanRetrofitClient(): JikanRestService {
        if (jikanRestService == null) {
            val okHttpClient = okHttpHandler.okHttpClientBuilder
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(jikanBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()

            jikanRestService = retrofit.create(JikanRestService::class.java)

            return jikanRestService!!
        }
        return jikanRestService!!
    }
}