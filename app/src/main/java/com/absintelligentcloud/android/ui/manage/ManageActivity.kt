package com.absintelligentcloud.android.ui.manage

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.StatusDeviceBody
import com.absintelligentcloud.android.logic.util.GridDividerItemDecoration
import com.absintelligentcloud.android.logic.util.OnLoadMoreListener
import com.absintelligentcloud.android.logic.util.getMD5
import com.absintelligentcloud.android.ui.area.AreaFragment
import com.absintelligentcloud.android.ui.detail.DetailActivity
import com.absintelligentcloud.android.ui.device.DeviceActivity
import com.absintelligentcloud.android.ui.history.HistoryActivity
import com.absintelligentcloud.android.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_manage.*
import kotlinx.android.synthetic.main.fragment_update_password.*


class ManageActivity : AppCompatActivity() {

    companion object {
        const val FUN_ADD = 2
    }

    private val viewModel by lazy { ViewModelProvider(this).get(ManageViewModel::class.java) }

    private lateinit var areaFrag: AreaFragment
    private lateinit var adapter: ManageAdapter

    private var page = 1

    override fun onResume() {
        super.onResume()
        if (ABSIntelligentCloudApplication.needUpdate) {
            ABSIntelligentCloudApplication.needUpdate = false
            page = 1
            getStatusDevices()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        setSupportActionBar(toolbar)

        areaFrag = areaFragment as AreaFragment

        val layoutManager = LinearLayoutManager(this)
//            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        deviceAlarmList.layoutManager = layoutManager
        adapter = ManageAdapter(this, viewModel.deviceList)
        deviceAlarmList.adapter = adapter

        deviceAlarmList.addItemDecoration(GridDividerItemDecoration(this))

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
                    viewModel.deviceList.clear()
                    deviceAlarmListEmpty.visibility = View.VISIBLE
                }
            } else {
                page = 1.coerceAtLeast(page - 1)
                adapter.notifyDataSetChanged()
//                Toast.makeText(this, "无法获取到数据", Toast.LENGTH_SHORT).show()
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
            startActivity(intent)
        }

        history.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        user.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.inflate(R.menu.manage_more)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.signOut -> {
                        ABSIntelligentCloudApplication.token = ""
                        ABSIntelligentCloudApplication.role = -1
                        viewModel.removeToken()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    R.id.updatePassword -> {
                        val dialogView: View = LayoutInflater.from(this)
                            .inflate(R.layout.fragment_update_password, null)
                        val alertDialog = AlertDialog.Builder(this)
                            .setTitle("修改密码")
                            .setView(dialogView)
                            .setPositiveButton(
                                "确定", null
                            )
                            .setNegativeButton("取消") { dialog, whick ->
                                dialog.dismiss()
                            }.create()

                        alertDialog.show()
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            val updatePasswordEditText =
                                dialogView.findViewById<EditText>(R.id.updatePasswordEditText)
                            val checkPasswordEditText =
                                dialogView.findViewById<EditText>(R.id.checkPasswordEditText)
                            if (checkPasswordEditText != null && updatePasswordEditText != null) {
                                val password = updatePasswordEditText.text.toString()
                                val checkPassword = checkPasswordEditText.text.toString()
                                if (password == checkPassword) {
                                    val md5Password = getMD5(password)
                                    val result = viewModel.updatePassword(md5Password)
                                    result.observe(this, { result ->
                                        if (result.isSuccess) {
                                            alertDialog.dismiss()
                                            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
                                            ABSIntelligentCloudApplication.token = ""
                                            ABSIntelligentCloudApplication.role = -1
                                            viewModel.removeToken()
                                            val intent = Intent(this, LoginActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                } else {
                                    Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "请输入新的密码并确认密码", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                false
            }
            popupMenu.show()
        }
    }

    private fun getStatusDevices() {
        val areaNow = areaFrag.viewModel.areaNow.value
        if (areaNow != null) {
            viewModel.getStatusDevices(StatusDeviceBody(page, areaNow.areaId))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                val backUpdate = data?.getBooleanExtra("back_update", false)
                Log.d("ManageActivity", backUpdate.toString())
                if (backUpdate == true) {
                    page = 1
                    getStatusDevices()
                }
            }
        }
    }
}