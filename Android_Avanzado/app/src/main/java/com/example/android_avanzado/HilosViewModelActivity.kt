package com.example.android_avanzado

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class HilosViewModelActivity : AppCompatActivity() {

    private val viewModel: TemporizadorViewModel by viewModels()
    private lateinit var tvContador: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hilos_view_model)

        tvContador = findViewById(R.id.tvContador)
        findViewById<Button>(R.id.btnIniciar).setOnClickListener { viewModel.iniciar() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.estado.collect { estado -> mostrar(estado) }
            }
        }
    }

    private fun mostrar(estado: EstadoTemporizador) {
        tvContador.text = when (estado) {
            EstadoTemporizador.Inactivo -> ""
            is EstadoTemporizador.Contando -> estado.numero.toString()
            EstadoTemporizador.Terminado -> getString(R.string.contador_terminado)
        }
    }
}