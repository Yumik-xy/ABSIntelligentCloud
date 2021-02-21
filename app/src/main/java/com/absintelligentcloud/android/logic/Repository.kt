package com.absintelligentcloud.android.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.absintelligentcloud.android.logic.dao.AreaDao
import com.absintelligentcloud.android.logic.dao.LoginDao
import com.absintelligentcloud.android.logic.model.*
import com.absintelligentcloud.android.logic.network.ABSIntelligentCloudNetwork
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

    fun getStatusDevices(statusDeviceBody: StatusDeviceBody) = fire(Dispatchers.IO) {
        val deviceResponse = ABSIntelligentCloudNetwork.getStatusDevices(statusDeviceBody)
        if (deviceResponse.success) {
            Log.d("Repository", deviceResponse.toString())
            val devices = deviceResponse.data.list
            Result.success(devices)
        } else {
            Result.failure(RuntimeException("response message is ${deviceResponse.message}."))
        }
    }

    fun loginNormal(user: LoginBody) = fire(Dispatchers.IO) {
        val loginResponse = ABSIntelligentCloudNetwork.loginNormal(user)
        Log.d("Repository", loginResponse.toString())
        if (loginResponse.code == 0) {
            val login = loginResponse.data
            Result.success(login)
        } else {
            Result.failure(RuntimeException("response message is ${loginResponse.message}."))
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

    fun saveToken(token: String) = LoginDao.saveToken(token)

    fun removeToken() = LoginDao.removeToken()

    fun getSavedToken() = LoginDao.getSavedToken()

    fun isTokenSaved() = LoginDao.isTokenSaved()

    fun addDevice(device: DetailBody) = fire(Dispatchers.IO) {
        val normalResponse = ABSIntelligentCloudNetwork.addDevice(device)
        if (normalResponse.success) {
            Result.success(normalResponse)
        } else {
            Result.failure(RuntimeException("response message is ${normalResponse.message}."))
        }
    }

    fun getDevice(deviceId: String) = fire(Dispatchers.IO) {
        val detailResponse = ABSIntelligentCloudNetwork.getDevice(deviceId)
        if (detailResponse.success) {
            val devices = detailResponse.data
            Result.success(devices)
        } else {
            Result.failure(RuntimeException("response message is ${detailResponse.message}."))
        }
    }

    fun updateDevice(device: DetailBody) = fire(Dispatchers.IO) {
        val normalResponse = ABSIntelligentCloudNetwork.updateDevice(device)
        if (normalResponse.success) {
            Result.success(normalResponse)
        } else {
            Result.failure(RuntimeException("response message is ${normalResponse.message}."))
        }
    }

    fun deleteDevice(deviceId: String) = fire(Dispatchers.IO) {
        val normalResponse = ABSIntelligentCloudNetwork.deleteDevice(deviceId)
        if (normalResponse.success) {
            Result.success(normalResponse)
        } else {
            Result.failure(RuntimeException("response message is ${normalResponse.message}."))
        }
    }

    fun solveDevice(deviceId: String) = fire(Dispatchers.IO) {
        val normalResponse = ABSIntelligentCloudNetwork.solveDevice(deviceId)
        if (normalResponse.success) {
            Result.success(normalResponse)
        } else {
            Result.failure(RuntimeException("response message is ${normalResponse.message}."))
        }
    }

    fun getHistory(history: HistoryBody) = fire(Dispatchers.IO) {
        val historyResponse = ABSIntelligentCloudNetwork.getHistory(history)
        Log.d("Repository", historyResponse.toString())
        if (historyResponse.success) {
            val histories = historyResponse.data.list
            Result.success(histories)
        } else {
            Result.failure(RuntimeException("response message is ${historyResponse.message}."))
        }
    }

    fun updatePassword(md5Password: String) = fire(Dispatchers.IO) {
        val updatePasswordResponse = ABSIntelligentCloudNetwork.updatePassword(md5Password)
        if (updatePasswordResponse.success) {
            Result.success(updatePasswordResponse.message)
        } else {
            Result.failure(RuntimeException("response message is ${updatePasswordResponse.message}."))
        }
    }

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