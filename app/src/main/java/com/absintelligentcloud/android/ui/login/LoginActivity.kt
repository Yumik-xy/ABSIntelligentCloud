package com.absintelligentcloud.android.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.MainActivity
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.LoginUserBody
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (viewModel.user.isEmpty()) {
            viewModel.user = intent.getStringExtra("user") ?: ""
        }
        if (viewModel.password.isEmpty()) {
            viewModel.password = intent.getStringExtra("password") ?: ""
        }

        loginNormalBtn.setOnClickListener {
            val loginName = loginNameEdit.text.toString()
            val loginPassword = loginPasswordEdit.text.toString()
            // TODO 加密文本
            if (loginName.isNotEmpty() && loginPassword.isNotEmpty()) {
                viewModel.loginNormal(LoginUserBody(loginName, loginPassword))
            } else {
                Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginLiveData.observe(this) { result ->
            val login = result.getOrNull()
            if (login != null) {
                ABSIntelligentCloudApplication.token = login.token
                ABSIntelligentCloudApplication.role = login.role
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "账号或密码错误", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
}