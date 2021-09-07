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
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yumik.absintelligentcloud.databinding.ActivityMainBinding
import com.yumik.absintelligentcloud.dialog.DownloadDialog
import com.yumik.absintelligentcloud.dialog.LoadingDialog
import com.yumik.absintelligentcloud.ui.BaseActivity
import com.yumik.absintelligentcloud.ui.device.DeviceActivity
import com.yumik.absintelligentcloud.ui.equipment.EquipmentFragment
import com.yumik.absintelligentcloud.ui.history.HistoryFragment
import com.yumik.absintelligentcloud.ui.home.HomeFragment
import com.yumik.absintelligentcloud.ui.login.LoginActivity
import com.yumik.absintelligentcloud.ui.mine.MineFragment
import com.yumik.absintelligentcloud.util.FixFragmentNavigator
import com.yumik.absintelligentcloud.util.SPUtil


class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
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
        // val navHostFragment =
        //     supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // val navController = navHostFragment.navController
        // binding.navView.setupWithNavController(navController)
        // 手动加载navGraph
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        fragment?.let { it ->
            val navController = NavHostFragment.findNavController(it)
            val fragmentNavigator = FixFragmentNavigator(this, it.childFragmentManager, it.id)
            val provider = navController.navigatorProvider
            provider.addNavigator(fragmentNavigator)
            val navGraph = initNavGraph(provider, fragmentNavigator)
            navController.graph = navGraph
            binding.navView.setOnNavigationItemSelectedListener { item ->
                navController.navigate(item.itemId)
                true
            }
        }


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

    private fun initNavGraph(
        provider: NavigatorProvider,
        fragmentNavigator: FixFragmentNavigator
    ): NavGraph {
        val navGraph = NavGraph(NavGraphNavigator(provider))
        val des1 = fragmentNavigator.createDestination()
        des1.id = R.id.navigation_home
        des1.className = HomeFragment::class.java.canonicalName!!
        des1.label = resources.getString(R.string.首页)
        navGraph.addDestination(des1)

        val des2 = fragmentNavigator.createDestination()
        des2.id = R.id.navigation_equipment
        des2.className = EquipmentFragment::class.java.canonicalName!!
        des2.label = resources.getString(R.string.设备)
        navGraph.addDestination(des2)

        val des3 = fragmentNavigator.createDestination()
        des3.id = R.id.navigation_history
        des3.className = HistoryFragment::class.java.canonicalName!!
        des3.label = resources.getString(R.string.历史)
        navGraph.addDestination(des3)

        val des4 = fragmentNavigator.createDestination()
        des4.id = R.id.navigation_mine
        des4.className = MineFragment::class.java.canonicalName!!
        des4.label = resources.getString(R.string.我的)
        navGraph.addDestination(des4)

        navGraph.startDestination = R.id.navigation_home
        return navGraph
    }
}