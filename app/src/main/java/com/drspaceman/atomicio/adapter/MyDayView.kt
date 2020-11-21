package com.drspaceman.atomicio.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.annotation.VisibleForTesting
import com.linkedin.android.tachyon.DayView
import java.util.*

class MyDayView : DayView {
    var paint: Paint

    constructor(context: Context) : super(context) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = -0x10000
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = -0x10000
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = -0x10000
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val d = Date()
        val h = d.hours
        val min = d.minutes
        val hourTop = getHourTop(h).toFloat()
        val hourBottom = getHourBottom(h).toFloat()
        val pixelsPerMin = (hourBottom - hourTop) / 60.0f
        val y = (hourTop + pixelsPerMin * min).toInt() + 2
        canvas.drawLine(0f, y.toFloat(), canvas.width.toFloat(), y.toFloat(), paint)
    }
}