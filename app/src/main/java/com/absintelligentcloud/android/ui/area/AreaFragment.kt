package com.absintelligentcloud.android.ui.area

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.absintelligentcloud.android.ABSIntelligentCloudApplication
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.Repository
import com.absintelligentcloud.android.logic.model.AreaResponse
import kotlinx.android.synthetic.main.activity_manage.*
import kotlinx.android.synthetic.main.fragment_area.*
import kotlinx.android.synthetic.main.fragment_area.areaSpin
import kotlinx.android.synthetic.main.fragment_device.*

class AreaFragment : Fragment() {

    private val viewModel by lazy { ViewModelProvider(this).get(AreaViewModel::class.java) }

    private lateinit var adapter: AreaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_area, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = AreaAdapter(this, viewModel.areaList)
        areaSpin.adapter = adapter
        areaSpin.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val areaName = viewModel.areaList[position].areaName
                Toast.makeText(activity, "You click item is $areaName", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(activity, "You click item is nothing.", Toast.LENGTH_SHORT).show()
            }

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }
        }
        viewModel.areaLiveData.observe(viewLifecycleOwner, { result ->
            Log.d("AreaFragment", result.toString())
            val areas = result.getOrNull()
            if (areas != null) {
                viewModel.areaList.clear()
                viewModel.areaList.addAll(areas)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未获取到区域", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.getAreaList(true)
    }
}