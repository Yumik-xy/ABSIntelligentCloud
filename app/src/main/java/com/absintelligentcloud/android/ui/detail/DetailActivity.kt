package com.absintelligentcloud.android.ui.detail

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.DetailBody
import com.absintelligentcloud.android.logic.util.setSpinnerItemSelectedByValue
import com.absintelligentcloud.android.ui.area.AreaFragment
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.absTypeEdit
import kotlinx.android.synthetic.main.activity_detail.agentNameEdit
import kotlinx.android.synthetic.main.activity_detail.areaChoiceFragment
import kotlinx.android.synthetic.main.activity_detail.contactNumberEdit
import kotlinx.android.synthetic.main.activity_detail.deviceIdEdit
import kotlinx.android.synthetic.main.activity_detail.tireBrandEdit
import kotlinx.android.synthetic.main.activity_detail.toolbar
import kotlinx.android.synthetic.main.activity_detail.userNameEdit
import kotlinx.android.synthetic.main.fragment_area.*
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    companion object {
        const val FUN_SOLVE = 1
        const val FUN_ADD = 2
        const val FUN_UPDATE = 3
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
                val simpleDataFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                productionDateEdit.text = simpleDataFormat.format(detail.productionDate)
                setSpinnerItemSelectedByValue(areaChoiceFragment.areaSpin, detail.areaId)
            } else {
                Toast.makeText(this, "无法获取详情信息", Toast.LENGTH_SHORT).show()
            }
        })

        when (function) {
            FUN_ADD -> {
                setClickable(true)
                saveBtn.visibility = View.VISIBLE
            }
            FUN_UPDATE -> updateBtn.visibility = View.VISIBLE
            FUN_SOLVE -> solveBtn.visibility = View.VISIBLE
        }

        solveBtn.setOnClickListener {
            viewModel.solveDevice(deviceId)
        }

        viewModel.solveResponseLiveData.observe(this, { result ->
            val response = result.getOrNull()
            if (response != null) {
                solveBtn.visibility = View.GONE
                Toast.makeText(this, "问题已解决！", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "提交失败，请检查网络！", Toast.LENGTH_SHORT).show()
            }
        })

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

        updateBtn.setOnClickListener {
            setClickable(true)
            updateBtn.visibility = View.GONE
            saveBtn.visibility = View.VISIBLE
        }
    }

    private fun getDetailBody(): DetailBody {
        val absType = absTypeEdit.text.toString()
        val deviceId = deviceIdEdit.text.toString()
        val areaFrag = areaChoiceFragment as AreaFragment
        val areaId = areaFrag.areaId
        val userName = userNameEdit.text.toString()
        val productionDateText = productionDateEdit.text
        val dataList = productionDateText.split("-")
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
        areaChoiceView.visibility = if (able) View.GONE else View.VISIBLE
    }
}