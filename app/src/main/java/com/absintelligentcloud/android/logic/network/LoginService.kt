package com.absintelligentcloud.android.logic.network

import com.absintelligentcloud.android.logic.model.LoginBody
import com.absintelligentcloud.android.logic.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
//    一般账号密码登陆方式
    @POST("http://192.144.170.159:22222/api/user/login")
    fun loginNormal(@Body data: LoginBody): Call<LoginResponse>
}