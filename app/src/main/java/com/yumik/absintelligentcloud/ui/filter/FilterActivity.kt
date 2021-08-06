package com.yumik.absintelligentcloud.ui.filter

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.ActivityFilterBinding
import com.yumik.absintelligentcloud.databinding.ChipFilterBinding
import com.yumik.absintelligentcloud.logic.model.Area
import com.yumik.absintelligentcloud.logic.model.FilterHistory
import com.yumik.absintelligentcloud.logic.network.response.EmptyResponse
import com.yumik.absintelligentcloud.ui.BaseActivity
import com.yumik.absintelligentcloud.util.DatePickerFragment
import com.yumik.absintelligentcloud.dialog.LoadingDialog
import com.yumik.absintelligentcloud.util.SPUtil
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar
import com.yumik.absintelligentcloud.util.setOnUnShakeClickListener
import com.yumik.statusbar.StatusBar
import java.text.SimpleDateFormat
import java.util.*


class FilterActivity : BaseActivity() {

    companion object {
        const val TAG = "FilterActivity"
    }

    private lateinit var binding: ActivityFilterBinding
    private lateinit var viewModel: FilterViewModel
    private val dialog = LoadingDialog(this)

    private var type = FilterType.NORMAL

    private val accessToken by SPUtil(this, "accessToken", "")
    private var fixedAreaId by SPUtil(this, "fixedAreaId", "")
    private var historyAreaId: String? = null

    private var tempFixedAreaId = ""
    private var tempFixedStatusId = ""
    private var tempFixedFaultId = ""

    private lateinit var dateFromPicker: DatePickerFragment
    private lateinit var dateToPicker: DatePickerFragment

    enum class FilterType {
        NORMAL,
        AREA_FILTER,
        DEVICE_FILTER,
        HISTORY_FILTER,
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 绑定viewModel
        viewModel = ViewModelProvider(this).get(FilterViewModel::class.java)

        // 修改状态栏颜色
        // url: https://stackoverflow.com/questions/65423778/system-ui-flag-light-status-bar-and-flag-translucent-status-is-deprecated
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = window
            val decorView: View = window.decorView
            val wic = WindowInsetsControllerCompat(window, decorView)
            wic.isAppearanceLightStatusBars = true
            // window.statusBarColor = Color.WHITE
        }

        // 获取类型
        type = FilterType.valueOf(intent.getStringExtra("type") ?: "NORMAL")

        // 变量初始化
        initVariable()

        // 处理viewModel回调
        initViewModel()

        // 处理组件
        initView()
    }

    private fun initVariable() {
        tempFixedAreaId = fixedAreaId
        dateFromPicker =
            DatePickerFragment(object : DatePickerFragment.OnDateSetListener {
                override fun callback(setDate: Long): Boolean {
                    if (setDate > (binding.dateToFilter.tag as String).toLong()) {
                        binding.container.showMySnackbar("起始时间必须小于终止时间", R.color.secondary_red)
                    } else {
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                        binding.dateFromFilter.text = formatter.format(setDate)
                        binding.dateFromFilter.tag = setDate.toString()
                        return true
                    }
                    return false
                }
            }, Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -5) })
        dateToPicker =
            DatePickerFragment(object : DatePickerFragment.OnDateSetListener {
                override fun callback(setDate: Long): Boolean {
                    if (setDate < (binding.dateFromFilter.tag as String).toLong()) {
                        binding.container.showMySnackbar("终止时间必须大于起始时间", R.color.secondary_red)
                    } else {
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                        binding.dateToFilter.text = formatter.format(setDate)
                        binding.dateToFilter.tag = setDate.toString()
                        return true
                    }
                    return false
                }
            }, Calendar.getInstance())

    }

    private fun initViewModel() {
        viewModel.areaList.observe(this) {
            dialog.dismissDialog()
            val result = it.getOrNull()
            if (result != null) {
                if (historyAreaId == null)
                    setArea(binding.areaFilterChipGroup, result.data, tempFixedAreaId)
                else
                    setArea(binding.areaFilterChipGroup, result.data, historyAreaId!!)
            } else {
                try {
                    it.onFailure { throwable ->
                        Log.d(TAG, throwable.message.toString())
                        val errorResponse =
                            Gson().fromJson(throwable.message, EmptyResponse::class.java)
                        binding.container.showMySnackbar(
                            errorResponse.message,
                            R.color.secondary_red
                        )
                    }
                } catch (e: Exception) {
                    binding.container.showMySnackbar("网络异常！请检查网络连接", R.color.secondary_yellow)
                }
            }
        }
    }

    private fun initView() {
        // 如果保存了历史记录
        val historyData = intent.getStringExtra("history")
        Log.d(TAG, historyData.toString())
        if (historyData != null) {
            val historyJson = Gson().fromJson(historyData, FilterHistory::class.java)
            historyAreaId = historyJson.areaFilter
            binding.filterAbsType.setText(historyJson.absType)
            binding.filterAgentName.setText(historyJson.agentName)
            binding.filterContactNumber.setText(historyJson.contactNumber)
            binding.filterDeviceId.setText(historyJson.deviceId)
            binding.filterTireBrand.setText(historyJson.tireBrand)
            binding.filterUserName.setText(historyJson.userName)
            tempFixedStatusId = historyJson.statusId
            when (historyJson.statusId) {
                binding.allStatusFilterChip.tag -> binding.statusFilterChipGroup.check(binding.allStatusFilterChip.id)
                binding.enableStatusFilterChip.tag -> binding.statusFilterChipGroup.check(binding.enableStatusFilterChip.id)
                binding.disableStatusFilterChip.tag -> binding.statusFilterChipGroup.check(binding.disableStatusFilterChip.id)
            }
            when (historyJson.faultId) {
                binding.allFaultFilterChip.tag -> binding.faultFilterChipGroup.check(binding.allFaultFilterChip.id)
                binding.normalFaultFilterChip.tag -> binding.faultFilterChipGroup.check(binding.normalFaultFilterChip.id)
                binding.abnormalFaultFilterChip.tag -> binding.faultFilterChipGroup.check(binding.abnormalFaultFilterChip.id)
            }
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            binding.dateFromFilter.text = formatter.format(historyJson.dataFrom)
            binding.dateFromFilter.tag = historyJson.dataFrom.toString()
            binding.dateToFilter.text = formatter.format(historyJson.dataTo)
            binding.dateToFilter.tag = historyJson.dataTo.toString()
        } else {
            val time = Calendar.getInstance()
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            binding.dateToFilter.text = formatter.format(time.timeInMillis)
            binding.dateToFilter.tag = time.timeInMillis.toString()
            time.add(Calendar.DAY_OF_MONTH, -5)
            binding.dateFromFilter.text = formatter.format(time.timeInMillis)
            binding.dateFromFilter.tag = time.timeInMillis.toString()
        }

        // 请求 AreaList
        dialog.showDialog()
        viewModel.getAreaList(accessToken)

        // 处理Filter显示内容
        when (type) {
            FilterType.AREA_FILTER -> {
                binding.deviceLayout.visibility = View.GONE
                binding.timeLayout.visibility = View.GONE
                binding.statusLayout.visibility = View.GONE
                binding.faultLayout.visibility = View.GONE
            }
            FilterType.DEVICE_FILTER -> {
                binding.timeLayout.visibility = View.GONE
            }
            FilterType.HISTORY_FILTER -> {
                binding.areaLayout.visibility = View.GONE
                binding.statusLayout.visibility = View.GONE
                binding.faultLayout.visibility = View.GONE
            }
            else -> {

            }
        }

        // reset 按钮
        binding.reset.setOnUnShakeClickListener {
            binding.areaFilterChipGroup.check(R.id.unknownAreaFilterChip)
            binding.filterAbsType.text.clear()
            binding.filterAgentName.text.clear()
            binding.filterContactNumber.text.clear()
            binding.filterDeviceId.text.clear()
            binding.filterTireBrand.text.clear()
            binding.filterUserName.text.clear()

            val time = Calendar.getInstance()
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
            binding.dateToFilter.text = formatter.format(time.timeInMillis)
            binding.dateToFilter.tag = time.timeInMillis.toString()
            time.add(Calendar.DAY_OF_MONTH, -5)
            binding.dateFromFilter.text = formatter.format(time.timeInMillis)
            binding.dateFromFilter.tag = time.timeInMillis.toString()

            binding.statusFilterChipGroup.check(R.id.allStatusFilterChip)
            binding.faultFilterChipGroup.check(R.id.allFaultFilterChip)
        }

        // 区域选择组
        binding.areaFilterChipGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == -1) {
                binding.allAreaFilterChip.isChecked = true
                tempFixedAreaId = binding.allAreaFilterChip.tag as String
            } else {
                try {
                    binding.root.findViewById<Chip>(checkedId).apply {
                        Log.d(TAG, "tag:$tag, text:$text")
                        tempFixedAreaId = tag as String
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                    binding.allAreaFilterChip.isChecked = true
                    tempFixedAreaId = binding.allAreaFilterChip.tag as String
                }
            }
        }

        // 状态选择组
        binding.statusFilterChipGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == -1) {
                binding.allStatusFilterChip.isChecked = true
                tempFixedStatusId = binding.allStatusFilterChip.tag as String
            } else {
                try {
                    binding.root.findViewById<Chip>(checkedId).apply {
                        Log.d(TAG, "tag:$tag, text:$text")
                        tempFixedStatusId = tag as String
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                    binding.allStatusFilterChip.isChecked = true
                    tempFixedStatusId = binding.allAreaFilterChip.tag as String
                }
            }
        }

        // 故障选择组
        binding.faultFilterChipGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == -1) {
                binding.allFaultFilterChip.isChecked = true
                tempFixedFaultId = binding.allFaultFilterChip.tag as String
            } else {
                try {
                    binding.root.findViewById<Chip>(checkedId).apply {
                        Log.d(TAG, "tag:$tag, text:$text")
                        tempFixedFaultId = tag as String
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                    binding.allFaultFilterChip.isChecked = true
                    tempFixedFaultId = binding.allFaultFilterChip.tag as String
                }
            }
        }

        // 状态栏返回回调
        binding.statusBar.setOnStatusBarClickListener(object : StatusBar.StatusBarClickListener {
            override fun backButtonClick() {
                onBackPressed()
            }

            override fun functionButtonClick() {
            }
        })

        // 时间选择器
        binding.dateFromFilter.setOnUnShakeClickListener {
            dateFromPicker.show(supportFragmentManager, "datePickerDialog")
        }
        binding.dateToFilter.setOnUnShakeClickListener {
            dateToPicker.show(supportFragmentManager, "datePickerDialog")
        }


        // 确定筛选
        binding.confirm.setOnUnShakeClickListener {
            confirmResult()
        }

        // 取消
        binding.cancel.setOnUnShakeClickListener {
            onBackPressed()
        }
    }

    private fun setArea(chipGroup: ChipGroup, areaList: List<Area>, fixedAreaId: String) {
        for (area in areaList) {
            val chip = ChipFilterBinding.inflate(layoutInflater).allAreaFilterChip
                .apply {
                    text = area.areaName
                    tag = area.areaId
                    chipEndPadding = context.resources.getDimension(R.dimen.dimen_16)
                    chipStartPadding = context.resources.getDimension(R.dimen.dimen_16)
                    id = View.generateViewId()
                }
            chipGroup.addView(chip)
            if (fixedAreaId == area.areaId)
                chipGroup.check(chip.id)
        }
    }

    private fun confirmResult() {
        if (type == FilterType.AREA_FILTER) {
            fixedAreaId = tempFixedAreaId
        }
        val filterData = FilterHistory(
            tempFixedAreaId,
            binding.filterAbsType.text.toString(),
            binding.filterAgentName.text.toString(),
            binding.filterContactNumber.text.toString(),
            binding.filterDeviceId.text.toString(),
            binding.filterTireBrand.text.toString(),
            binding.filterUserName.text.toString(),
            tempFixedStatusId,
            tempFixedFaultId,
            (binding.dateFromFilter.tag as String).toLong(),
            (binding.dateToFilter.tag as String).toLong(),
        )
        Log.d(TAG, filterData.toString())
        val intent = Intent().also {
            it.putExtra("result", Gson().toJson(filterData))
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}