package com.absintelligentcloud.android.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.logic.model.DetailBody
import com.absintelligentcloud.android.logic.model.DeviceBody


class DetailViewModel : ViewModel() {

    private val getLiveData = MutableLiveData<String>()

    val detailLiveData = Transformations.switchMap(getLiveData) { result ->
        Repository.getDevice(result)
    }

    fun getDeviceDetail(deviceId: String) {
        getLiveData.value = deviceId
    }

    private val addLiveData = MutableLiveData<DetailBody>()

    val addResponseLiveData = Transformations.switchMap(addLiveData) { result ->
        Repository.addDevice(result)
    }

    fun addDevice(device: DetailBody) {
        addLiveData.value = device
    }

    private val updateLiveData = MutableLiveData<DetailBody>()

    val updateResponseLiveData = Transformations.switchMap(updateLiveData) { result ->
        Repository.updateDevice(result)
    }

    fun updateDevice(device: DetailBody) {
        updateLiveData.value = device
    }

    private val deleteLiveData = MutableLiveData<String>()

    val deleteResponseLiveData = Transformations.switchMap(deleteLiveData) { result ->
        Repository.deleteDevice(result)
    }

    fun deleteDevice(deviceId: String) {
        deleteLiveData.value = deviceId
    }

    private val solveLiveData = MutableLiveData<String>()

    val solveResponseLiveData = Transformations.switchMap(solveLiveData) { result ->
        Repository.solveDevice(result)
    }

    fun solveDevice(deviceId: String) {
        solveLiveData.value = deviceId
    }

}