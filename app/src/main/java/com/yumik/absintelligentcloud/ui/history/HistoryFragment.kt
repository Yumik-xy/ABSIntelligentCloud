package com.yumik.absintelligentcloud.ui.history

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.yumik.absintelligentcloud.MainActivity
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.FragmentHistoryBinding
import com.yumik.absintelligentcloud.logic.model.FilterHistory
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.logic.network.body.HistoryListBody
import com.yumik.absintelligentcloud.module.history.HistoryAdapter
import com.yumik.absintelligentcloud.ui.equipment.EquipmentFragment
import com.yumik.absintelligentcloud.ui.filter.FilterActivity
import com.yumik.absintelligentcloud.util.OnLoadMoreListener
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar
import com.yumik.statusbar.StatusBar
import java.util.*
import kotlin.math.min

class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() = EquipmentFragment()
    }

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HistoryViewModel
    private lateinit var historyListAdapter: HistoryAdapter
    private lateinit var mainActivity: MainActivity

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
                    viewModel.getHistoryList(
                        getHistoryList(), mainActivity.accessToken
                    )
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        historyListAdapter = HistoryAdapter(requireContext())
        mainActivity = activity as MainActivity

        // 处理viewModel回调
        initViewModel()

        // 处理组件
        initView()
    }

    private fun initViewModel() {
        viewModel.historyList.observe(viewLifecycleOwner, {
            binding.swipeRefresh.isRefreshing = false
            if (it.code == Repository.ApiException.CODE_SUCCESS && it.data != null) {
                val data = it.data
                if (page == 1) {
                    historyListAdapter.reAdd(data.list)
                }
                else {
                    historyListAdapter.add(data.list)
                }
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
                val intent = Intent(requireActivity(), FilterActivity::class.java).apply {
                    putExtra("type", FilterActivity.FilterType.HISTORY_FILTER.name)
                    putExtra("history", filterHistory)
                }
                filterLauncher.launch(intent)
            }
        })

        // 刷新页面
        binding.swipeRefresh.setColorSchemeResources(R.color.primary)
        binding.swipeRefresh.setOnRefreshListener {
            page = 1
            binding.swipeRefresh.isRefreshing = true
            viewModel.getHistoryList(
                getHistoryList(), mainActivity.accessToken
            )
        }

        // 设备区
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.historyDeviceListRV.layoutManager = layoutManager
        binding.historyDeviceListRV.adapter = historyListAdapter
        binding.swipeRefresh.isRefreshing = true
        viewModel.getHistoryList(
            getHistoryList(), mainActivity.accessToken
        )

        binding.historyDeviceListRV.addOnScrollListener(object : OnLoadMoreListener() {
            override fun onLoadMore() {
                viewModel.getHistoryList(
                    getHistoryList(), mainActivity.accessToken
                )
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fragment 的存在时间比其视图长，切记要销毁！
        _binding = null
    }

    private fun getHistoryList(): HistoryListBody {
        val dataTo =
            if (filterData.dataTo == 0L) Calendar.getInstance().timeInMillis else filterData.dataTo
        return HistoryListBody(
            page,
            filterData.absType,
            filterData.userName,
            filterData.contactNumber,
            filterData.agentName,
            filterData.tireBrand,
            filterData.dataFrom,
            dataTo
        )
    }
}