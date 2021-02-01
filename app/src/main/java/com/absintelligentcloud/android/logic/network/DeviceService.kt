package com.absintelligentcloud.android.logic.network

import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.logic.model.DeviceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DeviceService {
    @GET("get_data.json")
    fun searchDevices(
        @Query("query") query: String,
        @Header("token") token: String = ABSIntelligentCloudApplication.token
    ): Call<DeviceResponse>
}