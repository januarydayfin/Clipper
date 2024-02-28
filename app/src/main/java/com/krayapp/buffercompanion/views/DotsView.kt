package com.krayapp.buffercompanion.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.krayapp.buffercompanion.R
import com.krayapp.buffercompanion.dp

class DotsView(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val DOT_INACTIVE = 0
    private val DOT_ACTIVE = 1

    private var dots: Array<View>? = null
    private var size = -1
    private var margin = -1

    init {
        initAttrs(context, attrs)
        orientation = HORIZONTAL
        if (dots != null) inflateWithDots()
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray: TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DotsView,
            0, 0
        )
        size = typedArray.getInteger(R.styleable.DotsView_indicator_size, -1)
        margin = typedArray.getInteger(R.styleable.DotsView_indicator_margin, -1)
    }

    private fun inflateWithDots() {
        dots?.forEach { dot ->
            addView(dot)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDotBackground(state: Int): Drawable? {
        return if (state == DOT_ACTIVE)
            ContextCompat.getDrawable(context, R.drawable.circle_active)
        else
            ContextCompat.getDrawable(context, R.drawable.circle_inactive)
    }

    private fun getParamsForIndicator(): LayoutParams {
        var size = size
        var margin = margin
        size = if (size == -1) 7 else size
        margin = if (margin == -1) 5 else margin
        val params = LayoutParams(dp(size), dp(size))
        params.setMargins(dp(margin), 0, dp(margin), 0)
        return params
    }

    fun boundWithPager(pager: ViewPager2) {
        initIndicator(pager.adapter?.itemCount ?: 0)

        pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setActivePosition(position)
            }
        })
    }

    private fun initIndicator(elements: Int) {
        val params = getParamsForIndicator()
        dots = Array(elements) { index ->
            val dot = View(context)
            dot.tag = "dot$index"
            dot.layoutParams = params
            dot.background = getDotBackground(DOT_INACTIVE)
            dot
        }
        inflateWithDots()
        setActivePosition(0)
    }

    private fun setActivePosition(position: Int) {
        dots?.forEachIndexed { index, dot ->
            dot.background = if (index == position) {
                getDotBackground(DOT_ACTIVE)
            } else {
                getDotBackground(DOT_INACTIVE)
            }
        }
    }
}
