package com.yumik.absintelligentcloud.logic.network.service

import com.yumik.absintelligentcloud.logic.network.body.AddDeviceBody
import com.yumik.absintelligentcloud.logic.network.body.DeviceListBody
import com.yumik.absintelligentcloud.logic.network.body.StatusDeviceListBody
import com.yumik.absintelligentcloud.logic.network.body.HistoryListBody
import com.yumik.absintelligentcloud.logic.network.response.BaseResponse
import com.yumik.absintelligentcloud.logic.network.response.DeviceInfoResponse
import com.yumik.absintelligentcloud.logic.network.response.DeviceListResponse
import com.yumik.absintelligentcloud.logic.network.response.HistoryListResponse
import retrofit2.Call
import retrofit2.http.*

interface DeviceService {
    // 获取设备详情
    @GET("/api/abs/detail")
    fun getDeviceInfo(
        @Query("deviceId") deviceId: String,
        @Header("Authorization") Authorization: String
    ): Call<BaseResponse<DeviceInfoResponse>>

    // 删除数据信息
    @POST("/api/abs/delete")
    fun deleteDevice(
        @Query("deviceId") deviceId: String,
        @Header("Authorization") Authorization: String
    ): Call<BaseResponse<Nothing>>

    // 上传数据信息
    @POST("/api/abs/add")
    fun addDevice(
        @Body data: AddDeviceBody,
        @Header("Authorization") Authorization: String
    ): Call<BaseResponse<Nothing>>

    // 更新数据信息
    @POST("/api/abs/update")
    fun updateDevice(
        @Body data: AddDeviceBody,
        @Header("Authorization") Authorization: String
    ): Call<BaseResponse<Nothing>>

    // 更新故障信息
    @POST("api/abs/status/update")
    fun updateFaultDevice(
        @Query("deviceId") deviceId: String,
        @Header("Authorization") Authorization: String
    ): Call<BaseResponse<Nothing>>

    // 获取异常设备列表
    @POST("api/abs/status/pagination")
    fun getStatusDeviceList(
        @Body data: StatusDeviceListBody,
        @Header("Authorization") Authorization: String
    ): Call<BaseResponse<DeviceListResponse>>

    // 获取所有设备列表
    @POST("api/abs/pagination")
    fun getDeviceList(
        @Body data: DeviceListBody,
        @Header("Authorization") Authorization: String
    ): Call<BaseResponse<DeviceListResponse>>

    // 获取历史记录
    @POST("api/monitor/pagination")
    fun getHistoryList(
        @Body data: HistoryListBody,
        @Header("Authorization") Authorization: String
    ): Call<BaseResponse<HistoryListResponse>>
}