package com.absintelligentcloud.android.logic.network

import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.logic.model.UpdatePasswordResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ManageService {
    @POST("api/password/update")
    fun updatePassword(
        @Query("password") md5Password: String,
        @Header("Authorization") Authorization: String = ABSIntelligentCloudApplication.token
    ): Call<UpdatePasswordResponse>
}