package com.yumik.absintelligentcloud.module.history

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.yumik.absintelligentcloud.databinding.AdapterHistoryDangerBinding
import com.yumik.absintelligentcloud.databinding.AdapterHistoryNormalBinding
import com.yumik.absintelligentcloud.logic.model.History
import com.yumik.absintelligentcloud.ui.device.DeviceActivity
import com.yumik.absintelligentcloud.util.setOnUnShakeClickListener

class HistoryAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SOLVE = 0
        private const val TYPE_FAULT = 1
    }

    inner class NormalViewHolder(itemView: AdapterHistoryNormalBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val view = itemView
    }

    inner class DangerViewHolder(itemView: AdapterHistoryDangerBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val view = itemView
    }

    private val dataList =
        SortedList(History::class.java, object : SortedListAdapterCallback<History>(this) {
            override fun compare(o1: History, o2: History): Int {
                return o2.id.compareTo(o1.id) // 倒序，id更大的在上面
//                return o1.id.compareTo(o2.id) // 顺序，id更大的在下面
            }

            override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(item1: History, item2: History): Boolean {
                return item1.id == item2.id
            }
        })

    fun add(item: History) {
        dataList.add(item)
    }

    fun add(list: List<History>) {
        dataList.addAll(list)
    }

    fun reAdd(list: List<History>) {
        var remove = 1
        for (i in 1..dataList.size()) {
            val data = dataList.get(i - remove)
            if (data !in list) {
                remove++
                dataList.remove(data)
            }
        }
        dataList.addAll(list)
    }

    fun clear() {
        dataList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_FAULT -> {
                val binding =
                    AdapterHistoryDangerBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                DangerViewHolder(binding)
            }
            else -> {
                val binding =
                    AdapterHistoryNormalBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                NormalViewHolder(binding)
            }
        }
    }

    override fun getItemCount(): Int = dataList.size()

    override fun getItemViewType(position: Int): Int {
        val item = dataList[position]
        return if (item.faultName == "故障解除") TYPE_SOLVE else TYPE_FAULT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        if (getItemViewType(position) == TYPE_FAULT) {
            (holder as DangerViewHolder).view.apply {
                deviceId.text = item.deviceId
//                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                productionDate.text = item.monitorDate
                historyStatus.text = item.faultName
                // area.text = item.area
                container.setOnUnShakeClickListener {
                    val intent = Intent(context, DeviceActivity::class.java).apply {
                        putExtra("deviceId", item.deviceId)
                    }
                    context.startActivity(intent)
                }
            }
        } else {
            (holder as NormalViewHolder).view.apply {
                deviceId.text = item.deviceId
//                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                productionDate.text = item.monitorDate
                historyStatus.text = item.faultName
                // area.text = item.area
                container.setOnUnShakeClickListener {
                    val intent = Intent(context, DeviceActivity::class.java).apply {
                        putExtra("deviceId", item.deviceId)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
}