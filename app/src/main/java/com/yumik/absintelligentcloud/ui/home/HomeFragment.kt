package com.yumik.absintelligentcloud.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.yumik.absintelligentcloud.Application.Companion.BROAD_ADD_DEVICE
import com.yumik.absintelligentcloud.Application.Companion.BROAD_SEARCH_DEVICE
import com.yumik.absintelligentcloud.MainActivity
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.FragmentHomeBinding
import com.yumik.absintelligentcloud.logic.model.FilterHistory
import com.yumik.absintelligentcloud.logic.model.Porcelain
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.logic.network.body.StatusDeviceListBody
import com.yumik.absintelligentcloud.module.device.DeviceAdapter
import com.yumik.absintelligentcloud.module.porcelain.PorcelainAdapter
import com.yumik.absintelligentcloud.ui.filter.FilterActivity
import com.yumik.absintelligentcloud.util.OnLoadMoreListener
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar
import com.yumik.statusbar.StatusBar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
        private const val TAG = "HomeFragment"
        private const val DELAY_TIME = 60000L
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var porcelainAdapter: PorcelainAdapter
    private lateinit var statusDeviceListAdapter: DeviceAdapter
    private lateinit var mainActivity: MainActivity

    // 销毁前做的事情
    private val needDoList = mutableListOf<() -> Unit>()

    private var page = 1
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        viewModel.getNewStatusDeviceList(
            StatusDeviceListBody(1, mainActivity.areaId), mainActivity.accessToken
        )
    }
    private val filterLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val result = it.data?.getStringExtra("result")
                if (result != null) {
                    val data = Gson().fromJson(result, FilterHistory::class.java)
                    mainActivity.areaId = data.areaFilter
                    page = 1
                    mainActivity.dialog.showDialog()
                    viewModel.getStatusDeviceList(
                        StatusDeviceListBody(page, mainActivity.areaId), mainActivity.accessToken
                    )
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        needDoList.add { _binding = null }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 处理 late init
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        porcelainAdapter = PorcelainAdapter()
        statusDeviceListAdapter = DeviceAdapter(requireContext(), true)
        mainActivity = activity as MainActivity

        // 处理viewModel回调
        initViewModel()

        // 处理组件
        initView()

    }

    private fun initViewModel() {
        viewModel.statusDeviceList.observe(viewLifecycleOwner, {
            mainActivity.dialog.dismissDialog()
            binding.swipeRefresh.isRefreshing = false

            if (it.code == Repository.ApiException.CODE_SUCCESS && it.data != null) {
                val data = it.data
                if (page == 1)
                    statusDeviceListAdapter.reAdd(data.list)
                else
                    statusDeviceListAdapter.add(data.list)
                binding.statusDeviceNumber.text = data.totalRecords.toString()
                page = min(data.page + 1, data.totalPages)
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                binding.updateTime.text = formatter.format(Calendar.getInstance().timeInMillis)
            } else {
                binding.container.showMySnackbar(
                    it.message,
                    R.color.secondary_red
                )
            }
        })

        viewModel.porcelainList.observe(viewLifecycleOwner, {
            porcelainAdapter.add(it)
        })

        viewModel.newStatusDeviceList.observe(viewLifecycleOwner, {
            mainActivity.dialog.dismissDialog()
            binding.swipeRefresh.isRefreshing = false
            if (it.code == Repository.ApiException.CODE_SUCCESS && it.data != null) {
                val data = it.data
                statusDeviceListAdapter.add(data.list)
                binding.statusDeviceNumber.text = data.totalRecords.toString()
                page = min(data.page + 1, data.totalPages)
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                binding.updateTime.text = formatter.format(Calendar.getInstance().timeInMillis)
                handler.postDelayed(runnable, DELAY_TIME)
            }
        })
    }

    private fun initView() {
        // 筛选区
        binding.statusBar.setOnStatusBarClickListener(object : StatusBar.StatusBarClickListener {
            override fun backButtonClick() {}

            override fun functionButtonClick() {
                val intent = Intent(requireActivity(), FilterActivity::class.java).apply {
                    putExtra("type", FilterActivity.FilterType.AREA_FILTER.name)
                }
                filterLauncher.launch(intent)
            }
        })
        // 金刚区
        binding.porcelainListView.layoutManager = object : GridLayoutManager(requireContext(), 2) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding.porcelainListView.adapter = porcelainAdapter
        viewModel.setPorcelainList(
            listOf(
                Porcelain(0, R.drawable.ic_search, "搜索") {
                    findNavController().navigate(R.id.navigation_equipment)
                    needDoList.add {
                        val intent = Intent().setAction(BROAD_SEARCH_DEVICE)
                        LocalBroadcastManager.getInstance(requireContext())
                            .sendBroadcast(intent)
                    }
                },
                Porcelain(1, R.drawable.ic_add, "添加") {
                    findNavController().navigate(R.id.navigation_equipment)
                    needDoList.add {
                        val intent = Intent().setAction(BROAD_ADD_DEVICE)
                        LocalBroadcastManager.getInstance(requireContext())
                            .sendBroadcast(intent)
                    }
                },
                Porcelain(2, R.drawable.ic_list, "设备") {
                    findNavController().navigate(R.id.navigation_equipment)
                },
                Porcelain(3, R.drawable.ic_history, "历史") {
                    findNavController().navigate(R.id.navigation_history)
                },
            )
        )

        // 异常设备区
        binding.statusDeviceListRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.statusDeviceListRV.adapter = statusDeviceListAdapter
        mainActivity.dialog.showDialog()
        viewModel.getStatusDeviceList(
            StatusDeviceListBody(page, mainActivity.areaId), mainActivity.accessToken
        )

        binding.statusDeviceListRV.addOnScrollListener(object : OnLoadMoreListener() {
            override fun onLoadMore() {
                viewModel.getStatusDeviceList(
                    StatusDeviceListBody(page, mainActivity.areaId), mainActivity.accessToken
                )
            }
        })

        // 刷新页面
        binding.swipeRefresh.setColorSchemeResources(R.color.primary)
        binding.swipeRefresh.setOnRefreshListener {
            page = 1
            mainActivity.dialog.showDialog()
            viewModel.getStatusDeviceList(
                StatusDeviceListBody(page, mainActivity.areaId), mainActivity.accessToken
            )
        }

        // 反复获取最新信息
        handler.postDelayed(runnable, DELAY_TIME)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        for (needDoItem in needDoList) {
            needDoItem()
        }
        needDoList.clear()
    }
}