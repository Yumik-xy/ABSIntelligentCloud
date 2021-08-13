package com.yumik.absintelligentcloud.ui.area

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.ActivityAreaBinding
import com.yumik.absintelligentcloud.databinding.ChipFilterBinding
import com.yumik.absintelligentcloud.dialog.LoadingDialog
import com.yumik.absintelligentcloud.logic.model.Area
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.util.SPUtil
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar
import com.yumik.absintelligentcloud.util.setOnUnShakeClickListener
import com.yumik.statusbar.StatusBar

class AreaActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AreaActivity"
    }

    private lateinit var binding: ActivityAreaBinding
    private lateinit var viewModel: AreaViewModel
    private val dialog = LoadingDialog(this)

    private var areaId: String = ""
    private var areaName: String = "未知"

    private val accessToken by SPUtil(this, "accessToken", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAreaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 修改状态栏颜色
        // url: https://stackoverflow.com/questions/65423778/system-ui-flag-light-status-bar-and-flag-translucent-status-is-deprecated
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = window
            val decorView: View = window.decorView
            val wic = WindowInsetsControllerCompat(window, decorView)
            wic.isAppearanceLightStatusBars = true
            // window.statusBarColor = Color.WHITE
        }

        // 绑定viewModel
        viewModel = ViewModelProvider(this).get(AreaViewModel::class.java)

        // 获取默认areaId
        areaId = intent.getStringExtra("areaId") ?: ""

        // 处理viewModel回调
        initViewModel()

        // 处理组件
        initView()
    }

    private fun initViewModel() {
        viewModel.areaListLiveData.observe(this) {
            dialog.dismissDialog()
            if (it.code == Repository.ApiException.CODE_SUCCESS && it.data != null) {
                setArea(binding.areaFilterChipGroup, it.data, areaId)
            } else {
                binding.container.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        }
    }

    private fun initView() {
        viewModel.getAreaList(accessToken)

        binding.areaFilterChipGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == -1) {
                binding.unknownAreaFilterChip.isChecked = true
                areaId = binding.unknownAreaFilterChip.tag as String
                areaName = binding.unknownAreaFilterChip.text as String
            } else {
                try {
                    binding.root.findViewById<Chip>(checkedId).apply {
                        Log.d(TAG, "tag:$tag, text:$text")
                        areaId = tag as String
                        areaName = text.toString()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.message.toString())
                    binding.unknownAreaFilterChip.isChecked = true
                    areaId = binding.unknownAreaFilterChip.tag as String
                    areaName = binding.unknownAreaFilterChip.text as String
                }
            }
        }

        // 确定筛选
        binding.confirm.setOnUnShakeClickListener {
            val intent = Intent().also {
                it.putExtra("areaId", areaId)
                it.putExtra("areaName", areaName)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        // 取消
        binding.cancel.setOnUnShakeClickListener {
            onBackPressed()
        }

        // 状态栏返回按键
        binding.statusBar.setOnStatusBarClickListener(object : StatusBar.StatusBarClickListener {
            override fun backButtonClick() {
                onBackPressed()
            }

            override fun functionButtonClick() {
            }
        })
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
            if (fixedAreaId == area.areaId) {
                areaName = area.areaName
                chipGroup.check(chip.id)
            }
        }
    }
}