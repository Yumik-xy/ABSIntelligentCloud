package com.yumik.absintelligentcloud.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.logic.network.Network
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.body.LoginBody
import com.yumik.absintelligentcloud.logic.network.response.LoginResponse
import com.yumik.absintelligentcloud.logic.network.service.LoginService
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // 登录
    val loginLiveData = Network.StateLiveData<LoginResponse>()

    fun login(loginBody: LoginBody) {
        viewModelScope.launch {
            val res = Network.apiCall {
                ServiceCreator.create(LoginService::class.java)
                    .login(loginBody)
            }
            loginLiveData.value = res
        }
    }
}