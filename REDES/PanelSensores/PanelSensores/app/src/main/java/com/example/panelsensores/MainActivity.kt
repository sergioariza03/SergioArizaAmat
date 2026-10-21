
package com.example.panelsensores

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    // Servicio que permite acceder a los sensores del dispositivo.
    private lateinit var sensorManager: SensorManager

    // Sensores que utilizaremos en la práctica.
    private var acelerometro: Sensor? = null
    private var sensorLuz: Sensor? = null

    // Elementos de la interfaz.
    private lateinit var tvX: TextView
    private lateinit var tvY: TextView
    private lateinit var tvZ: TextView
    private lateinit var tvAgitacion: TextView
    private lateinit var tvLuz: TextView
    private lateinit var tvAmbiente: TextView

    // Variables para limitar los avisos de agitación.
    private var ultimaAgitacion = 0L
    private val umbralAgitacion = 3.0f
    private val intervaloMinimo = 100L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Conecto los elementos Kotlin con las vistas XML.
        tvX = findViewById(R.id.tvX)
        tvY = findViewById(R.id.tvY)
        tvZ = findViewById(R.id.tvZ)
        tvAgitacion = findViewById(R.id.tvAgitacion)
        tvLuz = findViewById(R.id.tvLuz)
        tvAmbiente = findViewById(R.id.tvAmbiente)

        // Obtengo el servicio de sensores del sistema.
        sensorManager = getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager

        // Compruebo si los sensores están disponibles.
        acelerometro = sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER
        )

        sensorLuz = sensorManager.getDefaultSensor(
            Sensor.TYPE_LIGHT
        )

        if (acelerometro == null) {
            tvX.text = "Acelerómetro no disponible"
            tvY.text = ""
            tvZ.text = ""
        }

        if (sensorLuz == null) {
            tvLuz.text = "Sensor de luz no disponible"
            tvAmbiente.text =
                "Este emulador no ofrece un sensor de luz"
        }
    }

    override fun onResume() {
        super.onResume()

        // Registramos el acelerómetro cuando la aplicación está activa.
        acelerometro?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }

        // Registramos también el sensor de luz.
        sensorLuz?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent) {

        // Procesamos las lecturas del acelerómetro.
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // Mostramos las tres componentes en m/s².
            tvX.text = "X: %.2f m/s²".format(x)
            tvY.text = "Y: %.2f m/s²".format(y)
            tvZ.text = "Z: %.2f m/s²".format(z)

            // Calculo el módulo de la aceleración.
            val modulo = sqrt(x * x + y * y + z * z)

            // Resto la gravedad aproximada para detectar movimientos.
            val aceleracionMovimiento =
                kotlin.math.abs(modulo - SensorManager.GRAVITY_EARTH)

            val ahora = System.currentTimeMillis()

            // aviso si se supera el umbral y han pasado 100 ms.
            if (aceleracionMovimiento > umbralAgitacion &&
                ahora - ultimaAgitacion >= intervaloMinimo
            ) {
                ultimaAgitacion = ahora
                tvAgitacion.text = "¡AGITACIÓN DETECTADA!"
                tvAgitacion.setBackgroundColor(
                    android.graphics.Color.rgb(255, 180, 180)
                )
            } else if (aceleracionMovimiento <= umbralAgitacion) {
                tvAgitacion.text = "Estado: sin agitación"
                tvAgitacion.setBackgroundColor(
                    android.graphics.Color.rgb(224, 242, 233)
                )
            }
        }

        // lecturas del sensor de luz.
        if (event.sensor.type == Sensor.TYPE_LIGHT) {

            val lux = event.values[0]

            tvLuz.text = "Iluminación: %.1f lux".format(lux)

            // Clasificar el ambiente según la iluminación recibida.
            when {
                lux < 50 -> {
                    tvAmbiente.text = "Ambiente: oscuro"
                    tvAmbiente.setBackgroundColor(
                        android.graphics.Color.rgb(210, 210, 230)
                    )
                }

                lux < 500 -> {
                    tvAmbiente.text = "Ambiente: iluminación normal"
                    tvAmbiente.setBackgroundColor(
                        android.graphics.Color.rgb(224, 242, 233)
                    )
                }

                else -> {
                    tvAmbiente.text = "Ambiente: muy iluminado"
                    tvAmbiente.setBackgroundColor(
                        android.graphics.Color.rgb(255, 230, 160)
                    )
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No necesitamos realizar acciones cuando cambia la precisión.
    }

    override fun onPause() {
        super.onPause()

        // Liberamos los sensores al salir de primer plano.
        sensorManager.unregisterListener(this)
    }
}
