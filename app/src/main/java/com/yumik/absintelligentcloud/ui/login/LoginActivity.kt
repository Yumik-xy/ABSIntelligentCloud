package com.yumik.absintelligentcloud.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.yumik.absintelligentcloud.MainActivity
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.ActivityLoginBinding
import com.yumik.absintelligentcloud.dialog.LoadingDialog
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.logic.network.body.LoginBody
import com.yumik.absintelligentcloud.ui.BaseActivity
import com.yumik.absintelligentcloud.util.Md5Util.getMD5
import com.yumik.absintelligentcloud.util.SPUtil
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar

class LoginActivity : BaseActivity() {

    companion object {
        const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private val dialog = LoadingDialog(this)

    private var role by SPUtil(this, "role", -1)
    private var accessToken by SPUtil(this, "accessToken", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 绑定viewModel
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // 修改状态栏颜色
        // url: https://stackoverflow.com/questions/65423778/system-ui-flag-light-status-bar-and-flag-translucent-status-is-deprecated
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = window
            val decorView: View = window.decorView
            val wic = WindowInsetsControllerCompat(window, decorView)
            wic.isAppearanceLightStatusBars = true
            // window.statusBarColor = Color.WHITE
        }

        // 处理viewModel回调
        initViewModel()

        // 处理组件
        initView()
    }

    private fun initView() {
        binding.login.setOnClickListener {
            when {
                binding.user.text.isEmpty() -> {
                    binding.container.showMySnackbar("用户名不能为空！", R.color.secondary_red)
                }
                binding.password.text.isEmpty() -> {
                    binding.container.showMySnackbar("密码不能为空！", R.color.secondary_red)
                }
                else -> {
                    val loginBody = LoginBody(
                        binding.user.text.toString(),
                        binding.password.text.toString().getMD5()
                    )
                    Log.d(TAG, loginBody.toString())
                    dialog.showDialog()
                    viewModel.login(loginBody)
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel.loginLiveData.observe(this, {
            dialog.dismissDialog()
            if ((it.code == Repository.ApiException.CODE_SUCCESS || it.code == 0) && it.data != null) {
                binding.container.showMySnackbar("登录成功！")
                Thread {
                    Thread.sleep(1000)
                    val data = it.data
                    role = data.role
                    accessToken = data.accessToken
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }.start()
            } else {
                binding.container.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        })
    }
}