package com.yumik.absintelligentcloud.dialog

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.DialogProgressBarVBinding

class LoadingDialog(private val activity: Activity, private val loadingText: String = "网络请求中……") {

    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogProgressBarVBinding

    private var isInit = false

    private fun initDialog() {
        isInit = true
        binding = DialogProgressBarVBinding.inflate(activity.layoutInflater)
        val view = binding.root
        binding.loadingText.text = loadingText
        dialog = AlertDialog.Builder(activity, R.style.dialog)
            .setView(view)
            .setCancelable(false)
            .create()
    }

    fun showDialog() {
        if (!isInit) {
            initDialog()
        }
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismissDialog() {
        if (!isInit) {
            initDialog()
        }
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}