package com.yumik.absintelligentcloud.logic.network.service;

import com.yumik.absintelligentcloud.logic.network.body.LoginBody;
import com.yumik.absintelligentcloud.logic.network.response.BaseResponse
import com.yumik.absintelligentcloud.logic.network.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface LoginService {
    @POST("http://82.156.209.212:22222/api/user/login")
    fun login(
        @Body data: LoginBody
    ): Call<BaseResponse<LoginResponse>>
}