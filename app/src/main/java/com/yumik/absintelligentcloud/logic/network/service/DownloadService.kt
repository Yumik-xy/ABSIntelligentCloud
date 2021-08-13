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
    @GET("/file/getApk")
    suspend fun checkUpdate(): BaseResponse<CheckUpdateResponse>

    @Streaming
    @GET
    fun downloadFile(
        @Url url: String,
//        @Header("Range") range: String, /* bytes=startPos-endPos endPos可以省略 36985byte开始断点下载，则传值为："bytes=36985-" */
        @Header("Authorization") Authorization: String
    ): Call<ResponseBody>
}