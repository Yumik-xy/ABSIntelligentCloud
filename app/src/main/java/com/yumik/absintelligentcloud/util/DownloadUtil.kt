package com.yumik.absintelligentcloud.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.yumik.absintelligentcloud.logic.network.service.DownloadService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.util.concurrent.Executors


class DownloadUtil {

    companion object {
        private const val BUFFER_SIZE = 1024 * 4
        private const val PER_INTERFACE = 500
        private const val TAG = "DownloadUtil"
    }

    private var call: Call<ResponseBody>? = null

    fun cancelCall() {
        call?.cancel()
    }

    fun download(url: String, token: String, path: String, downloadListener: DownloadListener) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dl.hdslb.com/mobile/latest/")
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .build()
        try {
            val service = retrofit.create(DownloadService::class.java)
            call = service.downloadFile(url, token)
            call!!.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    writeResponseToDisk(path, response, downloadListener)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    downloadListener.onFail(t.message)
                }

            })
        } catch (e: Exception) {
            downloadListener.onFail(e.message)
            e.printStackTrace()
        }

    }

    private fun writeResponseToDisk(
        path: String,
        response: Response<ResponseBody>,
        downloadListener: DownloadListener
    ) {
        //从response获取输入流以及总大小
        writeFileFromIS(
            File(path),
            response.body()!!.byteStream(),
            response.body()!!.contentLength(),
            downloadListener
        )
    }

    private fun writeFileFromIS(
        file: File,
        inputStream: InputStream,
        totalLength: Long,
        downloadListener: DownloadListener
    ) {
        downloadListener.onStart()
        if (!file.exists()) {
            if (file.parentFile?.exists() == false) {
                file.parentFile?.mkdir()
            }
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                downloadListener.onFail(e.message)
            }
        }

        var outputStream: OutputStream? = null
        var currentLength = 0L
        try {
            outputStream = BufferedOutputStream(FileOutputStream(file))
            val data = ByteArray(BUFFER_SIZE)
            var len: Int

            var beforeTime = System.currentTimeMillis()
            var oneLength = 0

            while (inputStream.read(data, 0, BUFFER_SIZE).also { len = it } != -1) {
                outputStream.write(data, 0, len)
                currentLength += len
                val currentTime = System.currentTimeMillis()
                if (currentTime - beforeTime > PER_INTERFACE) {
                    beforeTime = currentTime
                    downloadListener.onProgress(
                        currentLength,
                        totalLength,
                        oneLength * (1000F / PER_INTERFACE)
                    )
                    oneLength = 0
                } else {
                    oneLength += len
                }
            }
            downloadListener.onFinish(file.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
            downloadListener.onFail(e.message)
        } finally {
            try {
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun installApp(context: Context, fileProvider: String, apkPath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                fileProvider, File(apkPath)
            )
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(
                Uri.fromFile(File(apkPath)),
                "application/vnd.android.package-archive"
            )
        }
        context.startActivity(intent)
    }

    fun getFileMD5(file: File): String? {
        if (!file.isFile) {
            return null
        }
        val buffer = ByteArray(1024)
        var len: Int
        val digest = MessageDigest.getInstance("MD5")
        val fileInputStream = FileInputStream(file)
        while (fileInputStream.read(buffer, 0, 1024).also { len = it } != -1) {
            digest.update(buffer, 0, len)
        }
        fileInputStream.close()
        val bigInt = BigInteger(1, digest.digest())
        val md5 = bigInt.toString(16)
        Log.d(TAG,"md5:$md5")
        return md5
    }

    interface DownloadListener {
        fun onStart() //下载开始

        /**
         * @param currentLength 已下载大小 B
         * @param totalLength 总大小 B
         * @param currentSpeed 速度 B/s
         */
        fun onProgress(currentLength: Long, totalLength: Long, currentSpeed: Float) //下载进度
        fun onFinish(path: String) //下载完成
        fun onFail(errorInfo: String?) //下载失败
    }
}