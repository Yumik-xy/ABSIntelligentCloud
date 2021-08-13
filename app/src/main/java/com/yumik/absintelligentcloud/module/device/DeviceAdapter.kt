package com.yumik.absintelligentcloud.module.device

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.yumik.absintelligentcloud.databinding.AdapterDeviceDangerBinding
import com.yumik.absintelligentcloud.databinding.AdapterDeviceNormalBinding
import com.yumik.absintelligentcloud.logic.model.Device
import com.yumik.absintelligentcloud.ui.device.DeviceActivity
import com.yumik.absintelligentcloud.util.setOnUnShakeClickListener

class DeviceAdapter(private val context: Context, private val danger: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class NormalViewHolder(itemView: AdapterDeviceNormalBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val view = itemView
    }

    inner class DangerViewHolder(itemView: AdapterDeviceDangerBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val view = itemView
    }

    private val dataList =
        SortedList(Device::class.java, object : SortedListAdapterCallback<Device>(this) {
            override fun compare(o1: Device, o2: Device): Int {
                return o2.updateAt.compareTo(o1.updateAt) // 倒序，id更大的在上面
//                return o1.productionDate.compareTo(o2.productionDate) // 顺序，id更大的在下面
            }

            override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(item1: Device, item2: Device): Boolean {
                return item1.id == item2.id
            }

            override fun onInserted(position: Int, count: Int) {
                super.onInserted(position, count)
                Log.d("DeviceAdapter", "position:$position, count:$count")
            }
        })

    fun add(item: Device) {
        dataList.add(item)
    }

    fun add(list: List<Device>) {
        dataList.addAll(list)
    }

    fun reAdd(list: List<Device>) {
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
        return if (danger) {
            val binding =
                AdapterDeviceDangerBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            DangerViewHolder(binding)
        } else {
            val binding =
                AdapterDeviceNormalBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
            NormalViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = dataList.size()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]
        if (danger) {
            (holder as DangerViewHolder).view.apply {
                deviceId.text = item.deviceId
                userName.text = item.userName
                contactNumber.text = item.contactNumber
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
                userName.text = item.userName
                contactNumber.text = item.contactNumber
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