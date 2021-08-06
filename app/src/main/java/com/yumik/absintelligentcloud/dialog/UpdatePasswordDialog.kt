package com.yumik.absintelligentcloud.dialog

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.DialogUpdatePasswordBinding
import com.yumik.absintelligentcloud.util.Md5Util.getMD5
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar
import com.yumik.absintelligentcloud.util.setOnUnShakeClickListener

class UpdatePasswordDialog(
    private val activity: Activity,
) {

    private lateinit var dialog: AlertDialog
    lateinit var binding: DialogUpdatePasswordBinding

    private var isInit = false

    private fun initDialog() {
        isInit = true
        binding = DialogUpdatePasswordBinding.inflate(activity.layoutInflater)
        val view = binding.root
        binding.confirmButton.setOnUnShakeClickListener {
            when {
                binding.oldPassword.text.isEmpty() ->
                    binding.container.showMySnackbar("请输入旧密码", R.color.secondary_yellow)
                binding.newPassword.text.isEmpty() ->
                    binding.container.showMySnackbar("请输入新密码", R.color.secondary_yellow)
                binding.checkPassword.text.isEmpty() ->
                    binding.container.showMySnackbar("请再次输入新密码", R.color.secondary_yellow)
                binding.checkPassword.text.toString() != binding.newPassword.text.toString() ->
                    binding.container.showMySnackbar("两次密码必须一致", R.color.secondary_yellow)
                else -> {
                    listener?.confirmClick(
                        binding.oldPassword.text.toString().getMD5(),
                        binding.newPassword.text.toString().getMD5()
                    )
                }
            }
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

    private var listener: OnConfirmListener? = null

    interface OnConfirmListener {
        fun confirmClick(oldPassword: String, newPassword: String)
    }

    fun setOnConfirmListener(listener: OnConfirmListener) {
        this.listener = listener
    }
}