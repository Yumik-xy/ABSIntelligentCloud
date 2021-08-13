package com.yumik.absintelligentcloud.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.body.LoginBody
import com.yumik.absintelligentcloud.logic.network.response.LoginResponse
import com.yumik.absintelligentcloud.logic.network.service.LoginService
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // 登录
    val loginLiveData = Repository.StateLiveData<LoginResponse>()

    fun login(loginBody: LoginBody) {
        viewModelScope.launch {
            val res = Repository.apiCall {
                ServiceCreator.create(LoginService::class.java)
                    .login(loginBody)
            }
            loginLiveData.value = res
        }
    }
}