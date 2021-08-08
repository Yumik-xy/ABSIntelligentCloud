package com.yumik.scrollview

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View.MeasureSpec
import androidx.core.widget.NestedScrollView

class ScrollView(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs) {

    private var maxHeight = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollView)
        maxHeight = typedArray.getDimension(R.styleable.ScrollView_maxHeight, 0F).toInt()
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val nowHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            (context as Activity).windowManager.currentWindowMetrics.bounds.height()
//        } else {
//            resources.displayMetrics.heightPixels
//        }
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize = if (heightSize <= maxHeight) heightSize else maxHeight
            }
            MeasureSpec.UNSPECIFIED -> {
                heightSize = if (heightSize <= maxHeight) heightSize else maxHeight
            }
            MeasureSpec.AT_MOST -> {
                heightSize = if (heightSize <= maxHeight) heightSize else maxHeight
            }
        }
        val maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode)
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec)
    }

}