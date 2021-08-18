package com.yumik.absintelligentcloud.logic.network.service

import android.database.Observable
import com.yumik.absintelligentcloud.logic.network.response.BaseResponse
import com.yumik.absintelligentcloud.logic.network.response.CheckUpdateResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadService {
    // @GET("https://api.github.com/repos/Yumik-xy/ABSIntelligentCloud/releases/latest")
    @GET("https://api.github.com/repos/Yumik-xy/ABSIntelligentCloud/releases/latest")
    suspend fun checkUpdate(): CheckUpdateResponse

    @Streaming
    @GET
    fun downloadFile(
        @Url url: String
    ): Call<ResponseBody>
}