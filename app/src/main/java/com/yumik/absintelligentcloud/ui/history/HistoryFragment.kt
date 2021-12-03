package com.yumik.absintelligentcloud.ui.history

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.yumik.absintelligentcloud.MainActivity
import com.yumik.absintelligentcloud.R
import com.yumik.absintelligentcloud.databinding.FragmentHistoryBinding
import com.yumik.absintelligentcloud.logic.model.FilterHistory
import com.yumik.absintelligentcloud.logic.network.Repository
import com.yumik.absintelligentcloud.logic.network.body.HistoryListBody
import com.yumik.absintelligentcloud.module.history.HistoryAdapter
import com.yumik.absintelligentcloud.ui.filter.FilterActivity
import com.yumik.absintelligentcloud.util.BaseFragment
import com.yumik.absintelligentcloud.util.OnLoadMoreListener
import com.yumik.absintelligentcloud.util.TipsUtil.showMySnackbar
import com.yumik.statusbar.StatusBar
import java.util.*
import kotlin.math.min

class HistoryFragment : BaseFragment<HistoryViewModel, FragmentHistoryBinding>() {

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

    override fun initViewAndData() {
        historyListAdapter = HistoryAdapter(requireContext())
        mainActivity = activity as MainActivity

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

    override fun initLiveData() {
        viewModel.historyList.observe(viewLifecycleOwner, {
            binding.swipeRefresh.isRefreshing = false
            if (it.code == Repository.ApiException.CODE_SUCCESS && it.data != null) {
                val data = it.data
                if (page == 1) {
                    historyListAdapter.reAdd(data.list)
                } else {
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
}