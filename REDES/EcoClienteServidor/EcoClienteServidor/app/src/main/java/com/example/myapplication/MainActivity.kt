package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket

class MainActivity : AppCompatActivity() {
    // Permite actualizar la interfaz desde los hilos secundarios.
    private val handler = Handler(Looper.getMainLooper())

    // Referencias a los sockets del servidor y del cliente.
    @Volatile
    private var serverSocket: ServerSocket? = null

    @Volatile
    private var connectedClient: Socket? = null

    @Volatile
    private var clientSocket: Socket? = null

    @Volatile
    private var clientWriter: PrintWriter? = null

    @Volatile
    private var serverRunning = false

    // Evita que dos conexiones del cliente se inicien simultáneamente.
    @Volatile
    private var clientConnecting = false

    private lateinit var  panelServidor: LinearLayout
    private lateinit var panelCliente: LinearLayout

    private lateinit var etPuertoServidor: EditText
    private lateinit var etIpCliente: EditText
    private lateinit var etPuertoCliente: EditText
    private lateinit var etMensaje: EditText

    private lateinit var tvIpServidor: TextView
    private lateinit var tvEstadoServidor: TextView
    private lateinit var tvMensajesServidor: TextView
    private lateinit var tvEstadoCliente: TextView
    private lateinit var tvRespuesta: TextView

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Recupera los componentes definidos en activity_main.xml.
        panelServidor = findViewById(R.id.panelServidor)
        panelCliente = findViewById(R.id.panelCliente)

        etPuertoServidor = findViewById(R.id.etPuertoServidor)
        etIpCliente = findViewById(R.id.etIpCliente)
        etPuertoCliente = findViewById(R.id.etPuertoCliente)
        etMensaje = findViewById(R.id.etMensaje)

        tvIpServidor = findViewById(R.id.tvIpServidor)
        tvEstadoServidor = findViewById(R.id.tvEstadoServidor)
        tvMensajesServidor = findViewById(R.id.tvMensajesServidor)
        tvEstadoCliente = findViewById(R.id.tvEstadoCliente)
        tvRespuesta = findViewById(R.id.tvRespuesta)

        // Cambia entre los dos paneles sin detener el servidor.
        findViewById<Button>(R.id.btnModoServidor).setOnClickListener {
            panelServidor.visibility = android.view.View.VISIBLE
            panelCliente.visibility = android.view.View.GONE
        }

        findViewById<Button>(R.id.btnModoCliente).setOnClickListener {
            panelServidor.visibility = android.view.View.GONE
            panelCliente.visibility = android.view.View.VISIBLE
        }

        findViewById<Button>(R.id.btnIniciarServidor).setOnClickListener {
            iniciarServidor()
        }

        findViewById<Button>(R.id.btnDetenerServidor).setOnClickListener {
            detenerServidor()
        }

        findViewById<Button>(R.id.btnConectar).setOnClickListener {
            conectarCliente()
        }

        findViewById<Button>(R.id.btnDesconectar).setOnClickListener {
            desconectarCliente()
        }

        findViewById<Button>(R.id.btnEnviar).setOnClickListener {
            enviarMensaje()
        }

        // Muestra una IP local orientativa para facilitar las pruebas.
        tvIpServidor.text = "IP del dispositivo: ${obtenerIpLocal()}"
    }

    // Inicia el servidor en un hilo independiente.
    private fun iniciarServidor() {
        if (serverRunning) {
            tvEstadoServidor.text = "El servidor ya está iniciado."
            return
        }

        val puerto = etPuertoServidor.text.toString().toIntOrNull()

        if (puerto == null || puerto !in 1..65535) {
            tvEstadoServidor.text = "Introduce un puerto válido (1-65535)."
            return
        }

        serverRunning = true
        tvEstadoServidor.text = "Iniciando servidor..."

        Thread {
            try {
                // Escucha conexiones entrantes en el puerto indicado.
                val servidor = ServerSocket(puerto)
                serverSocket = servidor

                handler.post {
                    tvEstadoServidor.text =
                        "Servidor escuchando en el puerto $puerto."
                    tvIpServidor.text =
                        "IP del dispositivo: ${obtenerIpLocal()}"
                }

                // Acepta clientes hasta que se detenga el servidor.
                while (serverRunning) {
                    try {
                        val socket = servidor.accept()
                        connectedClient = socket

                        handler.post {
                            tvEstadoServidor.text =
                                "Cliente conectado: ${socket.inetAddress.hostAddress}"
                        }

                        // Cada línea recibida se transforma y se devuelve.
                        socket.use { conexion ->
                            val lector = BufferedReader(
                                InputStreamReader(conexion.getInputStream())
                            )
                            val escritor = PrintWriter(
                                conexion.getOutputStream(),
                                true
                            )

                            var mensaje: String?

                            // Lee los mensajes hasta que el cliente cierre la conexión.
                            while (serverRunning) {
                                val recibido = lector.readLine() ?: break
                                val respuesta = recibido

                                // Muestra el mensaje recibido y la respuesta enviada.
                                handler.post {
                                    tvMensajesServidor.append(
                                        "\nRecibido: $recibido\nEnviado: $respuesta\n"
                                    )
                                    tvEstadoServidor.text = "Cliente conectado."
                                }

                                // Devuelve la respuesta al cliente.
                                escritor.println(respuesta)
                            }
                        }

                        connectedClient = null

                        handler.post {
                            if (serverRunning) {
                                tvEstadoServidor.text =
                                    "Cliente desconectado. Esperando otro..."
                            }
                        }
                    } catch (e: IOException) {
                        // Cerrar el servidor interrumpe accept() de forma normal.
                        if (serverRunning) {
                            handler.post {
                                tvEstadoServidor.text =
                                    "Error de conexión: ${e.message}"
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                handler.post {
                    tvEstadoServidor.text =
                        "No se pudo iniciar el servidor: ${e.message}"
                }
            } finally {
                serverRunning = false
                serverSocket = null

                handler.post {
                    tvEstadoServidor.text = "Servidor detenido."
                }
            }
        }.start()
    }

    // Detiene el servidor y cierra cualquier conexión activa.
    private fun detenerServidor(){
        serverRunning = false

        try{
            connectedClient?.close()
        } catch (_: IOException){
        }

        try{
            serverSocket?.close()
        } catch (_: IOException){
        }

        try{
            serverSocket?.close()
        } catch (_: IOException){
        }

        connectedClient = null
        serverSocket = null
        tvEstadoServidor.text = "Deteniendo servidor..."
    }

    // Establece la conexión del cliente fuera del hilo principal.
    private fun conectarCliente() {
        if (clientSocket?.isConnected == true && clientSocket?.isClosed == false) {
            tvEstadoCliente.text = "Ya estás conectado."
            return
        }

        if (clientConnecting) return

        val ip = etIpCliente.text.toString().trim()
        val puerto = etPuertoCliente.text.toString().toIntOrNull()

        if (ip.isBlank() || puerto == null || puerto !in 1..65535) {
            tvEstadoCliente.text = "Comprueba la IP y el puerto."
            return
        }

        clientConnecting = true
        tvEstadoCliente.text = "Conectando..."

        Thread {
            var conexion: Socket? = null

            try {
                conexion = Socket()
                conexion.connect(InetSocketAddress(ip, puerto), 5000)

                val escritor = PrintWriter(conexion.getOutputStream(), true)
                val lector = BufferedReader(
                    InputStreamReader(conexion.getInputStream())
                )

                clientSocket = conexion
                clientWriter = escritor

                handler.post {
                    tvEstadoCliente.text = "Conectado a $ip:$puerto."
                }

                // Permite recibir respuestas continuamente mientras haya conexión.
                while (!conexion.isClosed) {
                    val respuesta = lector.readLine() ?: break

                    handler.post {
                        tvRespuesta.append("\n$respuesta")
                    }
                }
            } catch (e: IOException) {
                handler.post {
                    tvEstadoCliente.text =
                        "Conexión finalizada: ${e.message ?: "servidor no disponible"}"
                }
            } finally {
                try {
                    conexion?.close()
                } catch (_: IOException) {
                }

                if (clientSocket === conexion) {
                    clientSocket = null
                    clientWriter = null
                }

                clientConnecting = false

                handler.post {
                    tvEstadoCliente.text = "Desconectado."
                }
            }
        }.start()
    }
    // Envía una línea al servidor sin bloquear la interfaz.
    private fun enviarMensaje(){
        val mensaje = etMensaje.text.toString()

        if(mensaje.isBlank()){
            tvEstadoCliente.text = "Escribe un mensaje antes de enviarlo."
            return
        }

        val escritor = clientWriter

        if(escritor == null || clientSocket?.isClosed != false){
            tvEstadoCliente.text = "Primero debes conectarte al servidor."
            return
        }

        Thread{
            synchronized(escritor){
                escritor.println(mensaje)
            }
        }.start()

        etMensaje.text.clear()
    }

    // Cierra el socket del cliente para terminar la comunicación.
    private fun desconectarCliente(){
        try{
            clientSocket?.close()
        } catch (_: IOException){
        }

        clientSocket = null
        clientWriter = null
        tvEstadoCliente.text = "Desconectado..."
    }

    // Busca una dirección IPv4 local que no sea la de loopback.
    private fun obtenerIpLocal(): String{
        return try{
        val interfaces = NetworkInterface.getNetworkInterfaces()

        while (interfaces.hasMoreElements()){
            val interfaz = interfaces.nextElement()
            val direcciones = interfaz.inetAddresses

            while(direcciones.hasMoreElements()){
                val direccion = direcciones.nextElement()

                if(direccion is Inet4Address && !direccion.isLoopbackAddress && !direccion.isLinkLocalAddress){
                    return direccion.hostAddress ?: "No disponible"
                }
            }
        }
            "No disponible"
        } catch (_: Exception){
            "No disponible"
        }
    }

    override fun onDestroy(){
        // Libera los recursos de red al cerrar la actividad.
        detenerServidor()
        desconectarCliente()
        super.onDestroy()
    }

}