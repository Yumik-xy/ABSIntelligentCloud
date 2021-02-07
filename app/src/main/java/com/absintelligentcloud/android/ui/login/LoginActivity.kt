package com.absintelligentcloud.android.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.ui.main.MainActivity
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.LoginBody
import com.absintelligentcloud.android.logic.util.getMD5
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (viewModel.username.isEmpty()) {
            viewModel.username = intent.getStringExtra("username") ?: ""
        }
        if (viewModel.password.isEmpty()) {
            viewModel.password = intent.getStringExtra("password") ?: ""
        }

        loginNormalBtn.setOnClickListener {
            val loginUserName = loginNameEdit.text.toString()
            val loginPassword = getMD5(loginPasswordEdit.text.toString())
            Log.d("LoginActivity", "$loginUserName|$loginPassword")
            // TODO 加密文本
            if (loginUserName.isNotEmpty() && loginPassword.isNotEmpty()) {
                viewModel.loginNormal(LoginBody(loginUserName, loginPassword))
            } else {
                Toast.makeText(this, "请输入账号和密码", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginLiveData.observe(this) { result ->
            val login = result.getOrNull()
            Log.d("LoginActivity", login.toString())
            if (login != null) {
//                ABSIntelligentCloudApplication.token = login.accessToken
//                ABSIntelligentCloudApplication.role = login.role
                viewModel.saveToken(login.accessToken)
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