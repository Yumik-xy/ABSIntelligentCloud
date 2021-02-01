package com.absintelligentcloud.android.logic.network

import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.logic.model.AreaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface AreaService {
    @GET("api/area/list")
    fun getAreaList(
        @Header("Authorization") Authorization: String = ABSIntelligentCloudApplication.token
    )
            : Call<AreaResponse>
}