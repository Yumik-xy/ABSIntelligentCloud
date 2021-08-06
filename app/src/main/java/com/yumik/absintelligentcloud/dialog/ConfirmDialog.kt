package com.yumik.absintelligentcloud.dialog

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.DialogConfirmBinding
import com.yumik.absintelligentcloud.util.setOnUnShakeClickListener

class ConfirmDialog(
    private val activity: Activity,
    private val title: String,
    private val subTitle: String,
    private val confirmClick: () -> Unit,
) {

    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogConfirmBinding

    private var isInit = false

    private fun initDialog() {
        isInit = true
        binding = DialogConfirmBinding.inflate(activity.layoutInflater)
        val view = binding.root
        binding.title.text = title
        binding.subTitle.text = subTitle
        binding.confirmButton.setOnUnShakeClickListener {
            dismissDialog()
            confirmClick()
        }
        binding.cancelButton.setOnUnShakeClickListener {
            dismissDialog()
        }
        dialog = AlertDialog.Builder(activity, R.style.dialog)
            .setView(view)
            .setCancelable(true)
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