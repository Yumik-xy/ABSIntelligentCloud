package com.absintelligentcloud.android.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.logic.model.LoginUserBody

class LoginViewModel : ViewModel() {

    private val userLiveData = MutableLiveData<LoginUserBody>()

    var user = ""

    var password = ""

    val loginLiveData = Transformations.switchMap(userLiveData) { user ->
        Repository.loginNormal(user)
    }

    fun loginNormal(user: LoginUserBody) {
        userLiveData.value = user
    }

}