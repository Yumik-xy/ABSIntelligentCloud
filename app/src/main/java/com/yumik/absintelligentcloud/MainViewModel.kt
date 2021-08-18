package com.yumik.absintelligentcloud

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.response.CheckUpdateResponse
import com.yumik.absintelligentcloud.logic.network.service.DownloadService
import com.yumik.absintelligentcloud.util.ApkVersionCodeUtils.getVerCodeFromName
import com.yumik.absintelligentcloud.util.ApkVersionCodeUtils.getVerName
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // 获取更新信息
    val checkUpdateLiveData = MutableLiveData<CheckUpdateResponse>()

    fun checkUpdate() {
        viewModelScope.launch {
            val res = Repository.apiCallT {
                ServiceCreator.create(DownloadService::class.java)
                    .checkUpdate()
            }
            if (res != null &&
                getVerCodeFromName(getVerName(Application.context)) < getVerCodeFromName(res.versionName)
            ) {
                checkUpdateLiveData.value = res!!
                Application.needUpdate = true
            }
        }
    }
}