package com.absintelligentcloud.android.ui.history

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.DeviceBody
import com.absintelligentcloud.android.logic.model.HistoryBody
import com.absintelligentcloud.android.logic.util.OnLoadMoreListener
import com.absintelligentcloud.android.ui.area.AreaFragment
import com.absintelligentcloud.android.ui.device.DeviceAdapter
import kotlinx.android.synthetic.main.activity_device.*
import kotlinx.android.synthetic.main.activity_device.toolbar
import kotlinx.android.synthetic.main.activity_history.*
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this).get(HistoryViewModel::class.java) }

    private lateinit var adapter: HistoryAdapter

    private var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)

        val layoutManager = LinearLayoutManager(this)
        historyRecyclerView.layoutManager = layoutManager
        adapter = HistoryAdapter(this, viewModel.historyList)
        historyRecyclerView.adapter = adapter

        showHistorySearchBtn.setOnClickListener {
            historyDrawerLayout.openDrawer(GravityCompat.END)
        }

        backHistoryBtn.setOnClickListener {
            finish()
        }

        recordDateFromHistoryEdit.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this, { view, year, month, dayOfMonth ->
                    @SuppressLint("SetTextI18n")
                    recordDateFromHistoryEdit.text = "$year-${month + 1}-$dayOfMonth"
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        recordDateToHistoryEdit.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this, { view, year, month, dayOfMonth ->
                    @SuppressLint("SetTextI18n")
                    recordDateToHistoryEdit.text = "$year-${month + 1}-$dayOfMonth"
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        viewModel.historyLiveData.observe(this, { result ->
            val histories = result.getOrNull()
            Log.d("histories", histories.toString())
            if (histories != null && histories.isNotEmpty()) {
                historyRecyclerView.visibility = View.VISIBLE
                bgHistoryImageView.visibility = View.GONE
                if (page == 1) {
                    viewModel.historyList.clear()
                }
                viewModel.historyList.addAll(histories)
                adapter.notifyDataSetChanged()
            } else if (page != 1) {
                page = 1.coerceAtLeast(page - 1)
                Toast.makeText(this, "没有更多的记录了", Toast.LENGTH_SHORT).show()
            } else {
                historyRecyclerView.visibility = View.VISIBLE
                bgHistoryImageView.visibility = View.VISIBLE
                viewModel.historyList.clear()
                adapter.notifyDataSetChanged()
                page = 1.coerceAtLeast(page - 1)
                Toast.makeText(this, "暂无记录", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })

        historyRecyclerView.addOnScrollListener(object : OnLoadMoreListener() {
            override fun onLoadMore() {
                page += 1
                Log.d("recyclerView", "addOnScrollListener")
                getHistory()
            }
        })

        searchHistoryBtn.setOnClickListener {
            page = 1
            getHistory()
        }

        clearHistoryBtn.setOnClickListener {
            absTypeHistoryEdit.text.clear()
            userNameHistoryEdit.text.clear()
            contactNumberHistoryEdit.text.clear()
            agentNameHistoryEdit.text.clear()
            tireBrandHistoryEdit.text.clear()
            recordDateFromHistoryEdit.text = ""
            recordDateToHistoryEdit.text = ""
            page = 1
        }

        getHistory()
    }

    private fun getHistory() {
        val absType = absTypeHistoryEdit.text.toString()
        val userName = userNameHistoryEdit.text.toString()
        val recordDateFromHistoryText = recordDateFromHistoryEdit.text
        var recordDateFromList = recordDateFromHistoryText.split("-")
        if (recordDateFromList[0].isEmpty()) recordDateFromList = listOf("1970", "1", "1")
        val recordDateFrom = GregorianCalendar(
            recordDateFromList[0].toInt(),
            recordDateFromList[1].toInt() - 1,
            recordDateFromList[2].toInt()
        ).timeInMillis
        val recordDateToHistoryText = recordDateToHistoryEdit.text
        var recordDateToList = recordDateToHistoryText.split("-")
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        if (recordDateToList[0].isEmpty()) recordDateToList =
            listOf(
                calendar.get(Calendar.YEAR).toString(),
                (calendar.get(Calendar.MONTH) + 1).toString(),
                calendar.get(Calendar.DAY_OF_MONTH).toString()
            )
        val recordDateTo = GregorianCalendar(
            recordDateToList[0].toInt(),
            recordDateToList[1].toInt() - 1,
            recordDateToList[2].toInt()
        ).timeInMillis
        val contactNumber = contactNumberHistoryEdit.text.toString()
        val agentName = agentNameHistoryEdit.text.toString()
        val tireBrand = tireBrandHistoryEdit.text.toString()
        val historyBody =
            HistoryBody(
                absType,
                userName,
                contactNumber,
                agentName,
                tireBrand,
                recordDateFrom,
                recordDateTo,
                page
            )
        Log.d("getHistory", historyBody.toString())
        viewModel.getHistory(historyBody)
        historyDrawerLayout.closeDrawer(GravityCompat.END)
    }
}