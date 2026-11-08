package com.example.android_avanzado

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class CajaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val densidad = resources.displayMetrics.density
    private val lado = 50 * densidad

    private val pintura = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.RED }
    private val pinturaLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 3 * densidad
        style = Paint.Style.STROKE
    }

    private var cajaX = 0f
    private var cajaY = 0f
    private var posicionInicializada = false
    private var esRoja = true

    private var hayFling = false
    private var flingInicioX = 0f
    private var flingInicioY = 0f
    private var flingFinX = 0f
    private var flingFinY = 0f

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean = dentroDeCaja(e.x, e.y)

        override fun onScroll(
            e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float
        ): Boolean {
            cajaX -= distanceX
            cajaY -= distanceY
            invalidate()
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            performClick()
            return true
        }

        override fun onFling(
            e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float
        ): Boolean {
            if (e1 == null) return false
            flingInicioX = e1.x
            flingInicioY = e1.y
            flingFinX = e2.x
            flingFinY = e2.y
            hayFling = true

            cajaX = flingFinX - lado / 2f
            cajaY = flingFinY - lado / 2f
            invalidate()
            return true
        }
    }

    private val gestureDetector = GestureDetector(context, gestureListener)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (!posicionInicializada) {
            cajaX = (w - lado) / 2f
            cajaY = (h - lado) / 2f
            posicionInicializada = true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (hayFling) {
            canvas.drawLine(flingInicioX, flingInicioY, flingFinX, flingFinY, pinturaLinea)
        }
        canvas.drawRect(cajaX, cajaY, cajaX + lado, cajaY + lado, pintura)
    }

    private fun dentroDeCaja(x: Float, y: Float): Boolean =
        x in cajaX..(cajaX + lado) && y in cajaY..(cajaY + lado)

    override fun onTouchEvent(event: MotionEvent): Boolean =
        gestureDetector.onTouchEvent(event)

    override fun performClick(): Boolean {
        super.performClick()
        esRoja = !esRoja
        pintura.color = if (esRoja) Color.RED else Color.BLUE
        invalidate()
        return true
    }
}