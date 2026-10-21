package com.example.telephonymanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.os.Build
import android.telephony.CellInfo
import android.telephony.CellInfoGsm
import android.telephony.CellInfoWcdma
import android.telephony.CellInfoLte
import android.telephony.CellIdentity
import android.telephony.CellInfoNr
import android.annotation.SuppressLint

class MainActivity : AppCompatActivity() {
    private lateinit var telephonyManager: TelephonyManager

    companion object{
        private const val CODIGO_PERMISO = 100
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Obtener el servicio de telefonia del dispositivo
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        //Cofigurar boton
        findViewById<Button>(R.id.btnActualizar).setOnClickListener {
            comprobarPermisosyActualizar()
        }

        //comprobar los servicios
        comprobarPermisosyActualizar()
    }

    private fun comprobarPermisosyActualizar(){
        if(ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        )  != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                CODIGO_PERMISO
            )
            return
        }
        mostrarInformacion()
    }

    private fun mostrarInformacion() {

        // Obtenemos la información de la SIM y de la red.
        val operadorSIM = telephonyManager.simOperatorName
        val codigoSIM = telephonyManager.simOperator
        val paisSIM = telephonyManager.simCountryIso
        val operadorRed = telephonyManager.networkOperatorName
        val codigoRed = telephonyManager.networkOperator
        val itinerancia = telephonyManager.isNetworkRoaming

        // Mostramos el operador de la SIM.
        findViewById<TextView>(R.id.tvOperadorSIM).text =
            "Operador de SIM: ${datoDisponible(operadorSIM)}"

        // Si no tenemos el código de la SIM, intentamos obtener el de la red.
        val codigoOperador = if (codigoSIM.isNotBlank()) {
            codigoSIM
        } else {
            codigoRed
        }

        // Separamos el MCC y el MNC cuando el código está disponible.
        val mccMncFormateado = if (codigoOperador.length >= 5) {
            "MCC: ${codigoOperador.substring(0, 3)} | " +
                    "MNC: ${codigoOperador.substring(3)}"
        } else {
            "MCC/MNC no disponibles (código: ${datoDisponible(codigoOperador)})"
        }

        // Mostramos el MCC y el MNC en pantalla.
        findViewById<TextView>(R.id.tvMccMnc).text = mccMncFormateado

        // Mostramos el país de la SIM.
        findViewById<TextView>(R.id.tvPais).text =
            "País de SIM: ${datoDisponible(paisSIM)}"

        // Mostramos el operador de red.
        findViewById<TextView>(R.id.tvOperadorRed).text =
            "Operador de red: ${datoDisponible(operadorRed)}"

        // Mostramos si el dispositivo está en itinerancia.
        findViewById<TextView>(R.id.tvItinerancia).text =
            "Itinerancia: ${if (itinerancia) "Sí" else "No"}"

        // Mostramos la tecnología de datos móviles.
        findViewById<TextView>(R.id.tvTecnologia).text =
            "Tecnología de datos: ${obtenerTecnologia()}"

        // Actualizamos la información de la celda y la señal.
        actualizarCeldaYSenal()

        // Consultamos los identificadores que Android permita leer.
        findViewById<TextView>(R.id.tvIdentificadores).text =
            leerIdentificadoresRestringidos()
    }
    private fun datoDisponible(dato:String?): String{
        return if (dato.isNullOrBlank()){
            "No disponible"
        } else{
            dato
        }
    }


    private fun obtenerTecnologia(): String {
        return try {
            // Consultamos el tipo de red actual.
            when (telephonyManager.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_GSM -> "2G"

                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"

                TelephonyManager.NETWORK_TYPE_LTE -> "4G (LTE)"

                TelephonyManager.NETWORK_TYPE_NR -> "5G (NR)"

                TelephonyManager.NETWORK_TYPE_UNKNOWN -> "Desconocida"

                else -> "Otra tecnología"
            }
        } catch (e: SecurityException) {
            "Acceso restringido: falta permiso"
        } catch (e: Exception) {
            "Tecnología no disponible"
        }
    }

    private fun actualizarCeldaYSenal(){
        val tvCelda = findViewById<TextView>(R.id.tvCelda)
        val tvSenal = findViewById<TextView>(R.id.tvSenal)

        //Comprobar que tengo el permiso de  ubicacion precisa
        if(ActivityCompat.checkSelfPermission(
            this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ){
            tvCelda.text = "Celda: falta el permiso de ubicación precisa"
            tvSenal.text = "Señal: no disponible sin el permiso"
            return
        }

        try{
            val celdas = telephonyManager.allCellInfo

            if(celdas.isNullOrEmpty()){
                tvCelda.text = "Celda: no disponible. Comprueba la ubicación del dispositivo."
                tvSenal.text = "Señal: no disponible"
                return
            }

            val celda = celdas.firstOrNull(){
                it.isRegistered
            }
                ?:celdas.first()
            when(celda){
                is CellInfoGsm -> {
                    val identidad = celda.cellIdentity
                    tvCelda.text =
                        "Tipo: GSM\n" +
                                "Identificador (CID): ${identidad.cid}\n" +
                                "Área (LAC): ${identidad.lac}"

                    tvSenal.text =
                        "Señal: ${celda.cellSignalStrength.dbm} dBm"
                }
                is CellInfoWcdma -> {
                    val identidad = celda.cellIdentity
                    tvCelda.text =
                        "Tipo: WCDMA\n" +
                                "Identificador (CID): ${identidad.cid}\n" +
                                "Área (LAC): ${identidad.lac}"

                    tvSenal.text =
                        "Señal: ${celda.cellSignalStrength.dbm} dBm"
                }
                is CellInfoLte -> {
                    val identidad = celda.cellIdentity
                    tvCelda.text =
                        "Tipo: LTE\n" +
                                "Identificador (CI): ${identidad.ci}\n" +
                                "Área (TAC): ${identidad.tac}"

                    tvSenal.text =
                        "Señal: ${celda.cellSignalStrength.dbm} dBm"
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                        celda is CellInfoNr
                    ) {
                        val identidad =
                            celda.cellIdentity as android.telephony.CellIdentityNr

                        tvCelda.text =
                            "Tipo: NR (5G)\n" +
                                    "Identificador (NCI): ${identidad.nci}\n" +
                                    "Área (TAC): ${identidad.tac}"

                        tvSenal.text =
                            "Señal: ${celda.cellSignalStrength.dbm} dBm"
                    } else {
                        tvCelda.text = "Tipo de celda no reconocido o no compatible"
                        tvSenal.text = "Señal no disponible"
                    }

                }
            }
        } catch(e: SecurityException){
            tvCelda.text = "Celda: acceso denegado por permisos"
            tvSenal.text = "Señal: acceso denegado por permisos"
        } catch (e: Exception){
            tvCelda.text = "Celda: no se ha podido obtener la información"
            tvSenal.text = "Señal: no se ha podido obtener la información"
        }
    }

    @SuppressLint("MissingPermission")
    private fun leerIdentificadoresRestringidos(): String {

        // Intenta leer cada identificador sin dejar que un permiso bloqueado
        // cierre la aplicación.
        fun intentarLeer(nombre: String, lectura: () -> String?): String {
            return try {
                val valor = lectura()

                if (valor.isNullOrBlank()) {
                    "$nombre: no disponible"
                } else {
                    "$nombre: $valor"
                }
            } catch (e: SecurityException) {
                "$nombre: acceso restringido por Android"
            } catch (e: Exception) {
                "$nombre: no se ha podido consultar"
            }
        }

        return listOf(
            intentarLeer("IMEI") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    telephonyManager.imei
                } else {
                    null
                }
            },
            intentarLeer("IMSI") {
                telephonyManager.subscriberId
            },
            intentarLeer("ICCID") {
                telephonyManager.simSerialNumber
            }
        ).joinToString("\n")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == CODIGO_PERMISO){
            if(grantResults.isNotEmpty() && grantResults.all {
                it == PackageManager.PERMISSION_GRANTED
                }) {
                mostrarInformacion()
            } else{
                Toast.makeText(
                    this,
                    "Se necesitan permisos para consultar la información.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }






}