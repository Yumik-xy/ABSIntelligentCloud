package com.absintelligentcloud.android.logic

import androidx.lifecycle.liveData
import com.absintelligentcloud.android.logic.model.Device
import com.absintelligentcloud.android.logic.network.ABSIntelligentCloudNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException


object Repository {
    fun searchDevices(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val deviceResponse = ABSIntelligentCloudNetwork.searchDevices(query)
            if (deviceResponse.status == "ok") {
                val devices = deviceResponse.devices
                Result.success(devices)
            } else {
                Result.failure(RuntimeException("response status is ${deviceResponse.status}."))
            }
        } catch (e: Exception) {
            Result.failure<List<Device>>(e)
        }
        emit(result)
    }
}