package com.yumik.absintelligentcloud

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yumik.absintelligentcloud.databinding.ActivityMainBinding
import com.yumik.absintelligentcloud.dialog.DownloadDialog
import com.yumik.absintelligentcloud.dialog.LoadingDialog
import com.yumik.absintelligentcloud.ui.BaseActivity
import com.yumik.absintelligentcloud.ui.device.DeviceActivity
import com.yumik.absintelligentcloud.ui.login.LoginActivity
import com.yumik.absintelligentcloud.util.SPUtil


class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var viewModel: MainViewModel
    val dialog = LoadingDialog(this)

    var accessToken by SPUtil(this, "accessToken", "")
    var areaId by SPUtil(this, "fixedAreaId", "")

    private lateinit var broadcastReceiver: BroadcastReceiver

    // 销毁前做的事情
    private val needDoList = mutableListOf<() -> Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // 绑定Nav切换控制
        // url: https://stackoverflow.com/questions/59275009/fragmentcontainerview-using-findnavcontroller/59275182#59275182
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)

        // 修改状态栏颜色
        // url: https://stackoverflow.com/questions/65423778/system-ui-flag-light-status-bar-and-flag-translucent-status-is-deprecated
        val window: Window = window
        val decorView: View = window.decorView
        val wic = WindowInsetsControllerCompat(window, decorView)
        wic.isAppearanceLightStatusBars = true
        // window.statusBarColor = Color.WHITE

        // 检查登录状态 ThemeOverlay_MaterialComponents_MaterialAlertDialog_FullWidthButtons
        checkLogin()
        // 检查更新
        viewModel.checkUpdate()

        initBroadcast()

        initViewModel()
    }

    private fun initViewModel() {
        viewModel.checkUpdateLiveData.observe(this, {
            val downloadDialog = DownloadDialog(this, it)
            downloadDialog.show(supportFragmentManager, "downloadDialog")
        })
    }

    // Activity Results API
    // https://segmentfault.com/a/1190000037601888
    //    private val mainActivityLauncher =
    //        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    //            if (it.resultCode == Activity.RESULT_OK) {
    //                val result = it.data?.getBooleanExtra("login", false)
    //                Log.d("MainActivity", "result:$result")
    //            }
    //        }

    fun checkLogin() {
        dialog.showDialog()
        if (accessToken == "") {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
//            binding.container.showMySnackbar("自动登录成功！")
        }
        dialog.dismissDialog()
    }

    private fun initBroadcast() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Application.BROAD_GET_DEVICE -> {
                        val intentDeviceActivity =
                            Intent(context, DeviceActivity::class.java).apply {
                                putExtra("deviceId", intent.getStringExtra("deviceId"))
                            }
                        startActivity(intentDeviceActivity)
                    }
                    Application.BROAD_LOG_OUT -> {
                        accessToken = ""
                        checkLogin()
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(Application.BROAD_GET_DEVICE)
            addAction(Application.BROAD_LOG_OUT)
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, intentFilter)

        needDoList.add {
            LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(broadcastReceiver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (needDoItem in needDoList) {
            needDoItem()
        }
        needDoList.clear()
    }
}