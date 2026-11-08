package com.example.android_avanzado

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android_avanzado.databinding.ActivityDrawablesBinding



class DrawablesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrawablesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawablesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
