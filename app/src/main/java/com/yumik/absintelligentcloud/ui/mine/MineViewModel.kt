package com.yumik.absintelligentcloud.ui.mine

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yumik.absintelligentcloud.logic.Repository
import com.yumik.absintelligentcloud.logic.network.body.UpdatePasswordBody

class MineViewModel : ViewModel() {

    // 更新密码
    private val _updatePasswordLiveData = MutableLiveData<Pair<UpdatePasswordBody, String>>()
    val updatePasswordLiveData = Transformations.switchMap(_updatePasswordLiveData) {
        Repository.updatePassword(it.first, it.second)
    }

    fun updatePassword(password: String, token: String) {
        _updatePasswordLiveData.value = Pair(UpdatePasswordBody(password), token)
    }

}