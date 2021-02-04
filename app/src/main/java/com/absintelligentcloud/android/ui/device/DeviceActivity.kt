package com.absintelligentcloud.android.ui.device

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.DeviceBody
import com.absintelligentcloud.android.ui.area.AreaFragment
import kotlinx.android.synthetic.main.activity_device.*

class DeviceActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(DeviceViewModel::class.java) }

    private lateinit var adapter: DeviceAdapter

    private var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_search)
        }

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = DeviceAdapter(this, viewModel.deviceList)
        recyclerView.adapter = adapter
        searchBtn.setOnClickListener {
            searchDevice()
        }

        clearBtn.setOnClickListener {
            absTypeEdit.text.clear()
            deviceIdEdit.text.clear()
            userNameEdit.text.clear()
            contactNumberEdit.text.clear()
            agentNameEdit.text.clear()
            tireBrandEdit.text.clear()
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
            Log.d("DeviceFragment", result.toString())
            val devices = result.getOrNull()
            if (devices != null && devices.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                bgImageView.visibility = View.GONE
                viewModel.deviceList.clear()
                viewModel.deviceList.addAll(devices)
                adapter.notifyDataSetChanged()
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.deviceList.clear()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "查无此设备", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    private fun searchDevice() {
        val absType = absTypeEdit.text.toString()
        val deviceId = deviceIdEdit.text.toString()
        val areaFrag = areaChoiceFragment as AreaFragment
        val areaId = areaFrag.areaId
        val userName = userNameEdit.text.toString()
        val contactNumber = contactNumberEdit.text.toString()
        val agentName = agentNameEdit.text.toString()
        val tireBrand = tireBrandEdit.text.toString()
        page = 1
        val deviceBody =
            DeviceBody(
                absType,
                deviceId,
                areaId,
                userName,
                contactNumber,
                agentName,
                tireBrand,
                page
            )
        viewModel.searchDevices(deviceBody)
        drawerLayout.closeDrawer(GravityCompat.START)
    }
}