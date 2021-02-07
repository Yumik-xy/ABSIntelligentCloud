package com.absintelligentcloud.android.logic.network

import android.util.Log
import com.absintelligentcloud.android.logic.model.DetailBody
import com.absintelligentcloud.android.logic.model.DeviceBody
import com.absintelligentcloud.android.logic.model.LoginBody
import com.absintelligentcloud.android.logic.model.StatusDeviceBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object ABSIntelligentCloudNetwork {

    //    创建了接口的动态代理对象
    private val deviceService = ServiceCreator.create(DeviceService::class.java)
    private val loginService = ServiceCreator.create(LoginService::class.java)
    private val areaService = ServiceCreator.create(AreaService::class.java)
    private val detailService = ServiceCreator.create(DetailService::class.java)

    //    然后定义了 searchDevices 函数，也就是 DeviceService 接口的内容
    suspend fun searchDevices(deviceBody: DeviceBody) =
        deviceService.searchDevices(deviceBody).await()

    suspend fun getStatusDevices(statusDeviceBody: StatusDeviceBody) =
        deviceService.getStatusDevices(statusDeviceBody).await()

    suspend fun loginNormal(user: LoginBody) = loginService.loginNormal(user).await()
    suspend fun getAreaList() = areaService.getAreaList().await()

    suspend fun addDevice(device: DetailBody) = detailService.addDevice(device).await()
    suspend fun getDevice(deviceId: String) = detailService.getDevice(deviceId).await()
    suspend fun updateDevice(device: DetailBody) = detailService.updateDevice(device).await()
    suspend fun deleteDevice(deviceId: String) = detailService.deleteDevice(deviceId).await()
    suspend fun solveDevice(deviceId: String) = detailService.solveDevice(deviceId).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    Log.d("ABS", response.toString())
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null.")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}