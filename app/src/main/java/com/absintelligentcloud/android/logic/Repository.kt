package com.absintelligentcloud.android.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.absintelligentcloud.android.logic.model.AreaResponse
import com.absintelligentcloud.android.logic.model.LoginUserBody
import com.absintelligentcloud.android.logic.network.ABSIntelligentCloudNetwork
import kotlinx.coroutines.Dispatchers
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext


object Repository {
    fun searchDevices(query: String) = fire(Dispatchers.IO) {
        val deviceResponse = ABSIntelligentCloudNetwork.searchDevices(query)
        if (deviceResponse.status == "ok") {
            val devices = deviceResponse.devices
            Result.success(devices)
        } else {
            Result.failure(RuntimeException("response status is ${deviceResponse.status}."))
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