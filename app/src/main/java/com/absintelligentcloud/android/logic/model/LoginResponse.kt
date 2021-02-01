package com.absintelligentcloud.android.logic.model

data class LoginResponse(val status: String, val login: Login) {

    data class Login(val role: Int, val token: String)
}
