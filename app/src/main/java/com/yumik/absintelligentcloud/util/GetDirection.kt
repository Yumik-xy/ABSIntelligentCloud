package com.yumik.absintelligentcloud.util

import android.content.Context
import android.os.Environment

object GetDirection {
    fun getDiskCacheDir(context: Context): String {
        return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            || !Environment.isExternalStorageRemovable()
        ) {
            context.externalCacheDir!!.path
        } else {
            context.cacheDir.path
        } + "/"
    }
}