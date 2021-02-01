package com.absintelligentcloud.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.absintelligentcloud.android.logic.model.AreaResponse
import com.absintelligentcloud.android.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.device_alarm_item.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        if (ABSIntelligentCloudApplication.token == "" || ABSIntelligentCloudApplication.role == -1) {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//            Log.d("MainActivity", "Empty")
//        }
    }
}