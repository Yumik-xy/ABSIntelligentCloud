package com.yumik.absintelligentcloud.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yumik.absintelligentcloud.logic.Repository
import com.yumik.absintelligentcloud.logic.network.body.HistoryListBody

class HistoryViewModel : ViewModel() {
    // 获取设备列表
    private val _historyList = MutableLiveData<Pair<HistoryListBody, String>>()
    val historyList = Transformations.switchMap(_historyList) {
        Repository.getHistoryList(it.first, it.second)
    }

    fun getHistoryList(historyListBody: HistoryListBody, token: String) {
        _historyList.value = Pair(historyListBody, token)
    }
}