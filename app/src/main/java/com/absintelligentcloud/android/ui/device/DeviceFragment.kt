package com.absintelligentcloud.android.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.absintelligentcloud.android.R
import kotlinx.android.synthetic.main.fragment_device.*

class DeviceFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(DeviceViewModel::class.java) }

    private lateinit var adapter: DeviceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        adapter = DeviceAdapter(this, viewModel.deviceList)
        recyclerView.adapter = adapter
        searchDeviceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.getDevice(content)
            } else {
                recyclerView.visibility = View.GONE
                bgImageView.visibility = View.VISIBLE
                viewModel.deviceList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.deviceLiveData.observe(viewLifecycleOwner, { result ->
            val devices = result.getOrNull()
            if (devices != null) {
                recyclerView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                viewModel.deviceList.clear()
                viewModel.deviceList.addAll(devices)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "查无此设备", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}


