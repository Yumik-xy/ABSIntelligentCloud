package com.absintelligentcloud.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.absintelligentcloud.android.ui.manage.ManageActivity

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

//        val intent = Intent(this, ManageActivity::class.java)
//        startActivity(intent)
//        finish()

    }
}