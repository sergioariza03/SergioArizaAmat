package com.example.android_avanzado


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EstadoTemporizador {
    data object Inactivo : EstadoTemporizador
    data class Contando(val numero: Int) : EstadoTemporizador
    data object Terminado : EstadoTemporizador
}

class TemporizadorViewModel : ViewModel() {

    private val _estado = MutableStateFlow<EstadoTemporizador>(EstadoTemporizador.Inactivo)
    val estado: StateFlow<EstadoTemporizador> = _estado.asStateFlow()

    private var job: Job? = null

    fun iniciar() {
        job?.cancel()

        job = viewModelScope.launch {
            for (i in 10 downTo 1) {
                _estado.value = EstadoTemporizador.Contando(i)
                delay(1000)
            }
            _estado.value = EstadoTemporizador.Terminado
        }
    }
}