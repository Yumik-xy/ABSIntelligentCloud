package com.absintelligentcloud.android.logic.model

data class LoginResponse(val code: Int, val data: Data, val message: String) {

    data class Data(val role: Int, val accessToken: String)
}
