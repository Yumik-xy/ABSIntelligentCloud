package com.absintelligentcloud.android.ui.detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.DetailBody
import com.absintelligentcloud.android.logic.util.setSpinnerItemSelectedByValue
import com.absintelligentcloud.android.ui.area.AreaFragment
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.fragment_area.*
import java.text.SimpleDateFormat
import java.util.*
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import com.absintelligentcloud.android.ABSIntelligentCloudApplication


class DetailActivity : AppCompatActivity() {

    companion object {
        const val FUN_SOLVE = 1
        const val FUN_ADD = 2
        const val FUN_UPDATE = 3
        const val FUN_READONLY = 4
    }

    private val viewModel by lazy { ViewModelProvider(this).get(DetailViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val deviceId = intent.getStringExtra("device_id") ?: ""
        val function = intent.getIntExtra("function", -1)

        productionDateEdit.setOnClickListener {
            if (productionDateEdit.isEnabled) {
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
        }

        if (function != FUN_ADD) {
            viewModel.getDeviceDetail(deviceId)
        }

        viewModel.detailLiveData.observe(this, { result ->
            val detail = result.getOrNull()
            if (detail != null) {
                absTypeEdit.setText(detail.absType)
                deviceIdEdit.setText(detail.deviceId)
                userNameEdit.setText(detail.userName)
                contactNumberEdit.setText(detail.contactNumber)
                agentNameEdit.setText(detail.agentName)
                tireBrandEdit.setText(detail.tireBrand)
                val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                productionDateEdit.text = simpleDataFormat.format(detail.productionDate)

                val areaFrag = areaChoiceFragment as AreaFragment
                val areaList = areaFrag.viewModel.areaList
                for (area in areaList) {
                    if (detail.areaId == area.areaId) {
                        setSpinnerItemSelectedByValue(areaChoiceFragment.areaSpin, area)
                    }
                }
            } else {
                Toast.makeText(this, "无法获取详情信息", Toast.LENGTH_SHORT).show()
            }
        })

        when (function) {
            FUN_ADD -> {
                setClickable(true)
                saveBtn.visibility = View.VISIBLE
            }
            FUN_UPDATE -> {
                deleteBtn.visibility = View.VISIBLE
                updateBtn.visibility = View.VISIBLE
            }
            FUN_SOLVE -> {
                solveBtn.visibility = View.VISIBLE
            }
        }

        solveBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("确定是否已解决")
                .setPositiveButton("确认") { dialogInterface, i ->
                    viewModel.solveDevice(deviceId)
                }.setNegativeButton("取消") { dialogInterface, i -> }
                .show()
        }

        saveBtn.setOnClickListener {
            when (function) {
                FUN_ADD -> viewModel.addDevice(getDetailBody())
                FUN_UPDATE -> {
                    viewModel.updateDevice(getDetailBody())
                    updateBtn.visibility = View.VISIBLE
                    saveBtn.visibility = View.GONE
                }
            }
        }

        viewModel.addResponseLiveData.observe(this, { result ->
            val response = result.getOrNull()
            if (response != null) {
                setClickable(false)
                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "提交失败，请检查网络！", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.updateResponseLiveData.observe(this, { result ->
            val response = result.getOrNull()
            if (response != null) {
                setClickable(false)
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "提交失败，请检查网络！", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.deleteResponseLiveData.observe(this, { result ->
            val response = result.getOrNull()
            if (response != null) {
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
                backAndUpdate()
            } else {
                Toast.makeText(this, "提交失败，请检查网络！", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.solveResponseLiveData.observe(this, { result ->
            val response = result.getOrNull()
            if (response != null) {
                solveBtn.visibility = View.GONE
                Toast.makeText(this, "问题已解决！", Toast.LENGTH_SHORT).show()
                backAndUpdate()
            } else {
                Toast.makeText(this, "提交失败，请检查网络！", Toast.LENGTH_SHORT).show()
            }
        })

        updateBtn.setOnClickListener {
            setClickable(true)
            updateBtn.visibility = View.GONE
            saveBtn.visibility = View.VISIBLE
        }

        deleteBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("确定要删除吗")
                .setPositiveButton("确认") { dialogInterface, i ->
                    viewModel.deleteDevice(deviceId)
                }.setNegativeButton("取消") { dialogInterface, i -> }
                .show()
        }

        backDetailBtn.setOnClickListener {
            backAndUpdate()
        }

        contactNumberView.setOnLongClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            val phone = contactNumberEdit.text.toString()
            intent.data = Uri.parse("tel:$phone")
            startActivity(intent)
            true
        }
    }

    private fun getDetailBody(): DetailBody {
        val absType = absTypeEdit.text.toString()
        val deviceId = deviceIdEdit.text.toString()
        val areaFrag = areaChoiceFragment as AreaFragment
        val areaId = areaFrag.areaId
        val userName = userNameEdit.text.toString()
        val productionDateText = productionDateEdit.text
        var dataList = productionDateText.split("-")
        if (dataList[0].isEmpty()) dataList = listOf("1970", "1", "1")
        val productionDate = GregorianCalendar(
            dataList[0].toInt(),
            dataList[1].toInt() - 1,
            dataList[2].toInt()
        ).timeInMillis
        val contactNumber = contactNumberEdit.text.toString()
        val agentName = agentNameEdit.text.toString()
        val tireBrand = tireBrandEdit.text.toString()
        return DetailBody(
            absType,
            deviceId,
            areaId,
            userName,
            productionDate,
            contactNumber,
            agentName,
            tireBrand,
        )
    }

    private fun setClickable(able: Boolean) {
        absTypeEdit.isEnabled = able
        deviceIdEdit.isEnabled = able
        userNameEdit.isEnabled = able
        contactNumberEdit.isEnabled = able
        agentNameEdit.isEnabled = able
        tireBrandEdit.isEnabled = able
        productionDateEdit.isEnabled = able
        contactNumberView.visibility = if (able) View.GONE else View.VISIBLE
        areaChoiceView.visibility = if (able) View.GONE else View.VISIBLE
    }

    private fun backAndUpdate() {
        ABSIntelligentCloudApplication.needUpdate = true
        finish()
    }
}