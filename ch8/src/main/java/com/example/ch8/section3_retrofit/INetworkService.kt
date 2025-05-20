package com.example.ch8.section3_retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// retrofit 객체에 등록
// 실제 network 가 필요한 순간 interface 의 함수 호출
interface INetworkService {
    @GET("/v2/everything")
    fun getList(
        @Query("q") q: String?,
        @Query("apiKey") apiKey: String?,
        @Query("page") page: Long,
        @Query("pageSize") pageSize: Int
    ): Call<PageListModel>

}