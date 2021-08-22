package com.yumik.absintelligentcloud.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.DialogDownloadBinding
import com.yumik.absintelligentcloud.logic.network.response.CheckUpdateResponse
import com.yumik.absintelligentcloud.util.DownloadUtil
import com.yumik.absintelligentcloud.util.GetDirection.getDiskCacheDir
import com.yumik.absintelligentcloud.util.SPUtil
import com.yumik.absintelligentcloud.util.TipsUtil.showToast
import com.yumik.absintelligentcloud.util.setOnUnShakeClickListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

class DownloadDialog(
    private val mContext: Context,
    private val checkUpdateResponse: CheckUpdateResponse,
) : DialogFragment() {

    companion object {
        private const val TAG = "DownloadDialog"
    }

    var filePath by SPUtil(
        mContext,
        "filePath",
        getDiskCacheDir(mContext) + checkUpdateResponse.assets[0].apkName
    )

    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogDownloadBinding
    private lateinit var downloadUtil: DownloadUtil
    private lateinit var downloadListener: DownloadUtil.DownloadListener

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

        initDownload()

        binding.cancelButton.setOnUnShakeClickListener {
            downloadUtil.cancelCall()
            dismiss()
        }

        binding.confirmButton.setOnUnShakeClickListener {
            if (binding.confirmButton.text == "更新" || binding.confirmButton.text == "重新更新") {
                binding.confirmButton.isEnabled = false
                downloadUtil.download(checkUpdateResponse.assets[0].url, filePath, downloadListener)
            } else if (binding.confirmButton.text == "安装") {
                installApp(filePath)
            }
        }

        val timeFormat = SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss", Locale.CHINA);
        val stringFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        val time = timeFormat.parse(checkUpdateResponse.time)!!
        @SuppressLint("SetTextI18n")
        binding.subTitle.text = "${stringFormat.format(time)}更新内容：\n" +
                "${checkUpdateResponse.description}\n" +
                "请尽快更新！"

        return dialog
    }

    @SuppressLint("SetTextI18n")
    private fun initDownload() {
        downloadUtil = DownloadUtil()
        Thread {
            File(filePath).also { file ->
                if (file.exists()) {
                    if (DownloadUtil().getFileLength(file) == checkUpdateResponse.assets[0].apkSize &&
                        filePath.contains(checkUpdateResponse.assets[0].apkName)
                    ) {
                        activity?.runOnUiThread {
                            binding.confirmButton.isEnabled = true
                            binding.confirmButton.text = "安装"
                            binding.message.text = "下载完成"
                            binding.progress.progress = 100
                            binding.progressTV.text = "100%"
                        }
                        return@Thread
                    }
                }
                activity?.runOnUiThread {
                    binding.confirmButton.isEnabled = true
                    binding.confirmButton.text = "更新"
                }
            }
        }.start()

        downloadListener = object : DownloadUtil.DownloadListener {
            override fun onStart() {
                activity?.runOnUiThread {
                    binding.confirmButton.text = "正在下载"
                    binding.message.text = "下载中..."
                }
            }

            override fun onProgress(
                currentLength: Long,
                totalLength: Long,
                currentSpeed: Float
            ) {
                activity?.runOnUiThread {
                    val progress = ceil(currentLength * 100.0 / totalLength)
                    binding.progress.progress = progress.toInt()
                    binding.progressTV.text = progress.toInt().toString() + "%"
                    binding.speed.text = currentSpeed.sizeFormat()
                }
            }

            override fun onFinish(path: String) {
                activity?.runOnUiThread {
                    binding.message.text = "下载完成"
                    binding.progress.progress = 100
                    binding.progressTV.text = "100%"
                    binding.confirmButton.text = "安装"
                    binding.confirmButton.isEnabled = true
                }
                installApp(path)
            }

            override fun onFail(errorInfo: String?) {
                activity?.runOnUiThread {
                    binding.message.text = "$errorInfo"
                    binding.confirmButton.text = "重新更新"
                    binding.confirmButton.isEnabled = true
                }
                Thread {
                    File(filePath).also { file ->
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                }.start()
            }
        }
    }

    private fun installApp(path: String) {
        Thread {
            File(path).also { file ->
                if (file.exists()) {
                    if (DownloadUtil().getFileLength(file) == checkUpdateResponse.assets[0].apkSize &&
                        filePath.contains(checkUpdateResponse.assets[0].apkName)
                    ) {
                        filePath = path
                        DownloadUtil().installApp(
                            mContext,
                            "com.yumik.absintelligentcloud.fileProvider",
                            path
                        )
                    } else {
                        file.delete()
                        activity?.runOnUiThread {
                            "安装包校验失败，请重新下载".showToast(requireContext())
                            binding.message.text = "等待中..."
                            binding.confirmButton.text = "重新更新"
                            binding.confirmButton.isEnabled = true
                            binding.progress.progress = 0
                            binding.progressTV.text = "0%"
                        }
                    }
                } else {
                    file.delete()
                    activity?.runOnUiThread {
                        "文件不存在，请重新下载".showToast(requireContext())
                        binding.message.text = "等待中..."
                        binding.confirmButton.text = "重新更新"
                        binding.confirmButton.isEnabled = true
                        binding.progress.progress = 0
                        binding.progressTV.text = "0%"
                    }
                }
            }
        }.start()
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