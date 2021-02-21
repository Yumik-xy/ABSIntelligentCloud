package com.absintelligentcloud.android.ui.device

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.DeviceBody
import com.absintelligentcloud.android.logic.util.OnLoadMoreListener
import com.absintelligentcloud.android.ui.area.AreaFragment
import kotlinx.android.synthetic.main.activity_device.*
import kotlinx.android.synthetic.main.activity_device.absTypeEdit
import kotlinx.android.synthetic.main.activity_device.agentNameEdit
import kotlinx.android.synthetic.main.activity_device.areaChoiceFragment
import kotlinx.android.synthetic.main.activity_device.contactNumberEdit
import kotlinx.android.synthetic.main.activity_device.deviceIdEdit
import kotlinx.android.synthetic.main.activity_device.productionDateEdit
import kotlinx.android.synthetic.main.activity_device.tireBrandEdit
import kotlinx.android.synthetic.main.activity_device.toolbar
import kotlinx.android.synthetic.main.activity_device.userNameEdit
import java.util.*


class DeviceActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(DeviceViewModel::class.java) }

    private lateinit var adapter: DeviceAdapter

    private var page = 1

    private var firstStart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        setSupportActionBar(toolbar)
//        supportActionBar?.let {
//            it.setDisplayHomeAsUpEnabled(true)
//            it.setHomeAsUpIndicator(R.drawable.ic_search)
//        }

        val layoutManager = LinearLayoutManager(this)
//        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        adapter = DeviceAdapter(this, viewModel.deviceList)
        recyclerView.adapter = adapter
        searchBtn.setOnClickListener {
            page = 1
            searchDevice()
        }

        clearBtn.setOnClickListener {
            absTypeEdit.text.clear()
            deviceIdEdit.text.clear()
            userNameEdit.text.clear()
            contactNumberEdit.text.clear()
            agentNameEdit.text.clear()
            tireBrandEdit.text.clear()
            productionDateEdit.text = ""
            page = 1
        }

        drawerLayout.addDrawerListener(object : SimpleDrawerListener() {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        })

        viewModel.deviceLiveData.observe(this, { result ->
            Log.d("DeviceActivity", result.toString())
            val devices = result.getOrNull()
            if (devices != null && devices.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                bgDeviceImageView.visibility = View.GONE
                if (page == 1) {
                    viewModel.deviceList.clear()
                }
                viewModel.deviceList.addAll(devices)
                adapter.notifyDataSetChanged()
            } else if (page != 1) {
                page = 1.coerceAtLeast(page - 1)
//                Toast.makeText(this, "没有更多的设备了", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            } else {
                recyclerView.visibility = View.GONE
                bgDeviceImageView.visibility = View.VISIBLE
                viewModel.deviceList.clear()
                adapter.notifyDataSetChanged()
                page = 1.coerceAtLeast(page - 1)
                Toast.makeText(this, "查无此设备", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })

        recyclerView.addOnScrollListener(object : OnLoadMoreListener() {
            override fun onLoadMore() {
                page += 1
                Log.d("recyclerView", "addOnScrollListener")
                searchDevice()
            }
        })

        productionDateEdit.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this, { view, year, month, dayOfMonth ->
                    @SuppressLint("SetTextI18n")
                    productionDateEdit.text = "$year-${month + 1}-$dayOfMonth"
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        showSearchBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        backDeviceBtn.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!firstStart) {
            firstStart = true
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun searchDevice() {
        val absType = absTypeEdit.text.toString()
        val deviceId = deviceIdEdit.text.toString()
        val areaFrag = areaChoiceFragment as AreaFragment
        val areaId = areaFrag.areaId
        val userName = userNameEdit.text.toString()
//        val productionDateText = productionDateEdit.text
//        val dataList = productionDateText.split("-")
//        val productionDate = GregorianCalendar(
//            dataList[0].toInt(),
//            dataList[1].toInt() - 1,
//            dataList[2].toInt()
//        ).timeInMillis
        val contactNumber = contactNumberEdit.text.toString()
        val agentName = agentNameEdit.text.toString()
        val tireBrand = tireBrandEdit.text.toString()
        val deviceBody =
            DeviceBody(
                absType,
                deviceId,
                areaId,
                userName,
//                productionDate,
                contactNumber,
                agentName,
                tireBrand,
                page
            )
        viewModel.searchDevices(deviceBody)
        drawerLayout.closeDrawer(GravityCompat.END)
    }
}