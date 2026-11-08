package com.example.android_avanzado

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android_avanzado.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDrawables.setOnClickListener {
            startActivity(Intent(this, DrawablesActivity::class.java))
        }

        binding.btnComponentes.setOnClickListener {
            startActivity(Intent(this, PersonalizacionActivity::class.java))
        }

        binding.btnNotificaciones.setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
        }

        binding.btnPantallaTactil.setOnClickListener {
            startActivity(Intent(this, PantallaTactilActivity::class.java))
        }

        binding.btnEstilosTemas.setOnClickListener {
            startActivity(Intent(this, EstilosTemasActivity::class.java))
        }

        binding.btnHilos.setOnClickListener {
            startActivity(Intent(this, HilosActivity::class.java))
        }
    }
}