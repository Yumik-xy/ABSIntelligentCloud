package com.yumik.statusbar

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.yumik.statusbar.R
import com.yumik.statusbar.databinding.StatusBarBinding

class StatusBar(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {

    private var binding: StatusBarBinding =
        StatusBarBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        // 获取组件参数
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusBar)
        val title =
            typedArray.getString(R.styleable.StatusBar_title)
        val backButtonShow =
            typedArray.getBoolean(R.styleable.StatusBar_backButtonShow, true)
        val backButtonRes =
            typedArray.getDrawable(R.styleable.StatusBar_backButtonRes)
        val functionButtonShow =
            typedArray.getBoolean(R.styleable.StatusBar_functionButtonShow, true)
        val functionButtonRes =
            typedArray.getDrawable(R.styleable.StatusBar_functionButtonRes)
        val backgroundColor =
            typedArray.getColor(
                R.styleable.StatusBar_backgroundColor,
                ContextCompat.getColor(context, R.color.white)
            )
        val color =
            typedArray.getColor(
                R.styleable.StatusBar_color,
                ContextCompat.getColor(context, R.color.text_1)
            )

        typedArray.recycle()

        binding.backButtonImageView.apply {
            visibility = if (backButtonShow) View.VISIBLE else View.GONE
            if (backButtonRes != null) setImageDrawable(backButtonRes)
            setColorFilter(color)
            setOnClickListener {
                statusBarClickListener?.backButtonClick()
            }
        }

        binding.titleTextView.apply {
            text = title
            setTextColor(color)
            gravity = Gravity.CENTER
        }

        binding.functionButtonImageView.apply {
            visibility = if (functionButtonShow) View.VISIBLE else View.GONE
            if (functionButtonRes != null) setImageDrawable(functionButtonRes)
            setColorFilter(color)
            setOnClickListener {
                statusBarClickListener?.functionButtonClick()
            }
        }
        setBackgroundColor(backgroundColor)
    }

    private var statusBarClickListener: StatusBarClickListener? = null

    public interface StatusBarClickListener {
        fun backButtonClick()
        fun functionButtonClick()
    }

    public fun setOnStatusBarClickListener(listener: StatusBarClickListener) {
        statusBarClickListener = listener
    }
}