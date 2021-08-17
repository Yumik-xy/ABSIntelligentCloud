package com.yumik.absintelligentcloud.ui.equipment

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.yumik.absintelligentcloud.Application
import com.yumik.absintelligentcloud.MainActivity
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.FragmentEquipmentBinding
import com.yumik.absintelligentcloud.logic.model.FilterHistory
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.logic.network.body.DeviceListBody
import com.yumik.absintelligentcloud.module.device.DeviceAdapter
import com.yumik.absintelligentcloud.ui.device.DeviceActivity
import com.yumik.absintelligentcloud.ui.filter.FilterActivity
import com.yumik.absintelligentcloud.util.OnLoadMoreListener
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar
import com.yumik.absintelligentcloud.util.setOnUnShakeClickListener
import com.yumik.statusbar.StatusBar
import kotlin.math.min

class EquipmentFragment : Fragment() {

    companion object {
        fun newInstance() = EquipmentFragment()
        private const val TAG = "EquipmentFragment"
    }

    private var _binding: FragmentEquipmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: EquipmentViewModel
    private lateinit var deviceListAdapter: DeviceAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var broadcastReceiver: BroadcastReceiver

    // 销毁前做的事情
    private val needDoList = mutableListOf<() -> Unit>()

    private var page = 1
    private var filterHistory: String? = null
    private var filterData = FilterHistory()

    private val filterLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val result = it.data?.getStringExtra("result")
                if (result != null) {
                    filterHistory = result
                    filterData = Gson().fromJson(result, FilterHistory::class.java)
                    page = 1
                    binding.swipeRefresh.isRefreshing = true
                    viewModel.getDeviceList(
                        getDeviceList(), mainActivity.accessToken
                    )
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipmentBinding.inflate(inflater, container, false)
        needDoList.add { _binding = null }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(EquipmentViewModel::class.java)
        deviceListAdapter = DeviceAdapter(requireContext(), false)
        mainActivity = activity as MainActivity

        // 处理viewModel回调
        initViewModel()

        // 处理组件
        initView()

        // 注册广播
        initBroadcast()
    }

    private fun initBroadcast() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Application.BROAD_ADD_DEVICE -> {
                        addDevice()
                    }
                    Application.BROAD_SEARCH_DEVICE -> {
                        filterDevice()
                    }
                }
            }
        }
        val intentFilter = IntentFilter().apply {
            addAction(Application.BROAD_ADD_DEVICE)
            addAction(Application.BROAD_SEARCH_DEVICE)
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, intentFilter)

        needDoList.add {
            LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(broadcastReceiver)
        }
    }

    private fun initViewModel() {
        viewModel.deviceList.observe(viewLifecycleOwner, {
            binding.swipeRefresh.isRefreshing = false
            if (it.code == Repository.ApiException.CODE_SUCCESS && it.data != null) {
                val data = it.data
                if (page == 1)
                    deviceListAdapter.reAdd(data.list)
                else
                    deviceListAdapter.add(data.list)
                page = min(data.page + 1, data.totalPages)
            } else {
                binding.container.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        })
    }

    private fun initView() {
        // 筛选区
        binding.statusBar.setOnStatusBarClickListener(object : StatusBar.StatusBarClickListener {
            override fun backButtonClick() {}

            override fun functionButtonClick() {
                filterDevice()
            }
        })

        // 弹出添加设备窗口
        binding.addDeviceFab.setOnUnShakeClickListener {
            addDevice()
        }

        // 刷新页面
        binding.swipeRefresh.setColorSchemeResources(R.color.primary)
        binding.swipeRefresh.setOnRefreshListener {
            page = 1
            binding.swipeRefresh.isRefreshing = true
            viewModel.getDeviceList(
                getDeviceList(), mainActivity.accessToken
            )
        }

        // 设备区
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.deviceListRV.layoutManager = layoutManager
        binding.deviceListRV.adapter = deviceListAdapter
        binding.swipeRefresh.isRefreshing = true
        viewModel.getDeviceList(
            getDeviceList(), mainActivity.accessToken
        )

        binding.deviceListRV.addOnScrollListener(object : OnLoadMoreListener() {
            override fun onLoadMore() {
                viewModel.getDeviceList(
                    getDeviceList(), mainActivity.accessToken
                )
            }
        })
    }

    private fun addDevice() {
        val intent = Intent(requireActivity(), DeviceActivity::class.java).apply {
            putExtra("deviceId", "")
        }
        startActivity(intent)
    }

    fun filterDevice() {
        val intent = Intent(requireActivity(), FilterActivity::class.java).apply {
            putExtra("type", FilterActivity.FilterType.DEVICE_FILTER.name)
            putExtra("history", filterHistory)
        }
        filterLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        for (needDoItem in needDoList) {
            needDoItem()
        }
        needDoList.clear()
    }

    private fun getDeviceList(): DeviceListBody {
        return DeviceListBody(
            page,
            filterData.areaFilter,
            filterData.absType,
            filterData.deviceId,
            filterData.userName,
            filterData.contactNumber,
            filterData.agentName,
            filterData.tireBrand
        )
    }
}