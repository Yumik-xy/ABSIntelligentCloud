package com.absintelligentcloud.android.logic.network

import com.absintelligentcloud.android.logic.model.LoginUserBody
import com.absintelligentcloud.android.logic.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
//    一般账号密码登陆方式
    @POST("login.json")
    fun loginNormal(@Body loginUser: LoginUserBody): Call<LoginResponse>
}