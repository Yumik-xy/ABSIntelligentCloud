package com.yumik.absintelligentcloud.ui.device

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.ActivityDeviceBinding
import com.yumik.absintelligentcloud.dialog.ConfirmDialog
import com.yumik.absintelligentcloud.dialog.LoadingDialog
import com.yumik.absintelligentcloud.logic.network.Network
import com.yumik.absintelligentcloud.logic.network.body.AddDeviceBody
import com.yumik.absintelligentcloud.ui.area.AreaActivity
import com.yumik.absintelligentcloud.util.*
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar
import com.yumik.statusbar.StatusBar
import java.text.SimpleDateFormat
import java.util.*

class DeviceActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "DeviceActivity"
    }

    private lateinit var binding: ActivityDeviceBinding
    private lateinit var viewModel: DeviceViewModel

    private val accessToken by SPUtil(this, "accessToken", "")

    private val loadingDialog = LoadingDialog(this)
    private var tempAreaId = ""
    private var deviceId = ""

    private val deviceActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val areaId = it.data?.getStringExtra("areaId") ?: ""
                val areaName = it.data?.getStringExtra("areaName") ?: ""
                binding.deviceArea.text = areaName
                binding.deviceArea.tag = areaId
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 绑定viewModel
        viewModel = ViewModelProvider(this).get(DeviceViewModel::class.java)

        // 修改状态栏颜色
        // url: https://stackoverflow.com/questions/65423778/system-ui-flag-light-status-bar-and-flag-translucent-status-is-deprecated
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = window
            val decorView: View = window.decorView
            val wic = WindowInsetsControllerCompat(window, decorView)
            wic.isAppearanceLightStatusBars = true
            // window.statusBarColor = Color.WHITE
        }

        // 变量初始化
        initVariable()

        // 处理viewModel回调
        initViewModel()

        // 初始化组件
        initView()

        // 获取设备信息
        getDeviceInfo()
    }

    private fun getDeviceInfo() {
        val deviceId = deviceId
        if (deviceId == "") {
            binding.configGroup.visibility = View.GONE
            binding.manageGroup.visibility = View.GONE
            binding.update.performClick()
        } else {
            loadingDialog.showDialog()
            viewModel.getDeviceInfo(deviceId, accessToken)
        }
    }

    private fun initVariable() {
        val deviceId = intent.getStringExtra("deviceId")
        if (deviceId != null) {
            this.deviceId = deviceId
        } else {
            binding.container.showMySnackbar("获取deviceId失败！", R.color.secondary_red)
            Thread {
                Thread.sleep(1000)
                finish()
            }.start()
        }
    }

    private fun initViewModel() {
        viewModel.deviceInfoLiveData.observe(this, {
            loadingDialog.dismissDialog()
            if (it.code == Network.ApiException.CODE_SUCCESS && it.data != null) {
                val data = it.data
                binding.deviceAbsType.setText(data.absType)
                binding.deviceAgentName.setText(data.agentName)
                tempAreaId = data.areaId
                loadingDialog.showDialog()
                viewModel.getAreaList(accessToken)
                // 这里需要一个根据id找内容的
                setFault(if (data.status == 0) Fault.GOOD else Fault.BAD)
                binding.deviceContactNumber.setText(data.contactNumber)
                binding.deviceDeviceId.setText(data.deviceId)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                binding.deviceProductionDate.text = formatter.format(data.productionDate)
                binding.deviceProductionDate.tag = data.productionDate.toString()
                binding.deviceUserName.setText(data.userName)
                binding.deviceTireBrand.setText(data.tireBrand)
            } else {
                binding.container.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        })

        viewModel.areaListLiveData.observe(this, {
            loadingDialog.dismissDialog()
            if (it.code == Network.ApiException.CODE_SUCCESS && it.data != null) {
                for (area in it.data)
                    if (area.areaId == tempAreaId) {
                        binding.deviceArea.text = area.areaName
                        binding.deviceArea.tag = area.areaId
                    }
            } else {
                binding.container.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        })

        viewModel.deleteDeviceLiveData.observe(this, {
            loadingDialog.dismissDialog()
            if (it.code == Network.ApiException.CODE_SUCCESS) {
                binding.container.showMySnackbar("该设备已删除！", R.color.primary)
                Thread {
                    Thread.sleep(1000)
                    finish()
                }.start()
            } else {
                binding.container.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        })

        viewModel.addDeviceLiveData.observe(this, {
            loadingDialog.dismissDialog()
            if (it.code == Network.ApiException.CODE_SUCCESS) {
                binding.container.showMySnackbar("添加成功！", R.color.primary)
                deviceId = binding.deviceDeviceId.text.toString()
                binding.manageGroup.visibility = View.VISIBLE
                binding.configGroup.visibility = View.VISIBLE
                enableChange(false)
                binding.update.text = "修改"
            } else {
                binding.container.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        })

        viewModel.updateDeviceLiveData.observe(this, {
            loadingDialog.dismissDialog()
            if (it.code == Network.ApiException.CODE_SUCCESS) {
                binding.container.showMySnackbar("修改成功！", R.color.primary)
                enableChange(false)
                binding.update.text = "修改"
            } else {
                binding.container.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        })

        viewModel.updateFaultDeviceLiveData.observe(this, {
            loadingDialog.dismissDialog()
            if (it.code == Network.ApiException.CODE_SUCCESS) {
                binding.container.showMySnackbar("故障已排除！", R.color.primary)
                setFault(Fault.GOOD)
            } else {
                binding.container.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        })
    }

    private fun initView() {

        // 状态栏返回按键
        binding.statusBar.setOnStatusBarClickListener(object : StatusBar.StatusBarClickListener {
            override fun backButtonClick() {
                onBackPressed()
            }

            override fun functionButtonClick() {
            }
        })

        // 切换工作状态
        binding.statusLayout.setOnUnShakeClickListener {
            // TODO: ViewModel.xxx()
        }

        // 修正状态信息
        binding.faultLayout.setOnUnShakeClickListener {
            when (binding.faultLayout.tag as String) {
                Fault.GOOD.name -> {
                    binding.container.showMySnackbar("当前状态正常！", R.color.secondary_yellow)
                }
                Fault.BAD.name -> {
                    ConfirmDialog(this, "故障排除", "请确认故障是否排除") {
                        viewModel.updateFaultDevice(deviceId, accessToken)
                    }.showDialog()
                }
            }
        }

        // 获取地点信息
        binding.locationLayout.setOnUnShakeClickListener {
            // TODO
        }

        // 修改设备信息
        binding.update.setOnUnShakeClickListener {
            binding.update.apply {
                if (text == "修改") {
                    enableChange(true)
                    text = "保存"
                } else {
                    if (deviceId == "") {
                        ConfirmDialog(
                            this@DeviceActivity,
                            "添加设备",
                            "新设备编号为：${binding.deviceDeviceId.text}\n添加后编号不可修改！"
                        ) {
                            viewModel.addDevice(getAddDevice(), accessToken)
                            loadingDialog.showDialog()
                        }.showDialog()
                    } else {
                        viewModel.updateDevice(getAddDevice(), accessToken)
                        loadingDialog.showDialog()
                    }
                }
            }
        }

        // 删除设备信息
        binding.delete.setOnUnShakeClickListener {
            ConfirmDialog(this, "删除设备", "是否“删除设备”，操作不可恢复") {
                viewModel.deleteDevice(deviceId, accessToken)
                loadingDialog.showDialog()
            }.showDialog()
        }

        // 设备区域
        binding.deviceArea.setOnUnShakeClickListener {
            if (binding.deviceArea.isEnabled) {
                val intent = Intent(this, AreaActivity::class.java).apply {
                    putExtra("areaId", binding.deviceArea.tag as String)
                }
                deviceActivityLauncher.launch(intent)
            }
        }

        // 设置生产日期
        binding.deviceProductionDate.setOnUnShakeClickListener {
            if (binding.deviceProductionDate.isEnabled) {
                val productionDatePicker = DatePickerFragment(
                    object : DatePickerFragment.OnDateSetListener {
                        override fun callback(setDate: Long): Boolean {
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                            binding.deviceProductionDate.text = formatter.format(setDate)
                            binding.deviceProductionDate.tag = setDate.toString()
                            return true
                        }
                    },
                    Calendar.getInstance()
                        .apply {
                            if (binding.deviceProductionDate.tag as String != "0")
                                timeInMillis = (binding.deviceProductionDate.tag as String).toLong()
                        })
                productionDatePicker.show(supportFragmentManager, "datePickerDialog")
            }
        }

        // 展开/收起管理页面
        var showManageLayout = true
        binding.manageTitle.setOnUnShakeClickListener {
            if (showManageLayout) {
                binding.manageImage.setImageResource(R.drawable.ic_arrow_down)
                binding.manageLayout.visibility = View.VISIBLE
            } else {
                binding.manageImage.setImageResource(R.drawable.ic_arrow_up)
                binding.manageLayout.visibility = View.GONE
            }
            showManageLayout = !showManageLayout
        }
    }

    // 设置工作状态
    enum class Status {
        ENABLE, DISABLE, ENABLE_UNKNOWN, DISABLE_UNKNOWN
    }

    private fun setStatus(status: Status) {
        when (status) {
            Status.ENABLE -> {
                binding.statusImage.setImageResource(R.drawable.ic_status_enable)
                binding.statusText.text = "已启用"
            }
            Status.DISABLE -> {
                binding.statusImage.setImageResource(R.drawable.ic_status_disable)
                binding.statusText.text = "已禁用"
            }
            Status.ENABLE_UNKNOWN -> {
                binding.statusImage.setImageResource(R.drawable.ic_status_enable_unknown)
                binding.statusText.text = "已启用"
            }
            Status.DISABLE_UNKNOWN -> {
                binding.statusImage.setImageResource(R.drawable.ic_status_disable_unknown)
                binding.statusText.text = "已禁用"
            }
        }
    }

    // 设置设备故障
    enum class Fault {
        GOOD, BAD
    }

    private fun setFault(fault: Fault) {
        when (fault) {
            Fault.GOOD -> {
                binding.faultImage.setImageResource(R.drawable.ic_fault_good)
                binding.faultText.text = "状态良好"
                binding.faultLayout.tag = Fault.GOOD.name
            }
            Fault.BAD -> {
                binding.faultImage.setImageResource(R.drawable.ic_fault_bad)
                binding.faultText.text = "状态异常"
                binding.faultLayout.tag = Fault.BAD.name
            }
        }
    }

    // 设置能否修改
    private fun enableChange(enable: Boolean) {
        binding.deviceAbsType.isEnabled = enable
        binding.deviceAgentName.isEnabled = enable
        binding.deviceArea.isEnabled = enable
        binding.deviceContactNumber.isEnabled = enable
        if (deviceId == "") {
            binding.deviceDeviceId.isEnabled = enable
        } else {
            binding.deviceDeviceId.isEnabled = false
        }
        binding.deviceTireBrand.isEnabled = enable
        binding.deviceUserName.isEnabled = enable
        binding.deviceProductionDate.isEnabled = enable
    }

    private fun getAddDevice(): AddDeviceBody {
        return AddDeviceBody(
            binding.deviceAbsType.text.toString(),
            binding.deviceDeviceId.text.toString(),
            binding.deviceArea.tag as String,
            binding.deviceUserName.text.toString(),
            (binding.deviceProductionDate.tag as String).toLong(),
            binding.deviceContactNumber.text.toString(),
            binding.deviceAgentName.text.toString(),
            binding.deviceTireBrand.text.toString(),
        )
    }
}