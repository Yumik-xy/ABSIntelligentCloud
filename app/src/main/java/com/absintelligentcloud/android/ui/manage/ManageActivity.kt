package com.absintelligentcloud.android.ui.manage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.DeviceResponse
import com.absintelligentcloud.android.logic.model.StatusDeviceBody
import com.absintelligentcloud.android.logic.util.OnLoadMoreListener
import com.absintelligentcloud.android.ui.area.AreaFragment
import com.absintelligentcloud.android.ui.detail.DetailActivity
import com.absintelligentcloud.android.ui.device.DeviceActivity
import com.absintelligentcloud.android.ui.device.DeviceAdapter
import kotlinx.android.synthetic.main.activity_device.*
import kotlinx.android.synthetic.main.activity_manage.*
import kotlinx.android.synthetic.main.activity_manage.toolbar
import kotlinx.android.synthetic.main.fragment_area.*
import java.text.SimpleDateFormat
import java.util.*


class ManageActivity : AppCompatActivity() {

    companion object {
        const val FUN_ADD = 2
    }

    private val viewModel by lazy { ViewModelProvider(this).get(ManageViewModel::class.java) }

    private lateinit var areaFrag: AreaFragment
    private lateinit var adapter: ManageAdapter

    private var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        setSupportActionBar(toolbar)

        areaFrag = areaFragment as AreaFragment

        val layoutManager = LinearLayoutManager(this)
        deviceAlarmList.layoutManager = layoutManager
        adapter = ManageAdapter(this, viewModel.deviceList)
        deviceAlarmList.adapter = adapter

        searchDevice.setOnClickListener {
            val intent = Intent(this, DeviceActivity::class.java)
            startActivity(intent)
        }

        viewModel.deviceLiveData.observe(this, { result ->
            val devices = result.getOrNull()
            Log.d("ManageActivity", result.toString())
            if (devices != null) {
                if (devices.isNotEmpty()) {
                    if (page == 1) {
                        viewModel.deviceList.clear()
                        deviceAlarmListEmpty.visibility = View.GONE
                    }
                    viewModel.deviceList.addAll(devices)
                    adapter.notifyDataSetChanged()
                } else if (devices.isEmpty() && page != 1) {
                    page = 1.coerceAtLeast(page - 1)
                    Toast.makeText(this, "没有更多的设备了", Toast.LENGTH_SHORT).show()
                } else {
                    deviceAlarmListEmpty.visibility = View.VISIBLE
                }
            } else {
                page = 1.coerceAtLeast(page - 1)
                Toast.makeText(this, "无法获取到数据", Toast.LENGTH_SHORT).show()
            }
            swipeRefresh.isRefreshing = false
        })


        swipeRefresh.setColorSchemeResources(R.color.blue)
        swipeRefresh.setOnRefreshListener {
            page = 1
            getStatusDevices()
        }

        areaFrag.viewModel.areaNow.observe(this, { result ->
            page = 1
            viewModel.getStatusDevices(StatusDeviceBody(page, result.areaId))
        })


        deviceAlarmList.addOnScrollListener(object : OnLoadMoreListener() {
            override fun onLoadMore() {
                page += 1
                getStatusDevices()
            }
        })

        addDevice.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("function", FUN_ADD)
            this.startActivity(intent)
        }

        history.setOnClickListener {

        }
    }

    private fun getStatusDevices() {
        val areaNow = areaFrag.viewModel.areaNow.value
        if (areaNow != null) {
            viewModel.getStatusDevices(StatusDeviceBody(page, areaNow.areaId))
        }
    }
}