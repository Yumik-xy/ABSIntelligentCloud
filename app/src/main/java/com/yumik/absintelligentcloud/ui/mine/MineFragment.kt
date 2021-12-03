package com.yumik.absintelligentcloud.ui.mine

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.yumik.absintelligentcloud.Application
import com.yumik.absintelligentcloud.MainActivity
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.FragmentMineBinding
import com.yumik.absintelligentcloud.dialog.ConfirmDialog
import com.yumik.absintelligentcloud.dialog.UpdatePasswordDialog
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.util.ApkVersionCodeUtils.getVerName
import com.yumik.absintelligentcloud.util.BaseFragment
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar
import com.yumik.absintelligentcloud.util.setOnUnShakeClickListener

class MineFragment : BaseFragment<MineViewModel, FragmentMineBinding>() {

    private var updatePasswordDialog: UpdatePasswordDialog? = null
    private lateinit var mainActivity: MainActivity

    override fun initViewAndData() {
        mainActivity = activity as MainActivity

        binding.versionBadge.visibility = if (Application.needUpdate) View.VISIBLE else View.GONE

        binding.versionName.text = getVerName(requireContext())

        binding.checkNew.setOnUnShakeClickListener {
            if (Application.needUpdate) {
                mainActivity.viewModel.checkUpdate()
            } else {
                binding.container.showMySnackbar("现在已经是最新版本了")
            }
        }

        binding.changePassword.setOnUnShakeClickListener {
            updatePasswordDialog = UpdatePasswordDialog(requireActivity())
            updatePasswordDialog?.apply {
                setOnConfirmListener(object :
                    UpdatePasswordDialog.OnConfirmListener {
                    override fun confirmClick(oldPassword: String, newPassword: String) {
                        mainActivity.dialog.showDialog()
                        viewModel.updatePassword(newPassword, mainActivity.accessToken)
                    }
                })
                showDialog()
            }
        }

        binding.logout.setOnUnShakeClickListener {
            val confirmExit = ConfirmDialog(requireActivity(), "退出登录", "请确认是否“退出登录”") {
                val intent = Intent().setAction(Application.BROAD_LOG_OUT)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
            }
            confirmExit.showDialog()
        }
    }

    override fun initLiveData() {
        viewModel.updatePasswordLiveData.observe(viewLifecycleOwner, {
            mainActivity.dialog.dismissDialog()
            if (it.code == Repository.ApiException.CODE_SUCCESS) {
                updatePasswordDialog?.dismissDialog()
                binding.container.showMySnackbar(it.message)
            } else {
                updatePasswordDialog?.binding?.container?.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        })
    }
}