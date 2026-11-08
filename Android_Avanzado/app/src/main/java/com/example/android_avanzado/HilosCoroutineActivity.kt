package com.example.android_avanzado

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HilosCoroutineActivity : AppCompatActivity() {

    private lateinit var tvContador: TextView
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hilos_coroutine)

        tvContador = findViewById(R.id.tvContador)
        findViewById<Button>(R.id.btnIniciar).setOnClickListener { iniciarCuenta() }
    }

    private fun iniciarCuenta() {
        job?.cancel()

        job = lifecycleScope.launch {
            for (i in 10 downTo 1) {
                tvContador.text = i.toString()
                delay(1000)
            }
            tvContador.text = getString(R.string.contador_terminado)
        }
    }
}