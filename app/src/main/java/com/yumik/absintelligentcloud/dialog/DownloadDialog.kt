package com.yumik.absintelligentcloud.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.DialogDownloadBinding
import com.yumik.absintelligentcloud.util.DownloadUtil
import com.yumik.absintelligentcloud.util.GetDirection.getDiskCacheDir
import com.yumik.absintelligentcloud.util.SPUtil
import com.yumik.absintelligentcloud.util.TipsUtil.showToast
import com.yumik.absintelligentcloud.util.setOnUnShakeClickListener
import java.io.File
import kotlin.math.ceil

class DownloadDialog(private val mContext: Context, private val url: String, private val md5: String, private val token: String) :
    DialogFragment() {

    companion object {
        private const val TAG = "DownloadDialog"
    }

    var filePath by SPUtil(
        mContext,
        "filePath",
        getDiskCacheDir(mContext) + "update.apk"
    )

    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogDownloadBinding
    private lateinit var downloadUtil: DownloadUtil

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "onCreateDialog")
        binding = DialogDownloadBinding.inflate(requireActivity().layoutInflater)
        dialog = AlertDialog.Builder(requireActivity(), R.style.dialog)
            .setView(binding.root)
            .setCancelable(false)
            .setOnKeyListener { _, keyCode, _ ->
                keyCode == KeyEvent.KEYCODE_BACK
            }
            .create()

        download()

        return dialog
    }

    private fun download() {
        downloadUtil = DownloadUtil()
        File(filePath).also { file ->
            if (file.exists()) {
                if (DownloadUtil().getFileMD5(file) == md5) {
                    DownloadUtil().installApp(
                        mContext,
                        "com.yumik.absintelligentcloud.fileProvider",
                        filePath
                    )
                    dismiss()
                    return // 如果安装文件存在且md5相等，则结束
                }
            }
        }

        downloadUtil.download(
            url,
            token,
            filePath,
            object : DownloadUtil.DownloadListener {
                override fun onStart() {
                    requireActivity().runOnUiThread {
                        binding.message.text = "正在开始下载"
                    }
                }

                override fun onProgress(
                    currentLength: Long,
                    totalLength: Long,
                    currentSpeed: Float
                ) {
                    requireActivity().runOnUiThread {
                        val progress = ceil(currentLength * 100.0 / totalLength)
                        binding.progress.progress = progress.toInt()
                        binding.progressTV.text = progress.toInt().toString() + "%"
                        binding.message.text =
                            "${currentSpeed.sizeFormat()} - ${currentLength.sizeFormat()}/${totalLength.sizeFormat()}"
                    }
                }

                override fun onFinish(path: String) {
                    requireActivity().runOnUiThread {
                        binding.message.text = "下载完成"
                        binding.progress.progress = 100
                        binding.progressTV.text = "100%"
                    }
                    Thread {
                        File(path).also { file ->
                            if (file.exists()) {
                                if (DownloadUtil().getFileMD5(file) == md5) {
                                    filePath = path
                                    DownloadUtil().installApp(
                                        mContext,
                                        "com.yumik.absintelligentcloud.fileProvider",
                                        path
                                    )
                                } else {
                                    "安装包校验失败，请重新下载".showToast(mContext)
                                    file.delete()
                                }
                            }
                        }
                        dismiss()
                    }.start()
                }

                override fun onFail(errorInfo: String?) {
                    requireActivity().runOnUiThread {
                        binding.message.text = "$errorInfo"
                    }
                }
            }
        )
    }

    private fun Long.sizeFormat(): String {
        return when {
            this < 1024 -> String.format("%fB", this)
            this < 1024 * 1024 -> String.format("%.1fKb", this / 1024.0)
            this < 1024 * 1024 * 1024 -> String.format("%.1fMb", this / 1024.0 / 1024.0)
            else -> "∞"
        }
    }

    private fun Float.sizeFormat(): String {
        return when {
            this < 1024 -> String.format("%.2fB/s", this)
            this < 1024 * 1024 -> String.format("%.2fKb/s", this / 1024.0)
            this < 1024 * 1024 * 1024 -> String.format("%.2fMb/s", this / 1024.0 / 1024.0)
            else -> "∞"
        }
    }
}