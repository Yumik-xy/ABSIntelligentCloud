package com.yumik.absintelligentcloud.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.yumik.absintelligentcloud.logic.network.Network
import com.yumik.absintelligentcloud.logic.network.body.*
import com.yumik.absintelligentcloud.logic.network.response.BaseResponse
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object Repository {

    private const val TAG = "Repository"

    fun loginNormal(loginBody: LoginBody) = fire(Dispatchers.IO) {
        Network.login(loginBody)
    }

    fun getAreaList(token: String) = fire(Dispatchers.IO) {
        Network.getAreaList(token)
    }

    fun updatePassword(updatePasswordBody: UpdatePasswordBody, token: String) =
        fire(Dispatchers.IO) {
            Network.updatePassword(updatePasswordBody, token)
        }

    fun getDeviceInfo(deviceInfoBody: DeviceInfoBody, token: String) = fire(Dispatchers.IO) {
        Network.getDeviceInfo(deviceInfoBody, token)
    }

    fun getStatusDeviceList(statusDeviceListBody: StatusDeviceListBody, token: String) =
        fire(Dispatchers.IO) {
            Network.getStatusDeviceList(statusDeviceListBody, token)
        }

    fun getDeviceList(deviceListBody: DeviceListBody, token: String) = fire(Dispatchers.IO) {
        Network.getDeviceList(deviceListBody, token)
    }

    fun deleteDevice(deviceId: String, token: String) = fire(Dispatchers.IO) {
        Network.deleteDevice(deviceId, token)
    }

    fun addDevice(addDeviceBody: AddDeviceBody, token: String) = fire(Dispatchers.IO) {
        Network.addDevice(addDeviceBody, token)
    }

    fun updateDevice(addDeviceBody: AddDeviceBody, token: String) = fire(Dispatchers.IO) {
        Network.updateDevice(addDeviceBody, token)
    }

    fun updateFaultDevice(deviceId: String, token: String) = fire(Dispatchers.IO) {
        Network.updateFaultDevice(deviceId, token)
    }

    fun getHistoryList(historyListBody: HistoryListBody, token: String) = fire(Dispatchers.IO) {
        Network.getHistoryList(historyListBody, token)
    }

    fun checkUpdate() = fire(Dispatchers.IO) { Network.checkUpdate() }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> BaseResponse<T>) =
        liveData(context) {
            val result = try {
                val returnResult = block()
                Log.d(TAG, returnResult.toString())
                Result.success(returnResult)
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }
}