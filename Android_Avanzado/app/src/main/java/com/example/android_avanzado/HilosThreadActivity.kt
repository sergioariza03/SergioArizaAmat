package com.example.android_avanzado

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HilosThreadActivity : AppCompatActivity() {

    private lateinit var tvContador: TextView
    private var hilo: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hilos_thread)

        tvContador = findViewById(R.id.tvContador)
        findViewById<Button>(R.id.btnIniciar).setOnClickListener { iniciarCuenta() }
    }

    private fun iniciarCuenta() {
        hilo?.interrupt()

        hilo = Thread {
            try {
                for (i in 10 downTo 1) {
                    runOnUiThread { tvContador.text = i.toString() }
                    Thread.sleep(1000)
                }
                runOnUiThread { tvContador.text = getString(R.string.contador_terminado) }
            } catch (e: InterruptedException) {
                // Nos han interrumpido (reinicio o actividad destruida): salimos sin más
            }
        }.also { it.start() }
    }

    override fun onDestroy() {
        hilo?.interrupt()
        super.onDestroy()
    }
}