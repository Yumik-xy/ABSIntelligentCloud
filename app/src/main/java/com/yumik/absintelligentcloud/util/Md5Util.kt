package com.yumik.absintelligentcloud.util

import java.security.MessageDigest

object Md5Util {
    fun String.getMD5(): String {
        val md5: MessageDigest = MessageDigest.getInstance("MD5")
        md5.update(this.toByteArray())
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
}