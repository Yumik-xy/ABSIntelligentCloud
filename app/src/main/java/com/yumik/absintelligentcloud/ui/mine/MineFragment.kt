package com.yumik.absintelligentcloud.ui.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yumik.absintelligentcloud.MainActivity
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.FragmentMineBinding
import com.yumik.absintelligentcloud.dialog.ConfirmDialog
import com.yumik.absintelligentcloud.dialog.DownloadDialog
import com.yumik.absintelligentcloud.dialog.UpdatePasswordDialog
import com.yumik.absintelligentcloud.logic.network.Network
import com.yumik.absintelligentcloud.util.ApkVersionCodeUtils.getVersionCode
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar
import com.yumik.absintelligentcloud.util.setOnUnShakeClickListener

class MineFragment : Fragment() {

    companion object {
        fun newInstance() = MineFragment()
        private const val TAG = "MineFragment"
    }

    private var _binding: FragmentMineBinding? = null
    private val binding get() = _binding!!
    private var updatePasswordDialog: UpdatePasswordDialog? = null

    private lateinit var viewModel: MineViewModel
    private lateinit var mainActivity: MainActivity

    // 销毁前做的事情
    private val needDoList = mutableListOf<() -> Unit>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMineBinding.inflate(inflater, container, false)
        needDoList.add { _binding = null }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MineViewModel::class.java)
        mainActivity = activity as MainActivity

        // 处理viewModel回调
        initViewModel()

        // 初始化组件
        initView()
    }

    private fun initViewModel() {
        viewModel.updatePasswordLiveData.observe(viewLifecycleOwner, {
            mainActivity.dialog.dismissDialog()
            if (it.code == Network.ApiException.CODE_SUCCESS) {
                updatePasswordDialog?.dismissDialog()
                binding.container.showMySnackbar(it.message)
            } else {
                updatePasswordDialog?.binding?.container?.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        })

        viewModel.versionName.observe(viewLifecycleOwner, {
            binding.versionName.text = it
        })

        viewModel.checkUpdateLiveData.observe(viewLifecycleOwner, {
            if (it.code == Network.ApiException.CODE_SUCCESS && it.data != null) {
                if (getVersionCode(requireContext()) < it.data.versionCode) {
                    binding.versionBadge.visibility = View.VISIBLE
                } else {
                    binding.versionBadge.visibility = View.GONE
                }
            }
        })
    }

    private fun initView() {
        viewModel.checkUpdate() // 只请求一次更新就好了，不是频繁更新没必要轮询
        binding.checkNew.setOnUnShakeClickListener {
            if (binding.versionBadge.visibility == View.VISIBLE) {
                val downloadDialog = DownloadDialog(
                    requireContext(),
                    "iBiliPlayer-bili.apk?t=1628079808000",
                    "3158228ba1c9b3562da5e027a4ff321d",
                    mainActivity.accessToken
                )
                downloadDialog.show(parentFragmentManager, "downloadDialog")
            } else {
                binding.container.showMySnackbar("现在已经是最新版本了！")
            }
        }

        binding.changePassword.setOnUnShakeClickListener {
            updatePasswordDialog = UpdatePasswordDialog(requireActivity())
            updatePasswordDialog?.apply {
                setOnConfirmListener(object :
                    UpdatePasswordDialog.OnConfirmListener {
                    override fun confirmClick(oldPassword: String, newPassword: String) {
                        (activity as MainActivity).apply {
                            dialog.showDialog()
                            viewModel.updatePassword(newPassword, accessToken)
                        }
                    }
                })
                showDialog()
            }
        }

        binding.logout.setOnUnShakeClickListener {
            val confirmExit = ConfirmDialog(requireActivity(), "退出登录", "请确认是否“退出登录”") {
                (activity as MainActivity).apply {
                    accessToken = ""
                    checkLogin()
                }
            }
            confirmExit.showDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        for (needDoItem in needDoList) {
            needDoItem()
        }
        needDoList.clear()
    }
}