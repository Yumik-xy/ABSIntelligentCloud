package com.absintelligentcloud.android.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.logic.model.LoginBody

class LoginViewModel : ViewModel() {

    private val userLiveData = MutableLiveData<LoginBody>()

    var username = ""

    var password = ""

    val loginLiveData = Transformations.switchMap(userLiveData) { user ->
        Repository.loginNormal(user)
    }

    fun loginNormal(user: LoginBody) {
        userLiveData.value = user
    }

    fun saveToken(token: String) = Repository.saveToken(token)
}