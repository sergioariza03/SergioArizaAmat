package com.example.android_avanzado


import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TextViewCitas @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
): AppCompatTextView(context, attrs, defStyleAttr){

    private val citas = listOf(
        "Solo sé que no sé nada. — Sócrates",
        "Pienso, luego existo. — René Descartes",
        "La imaginación es más importante que el conocimiento. — Albert Einstein",
        "Somos lo que hacemos repetidamente. — Aristóteles"
    )

    init{
        mostrarCitaAleatoria()
        setOnClickListener { mostrarCitaAleatoria() }
    }

    private fun mostrarCitaAleatoria(){
        text = citas.filter { it != text.toString()}.random()
    }
}