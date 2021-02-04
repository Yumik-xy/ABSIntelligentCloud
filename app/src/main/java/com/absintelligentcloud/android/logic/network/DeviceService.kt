package com.absintelligentcloud.android.logic.network

import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.logic.model.DeviceBody
import com.absintelligentcloud.android.logic.model.DeviceResponse
import com.absintelligentcloud.android.logic.model.StatusDeviceBody
import retrofit2.Call
import retrofit2.http.*

interface DeviceService {
    @POST("api/abs/pagination")
    fun searchDevices(
        @Body data: DeviceBody,
        @Header("Authorization") Authorization: String = ABSIntelligentCloudApplication.token
    ): Call<DeviceResponse>

    @POST("api/abs/status/pagination")
    fun getStatusDevices(
        @Body data: StatusDeviceBody,
        @Header("Authorization") Authorization: String = ABSIntelligentCloudApplication.token
    ): Call<DeviceResponse>

}