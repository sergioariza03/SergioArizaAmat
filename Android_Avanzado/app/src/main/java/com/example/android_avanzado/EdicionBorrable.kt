package com.example.android_avanzado

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.android_avanzado.databinding.EdicionBorrableBinding


class EdicionBorrable @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr){
    private val binding = EdicionBorrableBinding.inflate(LayoutInflater.from(context), this)

    init{
        orientation = HORIZONTAL
        gravity = Gravity.CLIP_VERTICAL
        binding.btnBorrar.setOnClickListener {
            binding.editTexto.text.clear()
        }
    }
}