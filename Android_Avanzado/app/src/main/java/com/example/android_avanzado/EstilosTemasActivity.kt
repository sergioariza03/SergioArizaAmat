package com.example.android_avanzado

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class EstilosTemasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estilos_temas)

        findViewById<View>(R.id.btnContinuar).setOnClickListener {
            startActivity(Intent(this, EstilosTemasDetalleActivity::class.java))
        }
    }
}