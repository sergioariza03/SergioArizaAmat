package com.example.android_avanzado


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.android_avanzado.databinding.ActivityNotificacionesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class NotificacionesActivity : AppCompatActivity() {

    companion object {
        private const val CANAL_ID = "canal_tareas"
        private const val NOTIFICACION_ID = 1
    }

    private lateinit var binding: ActivityNotificacionesBinding

    private val tareas = mutableListOf<String>()
    private var colorElegido = 0
    private var tamanoElegido = 1
    private var contador = 0

    private val pedirPermiso =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
            if (concedido) {
                iniciarTarea()
            } else {
                Snackbar.make(binding.root, R.string.permiso_denegado, Snackbar.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        crearCanal()

        binding.btnToast.setOnClickListener { mostrarToast() }
        binding.btnTarea.setOnClickListener { anadirTarea() }
        binding.btnColor.setOnClickListener { mostrarDialogoColor() }
        binding.btnTamano.setOnClickListener { mostrarDialogoTamano() }
        binding.btnIniciar.setOnClickListener { alPulsarIniciar() }
        binding.btnDetener.setOnClickListener { alPulsarDetener() }
    }

    // ---------- Ejercicio 1: Toast ----------

    private fun mostrarToast() {
        val texto = binding.etToast.text.toString().trim()
        val mensaje = if (texto.isEmpty()) getString(R.string.escribe_texto) else texto
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        binding.etToast.text.clear()
    }

    // ---------- Ejercicio 2: Snackbar con Deshacer ----------

    private fun anadirTarea() {
        val texto = binding.etTarea.text.toString().trim()
        if (texto.isEmpty()) {
            Snackbar.make(binding.root, R.string.escribe_texto, Snackbar.LENGTH_SHORT).show()
            return
        }

        tareas.add(texto)
        val posicion = tareas.lastIndex
        binding.etTarea.text.clear()
        actualizarLista()

        Snackbar.make(binding.root, R.string.tarea_anadida, Snackbar.LENGTH_LONG)
            .setAction(R.string.deshacer) {
                if (posicion in tareas.indices) {
                    tareas.removeAt(posicion)
                    actualizarLista()
                }
            }
            .show()
    }

    private fun actualizarLista() {
        binding.tvTareas.text = tareas.joinToString("\n") { "• $it" }
    }

    // ---------- Ejercicio 3: diálogos de selección ----------

    private fun mostrarDialogoColor() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Color")
            .setSingleChoiceItems(
                resources.getStringArray(R.array.opciones_color), colorElegido
            ) { dialogo, opcion ->
                colorElegido = opcion
                aplicarColor(opcion)
                dialogo.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun aplicarColor(opcion: Int) {
        val (fondo, letra) = when (opcion) {
            0 -> R.color.blanco to R.color.negro
            1 -> R.color.negro to R.color.blanco
            else -> R.color.negro to R.color.verde_texto
        }
        binding.tvEjemplo.setBackgroundColor(ContextCompat.getColor(applicationContext, fondo))
        binding.tvEjemplo.setTextColor(ContextCompat.getColor(applicationContext, letra))
    }

    private fun mostrarDialogoTamano() {
        val tamanos = floatArrayOf(14f, 16f, 20f)
        MaterialAlertDialogBuilder(this)
            .setTitle("Tamaño")
            .setSingleChoiceItems(
                resources.getStringArray(R.array.opciones_tamano), tamanoElegido
            ) { dialogo, opcion ->
                tamanoElegido = opcion
                binding.tvEjemplo.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamanos[opcion])
                dialogo.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ---------- Ejercicio 4: notificaciones del sistema ----------

    private fun crearCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID, "Tareas", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notificaciones de tareas activas" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
        }
    }

    private fun tienePermiso(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

    private fun alPulsarIniciar() {
        if (tienePermiso()) {
            iniciarTarea()
        } else {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun iniciarTarea() {
        contador++
        actualizarNotificacion()
    }

    private fun alPulsarDetener() {
        if (contador > 0) {
            contador--
            actualizarNotificacion()
        }
    }

    private fun actualizarNotificacion() {
        val manager = getSystemService(NotificationManager::class.java)
        if (contador == 0) {
            manager.cancel(NOTIFICACION_ID)
            return
        }

        val intent = Intent(this, NotificacionesActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(this, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle("Android Avanzado")
            .setContentText(getString(R.string.tareas_iniciadas, contador))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(NOTIFICACION_ID, notificacion)
    }
}