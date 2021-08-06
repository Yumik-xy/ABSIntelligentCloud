package com.yumik.absintelligentcloud.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.yumik.absintelligentcloud.ui.mine.MineFragment
import com.yumik.absintelligentcloud.util.TipsUtil.showToast

class PackageAddedReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "PackageAddedReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                val packageName = intent.data?.schemeSpecificPart
                "安装成功$packageName".showToast(context!!)
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                val packageName = intent.data?.schemeSpecificPart
                "卸载成功$packageName".showToast(context!!)
            }
            Intent.ACTION_PACKAGE_REPLACED -> {
                val packageName = intent.data?.schemeSpecificPart
                "替换成功$packageName".showToast(context!!)
            }
        }
    }
}