package com.yumik.absintelligentcloud.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.yumik.absintelligentcloud.MainActivity
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.logic.Repository
import com.yumik.absintelligentcloud.logic.model.Porcelain
import com.yumik.absintelligentcloud.logic.network.body.StatusDeviceListBody
import com.yumik.absintelligentcloud.ui.equipment.EquipmentFragment

class HomeViewModel : ViewModel() {
    // TODO: 固定值
    private val _porcelainList = MutableLiveData<List<Porcelain>>()
    val porcelainList: LiveData<List<Porcelain>> = _porcelainList

    fun setPorcelainList(list: List<Porcelain>) {
        _porcelainList.value = list
    }

    // 异常设备列表
    private val _statusDeviceList = MutableLiveData<Pair<StatusDeviceListBody, String>>()
    val statusDeviceList = Transformations.switchMap(_statusDeviceList) {
        Repository.getStatusDeviceList(it.first, it.second)
    }

    fun getStatusDeviceList(statusDeviceListBody: StatusDeviceListBody, token: String) {
        _statusDeviceList.value = Pair(statusDeviceListBody, token)
    }

    // 最新异常设备列表
    private val _newStatusDeviceList = MutableLiveData<Pair<StatusDeviceListBody, String>>()
    val newStatusDeviceList = Transformations.switchMap(_newStatusDeviceList) {
        Repository.getStatusDeviceList(it.first, it.second)
    }

    fun getNewStatusDeviceList(statusDeviceListBody: StatusDeviceListBody, token: String) {
        _newStatusDeviceList.value = Pair(statusDeviceListBody, token)
    }
}