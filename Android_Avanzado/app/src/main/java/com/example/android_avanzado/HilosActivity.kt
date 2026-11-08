package com.example.android_avanzado

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HilosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hilos)

        findViewById<Button>(R.id.btnThread).setOnClickListener {
            startActivity(Intent(this, HilosThreadActivity::class.java))
        }
        findViewById<Button>(R.id.btnCoroutine).setOnClickListener {
            startActivity(Intent(this, HilosCoroutineActivity::class.java))
        }
        findViewById<Button>(R.id.btnViewModel).setOnClickListener {
            startActivity(Intent(this, HilosViewModelActivity::class.java))
        }

        // ---- PRUEBA DE BLOQUEO: comentar este bloque después de comprobarlo ----
        val tvBloqueo = findViewById<TextView>(R.id.tvBloqueo)
        findViewById<Button>(R.id.btnBloqueo).setOnClickListener {
            for (i in 3 downTo 1) {
                tvBloqueo.text = i.toString()
                Thread.sleep(1000)
            }
            tvBloqueo.text = getString(R.string.contador_terminado)
        }
        // -----------------------------------------------------------------------
    }
}