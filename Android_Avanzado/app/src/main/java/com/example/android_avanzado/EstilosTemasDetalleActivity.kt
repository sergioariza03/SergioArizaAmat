package com.example.android_avanzado

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class EstilosTemasDetalleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estilos_temas_detalle)

        findViewById<View>(R.id.btnVolver).setOnClickListener {
            finish()
        }
    }
}