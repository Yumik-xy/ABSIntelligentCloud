package com.yumik.absintelligentcloud.logic.network

import com.yumik.absintelligentcloud.Application
import com.yumik.absintelligentcloud.util.ConnectCheckUtil
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object ServiceCreator {

    private const val BASE_URL = "http://82.156.209.212:55555/"
    private const val CACHE_SIZE = 1024 * 1024 * 10L // 缓存大小
    private const val MAX_STALE = 60 * 60 * 1 // 离线时缓存保存
    private const val MAX_AGE = 15 // 有网时缓存保存
    private const val CONNECT_TIME_OUT = 10L // 请求超时

    // 内部缓存路径
    private val httpCacheDirectory = File(Application.context.cacheDir, "responses")

    // 外部缓存路径
    private val httpExternalCacheDirectory = File(Application.context.externalCacheDir, "responses")

    // 日志拦截器
    private val logging = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    // 读取缓存
    private val baseInterceptor = Interceptor { chain ->
        var request = chain.request()
        if (!ConnectCheckUtil.isNetworkAvailable(Application.context)) {
            val tempCacheControl = CacheControl.Builder()
                .onlyIfCached()
                .maxStale(MAX_STALE, TimeUnit.SECONDS)
                .build()
            request = request.newBuilder()
                .cacheControl(tempCacheControl)
                .build()
        }
        chain.proceed(request)
    }


    // 网络请求进行缓存前预处理
    private val networkInterceptor = Interceptor { chain ->
        val request = chain.request()
        val originalResponse = chain.proceed(request)
        originalResponse.newBuilder()
            // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
            .removeHeader("Pragma")
            .removeHeader("Cache-Control")
            .header("Cache-Control", "public, max-age=$MAX_AGE")
            .build()
    }

    private val cache = Cache(httpCacheDirectory, CACHE_SIZE) // 使用外部缓存

    private val okHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .addInterceptor(logging)
        .addInterceptor(baseInterceptor)
        .addNetworkInterceptor(networkInterceptor)
        .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitNoCache = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    fun <T> createNoCache(serviceClass: Class<T>): T = retrofitNoCache.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)
}