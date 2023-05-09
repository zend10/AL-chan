package com.zen.alchan.data.network.retrofit

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DefaultRetrofitHandler(
    private val gitHubBaseUrl: String,
    private val jikanBaseUrl: String
) : RetrofitHandler {

    private val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
        redactHeader("Authorization")
        redactHeader("Cookie")
    }

    private var gitHubRestService: GitHubRestService? = null
    private var jikanRestService: JikanRestService? = null

    override fun gitHubRetrofitClient(): GitHubRestService {
        if (gitHubRestService == null) {
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(gitHubBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build()

            gitHubRestService = retrofit.create(GitHubRestService::class.java)

            return gitHubRestService!!
        }
        return gitHubRestService!!
    }

    override fun jikanRetrofitClient(): JikanRestService {
        if (jikanRestService == null) {
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(jikanBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build()

            jikanRestService = retrofit.create(JikanRestService::class.java)

            return jikanRestService!!
        }
        return jikanRestService!!
    }
}