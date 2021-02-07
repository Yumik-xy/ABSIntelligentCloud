package com.absintelligentcloud.android.logic.util

import android.widget.Spinner
import java.security.MessageDigest

fun setSpinnerItemSelectedByValue(spinner: Spinner, item: Any) {
    val adapter = spinner.adapter
    for (i in 0 until adapter.count) {
        if (spinner.getItemAtPosition(i) == item) {
            spinner.setSelection(i, true)
            break
        }
    }
}

fun getMD5(string: String): String {
    val md5: MessageDigest = MessageDigest.getInstance("MD5")
    md5.update(string.toByteArray())
    val m: ByteArray = md5.digest() //加密
    return getString(m)
}

private fun getString(b: ByteArray): String {
    val sb = StringBuffer()
    for (i in b.indices) {
        var temp = b[i].toInt()
        if (temp < 0) temp += 256
        if (temp < 16) sb.append("0")
        sb.append(Integer.toHexString(temp))
    }
    return sb.toString().substring(8, 24);
}
