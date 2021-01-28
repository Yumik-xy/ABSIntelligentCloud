package com.absintelligentcloud.android.logic.network

import com.absintelligentcloud.android.logic.model.DeviceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DeviceService {
    @GET("v2")
    fun searchDevices(@Query("query") query: String): Call<DeviceResponse>
}