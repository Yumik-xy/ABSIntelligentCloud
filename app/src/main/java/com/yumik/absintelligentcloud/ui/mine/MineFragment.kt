package com.yumik.absintelligentcloud.ui.mine

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.yumik.absintelligentcloud.MainActivity
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.FragmentMineBinding
import com.yumik.absintelligentcloud.logic.network.response.EmptyResponse
import com.yumik.absintelligentcloud.receiver.PackageAddedReceiver
import com.yumik.absintelligentcloud.ui.filter.FilterActivity
import com.yumik.absintelligentcloud.dialog.ConfirmDialog
import com.yumik.absintelligentcloud.dialog.DownloadDialog
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar
import com.yumik.absintelligentcloud.dialog.UpdatePasswordDialog
import com.yumik.absintelligentcloud.util.ApkVersionCodeUtils.getVerName
import com.yumik.absintelligentcloud.util.ApkVersionCodeUtils.getVersionCode
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
    private lateinit var broadcastReceiver: BroadcastReceiver
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

        // 初始化监听
        // initBroadcast()

        // 处理viewModel回调
        initViewModel()

        // 初始化组件
        initView()
    }

    private fun initBroadcast() {

        broadcastReceiver = PackageAddedReceiver()

        // broadcastReceiver = object : BroadcastReceiver() {
        //     override fun onReceive(context: Context?, intent: Intent?) {
        //         Log.d(TAG, intent.toString())
        //         when (intent?.action) {
        //             Intent.ACTION_PACKAGE_ADDED -> {
        //                 val packageName = intent.data?.schemeSpecificPart
        //                 "安装成功$packageName".showToast(requireContext())
        //             }
        //             Intent.ACTION_PACKAGE_REMOVED -> {
        //                 val packageName = intent.data?.schemeSpecificPart
        //                 "卸载成功$packageName".showToast(requireContext())
        //             }
        //             Intent.ACTION_PACKAGE_REPLACED -> {
        //                 val packageName = intent.data?.schemeSpecificPart
        //                 "替换成功$packageName".showToast(requireContext())
        //             }
        //         }
        //     }
        // }

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, intentFilter)
        needDoList.add {
            LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(broadcastReceiver)
        }
    }

    private fun initViewModel() {
        viewModel.updatePasswordLiveData.observe(viewLifecycleOwner, {
            mainActivity.dialog.dismissDialog()
            val result = it.getOrNull()
            if (result != null) {
                updatePasswordDialog?.dismissDialog()
                binding.container.showMySnackbar(result.message)
            } else {
                try {
                    it.onFailure { throwable ->
                        Log.d(FilterActivity.TAG, throwable.message.toString())
                        val errorResponse =
                            Gson().fromJson(throwable.message, EmptyResponse::class.java)
                        updatePasswordDialog?.binding?.container?.showMySnackbar(
                            errorResponse.message,
                            R.color.secondary_red
                        )
                    }
                } catch (e: Exception) {
                    updatePasswordDialog?.binding?.container?.showMySnackbar(
                        "网络异常！请检查网络连接",
                        R.color.secondary_yellow
                    )
                }
            }
        })

        viewModel.versionName.observe(viewLifecycleOwner, {
            binding.versionName.text = it
        })

        viewModel.checkUpdateLiveData.observe(viewLifecycleOwner, {
            val result = it.getOrNull()
            if (result != null) {
                val data = result.data
                if (getVersionCode(requireContext()) < data.versionCode) {
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