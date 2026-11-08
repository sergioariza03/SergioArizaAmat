package com.example.android_avanzado


import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.android_avanzado.databinding.ActivityPersonalizacionBinding

class PersonalizacionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalizacionBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalizacionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actualizarGrafica(binding.seekPorcentaje.progress)

        binding.seekPorcentaje.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                actualizarGrafica(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun actualizarGrafica(porcentaje: Int) {
        binding.grafica.percentage = porcentaje
        binding.grafica.contentDescription = getString(R.string.grafica_descripcion, porcentaje)
    }
}