package com.absintelligentcloud.android.ui.area

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.absintelligentcloud.android.R
import com.absintelligentcloud.android.logic.model.AreaResponse
import com.absintelligentcloud.android.logic.util.setSpinnerItemSelectedByValue
import com.absintelligentcloud.android.ui.detail.DetailActivity
import com.absintelligentcloud.android.ui.device.DeviceActivity
import com.absintelligentcloud.android.ui.manage.ManageActivity
import kotlinx.android.synthetic.main.fragment_area.areaSpin

class AreaFragment : Fragment() {

    lateinit var areaId: String

    val viewModel by lazy { ViewModelProvider(this).get(AreaViewModel::class.java) }

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
                val area = viewModel.areaList[position]
                areaId = area.areaId
                if (activity is ManageActivity) {
                    viewModel.setAreaNow(area)
                    viewModel.saveArea(area)
                }
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
                if (activity !is DetailActivity) {
                    var newAreas = listOf(AreaResponse.Data("", " 全部 "))
                    newAreas = newAreas.plus(areas)
                    viewModel.areaList.addAll(newAreas)
                } else {
                    viewModel.areaList.addAll(areas)
                }
                adapter.notifyDataSetChanged()
                if (viewModel.isAreaSaved() && activity is ManageActivity) {
                    setSpinnerItemSelectedByValue(areaSpin, viewModel.getSavedArea())
                }
            } else {
                Toast.makeText(activity, "未获取到区域", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.getAreaList(true)
    }
}