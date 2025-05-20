package com.example.ch8.section3_retrofit

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// manifest 에 등록, 앱 실행 시 최초에 한번
class MyApplication : Application() {
    companion object {
        val API_KEY = "c26836668b0d487190ad5498725a7298"
        val retrofitService: INetworkService
        val retrofit: Retrofit
            get() = Retrofit.Builder()
                .baseUrl("https://newsapi.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        init {
            retrofitService = retrofit.create(INetworkService::class.java)
        }
    }
}