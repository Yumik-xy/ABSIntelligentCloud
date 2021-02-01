package com.absintelligentcloud.android.ui.manage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ArrayAdapter
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.AreaResponse

class ManageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_manage)
    }
}