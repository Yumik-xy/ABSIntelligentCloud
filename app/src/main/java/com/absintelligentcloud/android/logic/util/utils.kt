package com.absintelligentcloud.android.logic.util

import android.widget.Spinner

fun setSpinnerItemSelectedByValue(spinner: Spinner, item: Any) {
    val adapter = spinner.adapter
    for (i in 0 until adapter.count - 1) {
        if (spinner.getItemAtPosition(i) == item) {
            spinner.setSelection(i, true)
            break
        }
    }
}
