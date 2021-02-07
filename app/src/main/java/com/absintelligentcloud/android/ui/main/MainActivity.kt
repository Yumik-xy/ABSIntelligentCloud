package com.absintelligentcloud.android.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.ui.area.AreaViewModel
import com.absintelligentcloud.android.ui.login.LoginActivity
import com.absintelligentcloud.android.ui.manage.ManageActivity

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (viewModel.isTokenSaved()) {
            ABSIntelligentCloudApplication.token = viewModel.getSavedToken()
            val intent = Intent(this, ManageActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

}