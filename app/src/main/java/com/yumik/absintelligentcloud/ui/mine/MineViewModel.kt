package com.yumik.absintelligentcloud.ui.mine

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.Application
import com.yumik.absintelligentcloud.logic.network.Network
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.response.CheckUpdateResponse
import com.yumik.absintelligentcloud.logic.network.service.DownloadService
import com.yumik.absintelligentcloud.logic.network.service.UpdatePasswordService
import com.yumik.absintelligentcloud.util.ApkVersionCodeUtils
import kotlinx.coroutines.launch

class MineViewModel : ViewModel() {

    // 更新密码
    val updatePasswordLiveData = Network.StateLiveData<Nothing>()

    fun updatePassword(password: String, token: String) {
        viewModelScope.launch {
            val res = Network.apiCall {
                ServiceCreator.create(UpdatePasswordService::class.java)
                    .updatePassword(password, token)
            }
            updatePasswordLiveData.value = res
        }
    }

    // 获取版本号
    private val _versionName = MutableLiveData<String>().apply {
        value = ApkVersionCodeUtils.getVerName(Application.context)
    }
    val versionName: LiveData<String> = _versionName

    // 获取更新信息
    val checkUpdateLiveData = Network.StateLiveData<CheckUpdateResponse>()

    fun checkUpdate() {
        viewModelScope.launch {
            val res = Network.apiCall {
                ServiceCreator.create(DownloadService::class.java)
                    .checkUpdate()
            }
            checkUpdateLiveData.value = res
        }
    }


}