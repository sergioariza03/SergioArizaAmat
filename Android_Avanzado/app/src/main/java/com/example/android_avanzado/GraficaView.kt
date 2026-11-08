package com.example.android_avanzado

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class GraficaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr){

    private val pinturaRoja = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.RED }
    private val pinturaAzul = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLUE }
    private val area = RectF()

    var percentage: Int = 0
        set(value){
            field = value.coerceIn(0, 100)
            invalidate()
        }

    init{
        val a = context.obtainStyledAttributes(attrs, R.styleable.GraficaView)
        try{
            percentage = a.getInt(R.styleable.GraficaView_percentage, 0)
        } finally {
            a.recycle()
        }
    }

    override  fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int){
        val deseado = (200 * resources.displayMetrics.density).toInt()
        val ancho = resolveSize(deseado, widthMeasureSpec)
        val alto = resolveSize(deseado,heightMeasureSpec)
        setMeasuredDimension(ancho, alto)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val anchoUtil = width - paddingLeft - paddingRight
        val altoUtil = height - paddingTop - paddingBottom
        val diametro = minOf(anchoUtil, altoUtil).toFloat()
        if(diametro <= 0f) return

        val izquierda = paddingLeft + (anchoUtil - diametro) / 2f
        val arriba = paddingTop + (altoUtil - diametro)/2f
        area.set(izquierda, arriba, izquierda + diametro, arriba + diametro)

        val barrido = 360f * percentage/100f

        canvas.drawArc(area, -90f, barrido, true, pinturaRoja)
        canvas.drawArc(area, -90f + barrido, 360f - barrido, true, pinturaAzul)
    }
}