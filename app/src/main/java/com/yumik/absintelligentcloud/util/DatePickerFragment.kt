package com.yumik.absintelligentcloud.util

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(
    private val dateSetListener: OnDateSetListener,
    private val time: Calendar = Calendar.getInstance()
) : DialogFragment(),
    DatePickerDialog.OnDateSetListener {

    companion object {
        private const val TAG = "DatePickerFragment"
    }

    private var maxDate = Calendar.getInstance().timeInMillis
    lateinit var datePickerDialog: DatePickerDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = time.get(Calendar.YEAR)
        val month = time.get(Calendar.MONTH)
        val day = time.get(Calendar.DAY_OF_MONTH)
        datePickerDialog = DatePickerDialog(requireActivity(), this, year, month, day).apply {
            datePicker.maxDate = maxDate
        }
        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val setDate = Calendar.getInstance().run {
            set(year, month, dayOfMonth)
            timeInMillis
        }
        if (dateSetListener.callback(setDate))
            time.set(year, month, dayOfMonth)
    }

    interface OnDateSetListener {
        fun callback(setDate: Long): Boolean {
            return true
        }
    }
}