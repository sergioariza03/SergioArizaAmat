package com.example.wifiydireccionip


import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.LinkAddress
import android.net.Network
import android.net.NetworkCapabilities
import android.net.LinkProperties
import android.net.wifi.WifiInfo
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.content.Intent
import android.content.Context
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wifiydireccionip.databinding.ActivityMainBinding
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import kotlin.math.abs
import android.net.NetworkRequest

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityManager: ConnectivityManager
    private var callbackWifi: ConnectivityManager.NetworkCallback? = null

    //Solicita el permiso de ubicación cuando el usuario lo concede o lo rechaza.
    private val solicitarUbicacion = registerForActivityResult(ActivityResultContracts.RequestPermission()){ concedido ->
        if(concedido){
            actualizarInformacion()
        }else{
            binding.tvEstado.text = "Permiso de ubicación denegado. Algunos datos Wi-Fi estarán ocultos."
            actualizarInformacion()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        binding.btnActualizar.setOnClickListener {
            comprobarPermisosYActualizar()
        }
        comprobarPermisosYActualizar()
    }

    // Comprueba el permiso antes de consultar los datos de la conexión.
    private fun comprobarPermisosYActualizar(){
        val permisoConcedido = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if(permisoConcedido){
            actualizarInformacion()
        }
        else{
            solicitarUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Obtiene la red activa y separa los datos Wi-Fi de los datos IP.

    private fun actualizarInformacion() {

        // Obtenemos la red que Android considera activa.
        val redActiva = connectivityManager.activeNetwork

        // Intentamos obtener las propiedades IP de esa red.
        var propiedadesIP: LinkProperties? = redActiva?.let { red ->
            connectivityManager.getLinkProperties(red)
        }

        // Si no encontramos las propiedades de la red activa,
        // revisamos todas las redes disponibles.
        if (propiedadesIP == null) {

            for (red in connectivityManager.allNetworks) {

                val propiedades = connectivityManager.getLinkProperties(red)

                // Nos quedamos con una red que tenga direcciones IP.
                if (propiedades != null && propiedades.linkAddresses.isNotEmpty()) {
                    propiedadesIP = propiedades
                    break
                }
            }
        }

        // Mostramos la información IP si hemos encontrado una red.
        val propiedadesEncontradas = propiedadesIP

        if (propiedadesEncontradas != null) {
            mostrarInformacionIP(propiedadesEncontradas)
        } else {
            binding.tvIp.text =
                "Android no ha proporcionado las propiedades IP de ninguna red."
        }

        var infoWifi: WifiInfo? = null

        // Recorremos todas las redes para encontrar una conexión Wi-Fi.
        for (red in connectivityManager.allNetworks) {

            val capacidades = connectivityManager.getNetworkCapabilities(red)

            if (capacidades != null &&
                capacidades.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            ) {

                // getTransportInfo() requiere Android 10 (API 29) o superior.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    infoWifi = capacidades.transportInfo as? WifiInfo
                }

                // Si encontramos la información Wi-Fi, dejamos de buscar.
                if (infoWifi != null) {
                    break
                }
            }
        }

        // Mostramos los datos Wi-Fi o un mensaje explicativo.
        if (infoWifi != null) {

            binding.tvEstado.text = "Se ha encontrado una conexión Wi-Fi."

            mostrarInformacionWifi(infoWifi)

        } else {

            binding.tvEstado.text =
                "No se ha encontrado una conexión identificada como Wi-Fi."

            binding.tvWifi.text =
                "El emulador puede estar utilizando una conexión virtual. " +
                        "Comprueba los ajustes de red del dispositivo."
        }

        // Intentamos obtener el SSID y el BSSID mediante un callback.
        solicitarInfoWifiConCallback()
    }

    private fun solicitarInfoWifiConCallback() {

        // Comprobamos que la aplicación tenga permiso de ubicación.
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            binding.tvWifi.text =
                "Se necesita el permiso de ubicación para consultar el SSID y BSSID."
            return
        }

        // Evitamos registrar varias veces el mismo callback.
        callbackWifi?.let { callbackAnterior ->
            try {
                connectivityManager.unregisterNetworkCallback(callbackAnterior)
            } catch (_: IllegalArgumentException) {
                // El callback anterior ya estaba desconectado.
            }
        }

        // Solicitamos información de las redes Wi-Fi.
        val solicitud = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        // Android 12 o superior permite solicitar información sensible
        // de ubicación mediante FLAG_INCLUDE_LOCATION_INFO.
        val callback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            object : ConnectivityManager.NetworkCallback(
                ConnectivityManager.NetworkCallback.FLAG_INCLUDE_LOCATION_INFO
            ) {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    procesarInfoWifi(networkCapabilities)
                }
            }

        } else {

            object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    procesarInfoWifi(networkCapabilities)
                }
            }
        }

        callbackWifi = callback

        try {
            connectivityManager.registerNetworkCallback(solicitud, callback)
        } catch (e: SecurityException) {
            binding.tvWifi.text =
                "Android ha denegado el acceso a la información Wi-Fi."
        }
    }

    private fun procesarInfoWifi(capacidades: NetworkCapabilities) {

        // transportInfo está disponible desde Android 10.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return
        }

        val infoWifi = capacidades.transportInfo as? WifiInfo

        runOnUiThread {
            if (infoWifi != null) {
                mostrarInformacionWifi(infoWifi)
            } else {
                binding.tvWifi.text =
                    "Android no ha proporcionado los datos detallados de Wi-Fi."
            }
        }
    }


    // Muestra los datos de radio proporcionados por WifiInfo.
    private fun mostrarInformacionWifi(info: WifiInfo?){
        if(info == null){
            binding.tvWifi.text = "Android no ha proporcionado los datos de WifiInfo."
            return
        }

        val ssid = info.ssid
            ?.removeSurrounding("\"")
            ?.takeUnless { it.isBlank() || it == "<unknown ssid>" }
            ?: "Desconocido o restringido por Android"

        val bssid = info.bssid
            ?.takeUnless { it.isBlank() || it == "02:00:00:00:00:00" }
            ?: "Desconocido o restringido por Android"

        val frecuencia = info.frequency
        val banda = obtenerBanda(frecuencia)
        val canal = obtenerCanal(frecuencia)

        val estandar = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            obtenerEstandarWifi(info.wifiStandard)
        }else{
            "No disponible en esta versión de Android"
        }

        val senal = info.rssi
        val velocidad = info.linkSpeed

        binding.tvWifi.text = """
            SSID: $ssid
            BSSID: $bssid
            Potencia de señal: ${senal} dBm
            Velocidad del enlace: ${velocidad} Mbit/s
            Frecuencia: ${frecuencia} MHz
            Banda: $banda
            Canal: ${canal ?: "No disponible"}
            Estándar Wi-Fi: $estandar
            Red oculta: ${if (info.hiddenSSID) "Sí" else "No"}
        """.trimIndent()
    }

    // Traduce el estándar numérico de Android a un nombre legible.
    private fun obtenerEstandarWifi(estandar: Int): String {
        return when (estandar) {
            1 -> "Legacy (802.11a/b/g)"
            4 -> "Wi-Fi 4 (802.11n)"
            5 -> "Wi-Fi 5 (802.11ac)"
            6 -> "Wi-Fi 6 (802.11ax)"
            7 -> "Wi-Fi 7 (802.11be)"
            else -> "Desconocido ($estandar)"
        }
    }

    // Calcula la banda a partir de la frecuencia central en MHz.
    private fun obtenerBanda(frecuencia: Int): String {
        return when (frecuencia) {
            in 2400..2500 -> "2,4 GHz"
            in 4900..5900 -> "5 GHz"
            in 5925..7125 -> "6 GHz"
            else -> "Desconocida"
        }
    }

    // Calcula el canal Wi-Fi para las bandas habituales.
    private fun obtenerCanal(frecuencia: Int): Int? {
        return when (frecuencia) {
            in 2412..2472 -> (frecuencia - 2407) / 5
            2484 -> 14
            in 5000..5895 -> (frecuencia - 5000) / 5
            in 5955..7115 -> (frecuencia - 5950) / 5
            else -> null
        }
    }

    private fun mostrarInformacionIP(propiedades: LinkProperties){
        val direcciones = propiedades.linkAddresses

        val ipv4 = direcciones.filter{
            it.address is InetAddress
        }

        val ipv6 = direcciones.filter {
            it.address is Inet6Address
        }

        val rutas = propiedades.routes
        val rutaDefecto = rutas.firstOrNull{
            it.isDefaultRoute
        }

        val puertaEnlace = rutaDefecto?.gateway
            ?.takeUnless { it.isAnyLocalAddress }
            ?.hostAddress ?: "No disponible"

        val dns = propiedades.dnsServers
            .joinToString("\n") { it.hostAddress ?: it.toString() }
            .ifBlank { "No disponibles" }

        val textoIpv4 = if (ipv4.isNotEmpty()) {
            ipv4.joinToString("\n") { direccion ->
                val prefijo = direccion.prefixLength
                val mascara = obtenerMascaraIpv4(direccion)
                "IP: ${direccion.address.hostAddress}\n" +
                        "Prefijo: /$prefijo\n" +
                        "Máscara: $mascara"
            }
        } else {
            "No hay dirección IPv4 asignada."
        }

        val textoIpv6 = if (ipv6.isNotEmpty()) {
            ipv6.joinToString("\n") { direccion ->
                "${direccion.address.hostAddress}/${direccion.prefixLength}"
            }
        } else {
            "No hay direcciones IPv6 disponibles."
        }

        binding.tvIp.text = """
            IPv4:
            $textoIpv4

            IPv6:
            $textoIpv6

            Puerta de enlace: $puertaEnlace

            Servidores DNS:
            $dns
        """.trimIndent()
    }

    // Convierte la longitud del prefijo IPv4 en su máscara decimal.
    private fun obtenerMascaraIpv4(direccion: LinkAddress): String {
        val prefijo = direccion.prefixLength
        var mascara = 0xffffffffL shl (32 - prefijo)
        mascara = mascara and 0xffffffffL

        return "${(mascara shr 24) and 255}." +
                "${(mascara shr 16) and 255}." +
                "${(mascara shr 8) and 255}." +
                "${mascara and 255}"
    }

    // Informa al usuario cuando no hay una red activa.
    private fun mostrarSinConexion(){
        binding.tvEstado.text = "No hay una conexión de red activa."
        binding.tvWifi.text = "No hay información Wi-Fi disponible."
        binding.tvIp.text = "No hay información IP disponible."
    }

}