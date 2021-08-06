package com.yumik.absintelligentcloud.logic.network.body

import android.webkit.DownloadListener
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.*


class DownloadResponseBody(
    private val responseBody: ResponseBody,
    private val listener: DownloadListener
) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    override fun contentLength(): Long = responseBody.contentLength()

    override fun contentType(): MediaType? = responseBody.contentType()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = getSource(responseBody.source()).buffer();
        }
        return bufferedSource!!
    }

    private fun getSource(source: Source): Source {
        return object : ForwardingSource(source) {
            var downloadBytes = 0L

            override fun read(sink: Buffer, byteCount: Long): Long {
                val singleRead = super.read(sink, byteCount)
                if (-1L != singleRead) {
                    downloadBytes += singleRead
                }
                return singleRead
            }
        }
    }
}

class DownloadInterceptor(private val listener: DownloadListener) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())
        return originalResponse.newBuilder()
            .body(DownloadResponseBody(originalResponse.body!!, listener))
            .build()
    }
}