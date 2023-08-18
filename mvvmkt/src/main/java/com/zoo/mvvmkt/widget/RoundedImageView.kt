package com.zoo.mvvmkt.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class RoundedImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
    private val path = Path()
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        val radius = width.toFloat() / 2 // 圆角半径为宽度的一半
        path.addRoundRect(rect, radius, radius, Path.Direction.CW)
        canvas.clipPath(path)
        super.onDraw(canvas)
    }
}