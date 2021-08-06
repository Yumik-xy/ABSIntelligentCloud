package com.yumik.absintelligentcloud.logic.network

import android.util.Log
import com.yumik.absintelligentcloud.logic.network.body.*
import com.yumik.absintelligentcloud.logic.network.service.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Network {

    private const val TAG = "Network"

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private val loginService = ServiceCreator.createNoCache(LoginService::class.java)
    suspend fun login(loginBody: LoginBody) = loginService.login(loginBody).await()

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private val getAreaService = ServiceCreator.create(AreaService::class.java)
    suspend fun getAreaList(token: String) = getAreaService.getAreaList(token).await()

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private val updatePasswordService =
        ServiceCreator.createNoCache(UpdatePasswordService::class.java)

    suspend fun updatePassword(updatePasswordBody: UpdatePasswordBody, token: String) =
        updatePasswordService.updatePassword(updatePasswordBody.password, token).await()

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private val deviceService = ServiceCreator.createNoCache(DeviceService::class.java)
    suspend fun getDeviceInfo(deviceInfoBody: DeviceInfoBody, token: String) =
        deviceService.getDeviceInfo(deviceInfoBody.deviceId, token).await()

    suspend fun getStatusDeviceList(statusDeviceListBody: StatusDeviceListBody, token: String) =
        deviceService.getStatusDeviceList(statusDeviceListBody, token).await()

    suspend fun getDeviceList(deviceListBody: DeviceListBody, token: String) =
        deviceService.getDeviceList(deviceListBody, token).await()

    suspend fun deleteDevice(deviceId: String, token: String) =
        deviceService.deleteDevice(deviceId, token).await()

    suspend fun addDevice(addDeviceBody: AddDeviceBody, token: String) =
        deviceService.addDevice(addDeviceBody, token).await()

    suspend fun updateDevice(addDeviceBody: AddDeviceBody, token: String) =
        deviceService.updateDevice(addDeviceBody, token).await()

    suspend fun updateFaultDevice(deviceId: String, token: String) =
        deviceService.updateFaultDevice(deviceId, token).await()

    suspend fun getHistoryList(historyListBody: HistoryListBody, token: String) =
        deviceService.getHistoryList(historyListBody, token).await()

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    Log.d(TAG, response.toString())
                    val body = response.body()
                    when (body) {
                        null -> {
                            val errorBody = response.errorBody()
                            if (errorBody == null) {
                                continuation.resumeWithException(RuntimeException("empty response"))
                            } else {
                                continuation.resumeWithException(RuntimeException(errorBody.string()))
                            }

                        }
                        else -> {
                            continuation.resume(body)
                        }
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}