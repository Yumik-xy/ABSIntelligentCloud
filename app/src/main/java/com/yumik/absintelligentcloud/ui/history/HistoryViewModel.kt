package com.yumik.absintelligentcloud.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.logic.network.Network
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.body.HistoryListBody
import com.yumik.absintelligentcloud.logic.network.response.HistoryListResponse
import com.yumik.absintelligentcloud.logic.network.service.DeviceService
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    // 获取设备列表
    val historyList = Network.StateLiveData<HistoryListResponse>()

    fun getHistoryList(historyListBody: HistoryListBody, token: String) {
        viewModelScope.launch {
            val res = Network.apiCall {
                ServiceCreator.create(DeviceService::class.java)
                    .getHistoryList(historyListBody, token)
            }
            historyList.value = res
        }
    }
}