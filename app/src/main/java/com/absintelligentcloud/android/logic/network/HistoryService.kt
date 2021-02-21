package com.absintelligentcloud.android.logic.network

import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.logic.model.HistoryBody
import com.absintelligentcloud.android.logic.model.HistoryResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface HistoryService {
    @POST("api/monitor/pagination")
    fun getHistory(
        @Body data: HistoryBody,
        @Header("Authorization") Authorization: String = ABSIntelligentCloudApplication.token
    ): Call<HistoryResponse>
}