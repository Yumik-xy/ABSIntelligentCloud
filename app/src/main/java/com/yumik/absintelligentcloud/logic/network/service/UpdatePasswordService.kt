package com.yumik.absintelligentcloud.logic.network.service

import com.yumik.absintelligentcloud.logic.network.response.BaseResponse
import com.yumik.absintelligentcloud.logic.network.response.EmptyResponse
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UpdatePasswordService {
    @POST("api/password/update")
    fun updatePassword(
        @Query("password") password: String,
        @Header("Authorization") Authorization: String
    ): Call<BaseResponse<Nothing>>
}