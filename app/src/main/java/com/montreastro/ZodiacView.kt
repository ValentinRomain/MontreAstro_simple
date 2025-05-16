package com.montreastro

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class ZodiacView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val zodiacPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.zodiac_background)
        style = Paint.Style.FILL
    }

    private val moonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.moon_color)
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    private val zodiacRect = RectF()
    private var moonPositionArcSeconds = 0.0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = 50f
        zodiacRect.set(
            padding,
            padding,
            width.toFloat() - padding,
            height.toFloat() - padding
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw zodiac circle
        canvas.drawArc(zodiacRect, 0f, 360f, true, zodiacPaint)

        // Draw moon position
        val moonAngle = (moonPositionArcSeconds / 60.0 * 360.0).toFloat()
        val moonRadius = 30f
        val centerX = zodiacRect.centerX()
        val centerY = zodiacRect.centerY()
        val radius = (zodiacRect.width() / 2) - 20f

        val moonX = centerX + radius * Math.cos(Math.toRadians(moonAngle.toDouble())).toFloat()
        val moonY = centerY + radius * Math.sin(Math.toRadians(moonAngle.toDouble())).toFloat()

        canvas.drawCircle(moonX, moonY, moonRadius, moonPaint)

        // Draw degree markers
        for (i in 0 until 60) {
            val angle = i * 6f
            val startX = centerX + (radius - 10) * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val startY = centerY + (radius - 10) * Math.sin(Math.toRadians(angle.toDouble())).toFloat()
            val endX = centerX + radius * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val endY = centerY + radius * Math.sin(Math.toRadians(angle.toDouble())).toFloat()

            val markerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                strokeWidth = if (i % 5 == 0) 5f else 2f
            }

            canvas.drawLine(startX, startY, endX, endY, markerPaint)

            if (i % 5 == 0) {
                val textX = centerX + (radius - 40) * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
                val textY = centerY + (radius - 40) * Math.sin(Math.toRadians(angle.toDouble())).toFloat() + 10f
                canvas.drawText(i.toString(), textX, textY, textPaint)
            }
        }
    }

    fun setMoonPosition(arcSeconds: Double) {
        moonPositionArcSeconds = arcSeconds
        invalidate()
    }
}
