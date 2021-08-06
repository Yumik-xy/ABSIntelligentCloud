package com.yumik.absintelligentcloud.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yumik.absintelligentcloud.logic.Repository
import com.yumik.absintelligentcloud.logic.network.body.LoginBody

class LoginViewModel : ViewModel() {

    // 登录
    private val _loginLiveData = MutableLiveData<LoginBody>()
    val loginLiveData = Transformations.switchMap(_loginLiveData) {
        Repository.loginNormal(it)
    }

    fun login(loginBody: LoginBody) {
        _loginLiveData.value = loginBody
    }
}