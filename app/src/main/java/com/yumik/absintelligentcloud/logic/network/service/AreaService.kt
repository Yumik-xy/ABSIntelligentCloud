package com.yumik.absintelligentcloud.logic.network.service;

import com.yumik.absintelligentcloud.logic.model.Area
import com.yumik.absintelligentcloud.logic.network.response.BaseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface AreaService {
    @GET("api/area/list")
    suspend fun getAreaList(
        @Header("Authorization") token: String
    ): BaseResponse<List<Area>>
}