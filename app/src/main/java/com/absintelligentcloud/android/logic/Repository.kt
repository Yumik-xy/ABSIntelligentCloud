package com.absintelligentcloud.android.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.absintelligentcloud.android.logic.dao.AreaDao
import com.absintelligentcloud.android.logic.model.AreaResponse
import com.absintelligentcloud.android.logic.model.DeviceBody
import com.absintelligentcloud.android.logic.model.LoginUserBody
import com.absintelligentcloud.android.logic.network.ABSIntelligentCloudNetwork
import com.absintelligentcloud.android.ui.device.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext


object Repository {
    fun searchDevices(deviceBody: DeviceBody) = fire(Dispatchers.IO) {
        val deviceResponse = ABSIntelligentCloudNetwork.searchDevices(deviceBody)
        if (deviceResponse.success) {
            Log.d("Repository", deviceResponse.toString())
            val devices = deviceResponse.data.list
            Result.success(devices)
        } else {
            Result.failure(RuntimeException("response message is ${deviceResponse.message}."))
        }
    }

    fun getStatusDevices(page: Int) = fire(Dispatchers.IO) {
        val deviceResponse = ABSIntelligentCloudNetwork.getStatusDevices(page)
        if (deviceResponse.success) {
            Log.d("Repository", deviceResponse.toString())
            val devices = deviceResponse.data.list
            Result.success(devices)
        } else {
            Result.failure(RuntimeException("response message is ${deviceResponse.message}."))
        }
    }

    fun loginNormal(user: LoginUserBody) = fire(Dispatchers.IO) {
        val loginResponse = ABSIntelligentCloudNetwork.loginNormal(user)
        Log.d("Repository", loginResponse.toString())
        if (loginResponse.status == "ok") {
            val login = loginResponse.login
            Result.success(login)
        } else {
            Result.failure(RuntimeException("response status is ${loginResponse.status}."))
        }
    }

    fun getAreaList() = fire(Dispatchers.IO) {
        val areaResponse = ABSIntelligentCloudNetwork.getAreaList()
        Log.d("Repository", areaResponse.toString())
        if (areaResponse.success) {
            val data = areaResponse.data
            Result.success(data)
        } else {
            Result.failure(RuntimeException("response message is ${areaResponse.message}."))
        }
    }

    fun saveArea(area: AreaResponse.Data) = AreaDao.saveArea(area)

    fun getSavedArea() = AreaDao.getSavedArea()

    fun isAreaSaved() = AreaDao.isAreaSaved()

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
}