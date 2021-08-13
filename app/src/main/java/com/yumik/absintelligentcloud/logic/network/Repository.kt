package com.yumik.absintelligentcloud.logic.network

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.JsonParseException
import com.yumik.absintelligentcloud.Application
import com.yumik.absintelligentcloud.logic.network.body.*
import com.yumik.absintelligentcloud.logic.network.response.BaseResponse
import com.yumik.absintelligentcloud.logic.network.service.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object Repository {

    private const val TAG = "Network"

    suspend inline fun <T> apiCall(crossinline call: suspend CoroutineScope.() -> BaseResponse<T>): BaseResponse<T> {
        return withContext(Dispatchers.IO) {
            val res: BaseResponse<T>
            try {
                res = call()
            } catch (e: Throwable) {
                return@withContext ApiException.build(e).toResponse<T>()
            }
            when (res.code) {
                ApiException.CODE_AUTH_INVALID -> {
                    cancel()
                    // TODO: token过期，强制拉起登录页面
                    val intent = Intent().setAction(Application.BROAD_LOG_OUT)
                    LocalBroadcastManager.getInstance(Application.context).sendBroadcast(intent)
                }
            }
            return@withContext res
        }
    }

    // 网络、数据解析错误处理
    class ApiException(
        val code: Int, override val message: String, override val cause: Throwable? = null
    ) : RuntimeException(message, cause) {

        companion object {
            // 正常返回码
            const val CODE_SUCCESS = 200

            // 异常状态码
            const val CODE_NET_ERROR = 50000 // 网络异常
            const val CODE_TIMEOUT = 50001 // 连接超时
            const val CODE_JSON_PARSE_ERROR = 50002 // Json解析失败
            const val CODE_SERVER_ERROR = 50003 // 服务器错误

            const val CODE_AUTH_INVALID = 40101 // token 失效

            fun build(e: Throwable): ApiException {
                return if (e is HttpException) {
                    ApiException(CODE_NET_ERROR, "网络异常(${e.code()},${e.message()})")
                } else if (e is UnknownHostException) {
                    ApiException(CODE_NET_ERROR, "网络连接失败，请检查后再试")
                } else if (e is ConnectTimeoutException || e is SocketTimeoutException) {
                    ApiException(CODE_TIMEOUT, "请求超时，请稍后再试")
                } else if (e is IOException) {
                    ApiException(CODE_NET_ERROR, "网络异常(${e.message})")
                } else if (e is JsonParseException || e is JSONException) {
                    ApiException(CODE_JSON_PARSE_ERROR, "数据解析错误，请稍后再试")
                } else {
                    ApiException(CODE_SERVER_ERROR, "系统错误(${e.message})")
                }
            }
        }

        fun <T> toResponse(): BaseResponse<T> {
            return BaseResponse(code, message)
        }
    }

    class StateLiveData<T> : MutableLiveData<BaseResponse<T>>()
}