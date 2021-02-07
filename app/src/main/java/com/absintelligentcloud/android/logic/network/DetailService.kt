package com.absintelligentcloud.android.logic.network

import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.logic.model.*
import retrofit2.Call
import retrofit2.http.*

interface DetailService {
    //    上传数据信息
    @POST("/api/abs/add")
    fun addDevice(
        @Body data: DetailBody,
        @Header("Authorization") Authorization: String = ABSIntelligentCloudApplication.token
    ): Call<NormalResponse>

    //    获取数据信息
    @GET("/api/abs/detail")
    fun getDevice(
        @Query("deviceId") deviceId: String,
        @Header("Authorization") Authorization: String = ABSIntelligentCloudApplication.token
    ): Call<DetailResponse>

    //    更新数据信息
    @POST("/api/abs/update")
    fun updateDevice(
        @Body data: DetailBody,
        @Header("Authorization") Authorization: String = ABSIntelligentCloudApplication.token
    ): Call<NormalResponse>

    //    删除数据信息
    @POST("/api/abs/delete")
    fun deleteDevice(
        @Query("deviceId") deviceId: String,
        @Header("Authorization") Authorization: String = ABSIntelligentCloudApplication.token
    ): Call<NormalResponse>

    //    更新故障信息
    @POST("api/abs/status/update")
    fun solveDevice(
        @Query("deviceId") deviceId: String,
        @Header("Authorization") Authorization: String = ABSIntelligentCloudApplication.token
    ): Call<NormalResponse>
}