package com.absintelligentcloud.android.ui.manage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.DeviceResponse
import com.absintelligentcloud.android.ui.device.DeviceActivity
import kotlinx.android.synthetic.main.activity_manage.*
import kotlinx.android.synthetic.main.device_alarm_list.*
import java.text.SimpleDateFormat
import java.util.*

class ManageActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(ManageViewModel::class.java) }

    private val page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        setSupportActionBar(toolbar)

        viewModel.getStatusDevices(page)

        searchDevice.setOnClickListener {
            val intent = Intent(this, DeviceActivity::class.java)
            startActivity(intent)
        }

        viewModel.deviceLiveData.observe(this, { result ->
            val devices = result.getOrNull()
            if (devices != null) {
                showStatusDevices(devices)
            } else {
                Toast.makeText(this, "无法获取到数据", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showStatusDevices(statusDevices: List<DeviceResponse.Device>) {
        deviceAlarmList.removeAllViews()


        if (statusDevices.isNotEmpty()) {
            deviceAlarmListEmpty.visibility = View.GONE
        } else {
            deviceAlarmListEmpty.visibility = View.VISIBLE
            return
        }

        for (i in statusDevices.indices) {
            val statusDevice = statusDevices[i]
            val absType = statusDevice.absType
            val agentName = statusDevice.agentName
            val contactNumber = statusDevice.contactNumber
            val deviceId = statusDevice.deviceId
            val productionDate = statusDevice.productionDate
            val tireBrand = statusDevice.tireBrand
            val userName = statusDevice.userName

            val view = LayoutInflater.from(this)
                .inflate(R.layout.device_alarm_item, deviceAlarmList, false)
            val absTypeView = view.findViewById(R.id.absType) as TextView
            val agentNameView = view.findViewById(R.id.agentName) as TextView
            val contactNumberView = view.findViewById(R.id.contactNumber) as TextView
            val deviceIdView = view.findViewById(R.id.deviceId) as TextView
            val productionDateView = view.findViewById(R.id.productionDate) as TextView
            val tireBrandView = view.findViewById(R.id.tireBrand) as TextView
            val userNameView = view.findViewById(R.id.userName) as TextView

            absTypeView.text = absType
            agentNameView.text = agentName
            contactNumberView.text = contactNumber
            deviceIdView.text = deviceId
            // TODO geshihua
            val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            productionDateView.text = simpleDataFormat.format(productionDate)
            tireBrandView.text = tireBrand
            userNameView.text = userName
            deviceAlarmList.addView(view)
        }
    }
}