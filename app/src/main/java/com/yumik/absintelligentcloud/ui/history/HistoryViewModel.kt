package com.yumik.absintelligentcloud.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.logic.network.ServiceCreator
import com.yumik.absintelligentcloud.logic.network.body.HistoryListBody
import com.yumik.absintelligentcloud.logic.network.response.HistoryListResponse
import com.yumik.absintelligentcloud.logic.network.service.DeviceService
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    // 获取设备列表
    val historyList = Repository.StateLiveData<HistoryListResponse>()

    fun getHistoryList(historyListBody: HistoryListBody, token: String) {
        viewModelScope.launch {
            val res = Repository.apiCall {
                ServiceCreator.create(DeviceService::class.java)
                    .getHistoryList(historyListBody, token)
            }
            historyList.value = res
        }
    }
}