package com.yumik.absintelligentcloud.ui.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.service.UpdatePasswordService
import kotlinx.coroutines.launch

class MineViewModel : ViewModel() {

    // 更新密码
    val updatePasswordLiveData = Repository.StateLiveData<Nothing>()

    fun updatePassword(password: String, token: String) {
        viewModelScope.launch {
            val res = Repository.apiCall {
                ServiceCreator.create(UpdatePasswordService::class.java)
                    .updatePassword(password, token)
            }
            updatePasswordLiveData.value = res
        }
    }
}