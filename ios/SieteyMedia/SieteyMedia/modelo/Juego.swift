enum EstadoJuego {
    //pierdeJugador sería que pierde porque la máquina está más cerca del 7.5
    //sepasaJugador sería que pierde porque se ha pasado
    case noIniciado, turnoJugador, ganaJugador, sepasaJugador, pierdeJugador, empate
}



class Juego {
    var baraja : Baraja
    var manoJugador : Mano
    var estado : EstadoJuego
    var sumaMaquina : Double = 0.0
    var sumaJugador : Double = 0.0
    
    init() {
        self.estado = .noIniciado
        self.baraja = Baraja()
        self.baraja.barajar()
        self.manoJugador = Mano()
    }
    
    //LLamar a este método cuando se pulse el botón de "comenzar partida"
    func turnoMaquina() {
        //generamos un valor al azar entre 1 y 7.5. La máquina nunca se pasa
        //primero generamos un valor entre 1 y 7
        self.sumaMaquina = Double(Int.random(in: 1...7))
        //y luego el 50% de las veces le sumamos 0.5
        if (Bool.random()) {
            self.sumaMaquina += 0.5
        }
        self.estado = .turnoJugador
    }
    
    //LLamar a este método cuando se pulse el botón de "Pedir carta"
    func jugadorPideCarta() -> EstadoJuego {
        if let pedida = self.baraja.repartirCarta() {
            print("Sacas \(pedida.descripcion())")
            self.manoJugador.addCarta(pedida)
            self.sumaJugador = self.sumarManoJugador()
            print("Llevas \(self.sumaJugador) puntos")
            if (self.sumaJugador>7.5) {
                acabarPartida()
            }
        }
        return self.estado
    }
    
    //LLamar a este método cuando se pulse el botón de "Plantarse"
    func jugadorSePlanta() -> EstadoJuego {
        acabarPartida()
        return self.estado
    }
    
    
    //Métodos para uso interno de la clase, no es necesario llamarlos desde fuera
    //Calcula quién gana, cambia el estado del juego y lo muestra en la consola con print
    private func acabarPartida() {
        //sumar el valor de las cartas del jugador y guardarlo en sumaJugador, si no está calculado
        self.sumaJugador = self.sumarManoJugador()
        //calcular quién gana, en función de esta suma y la jugada de la máquina
        if self.sumaJugador>7.5 {
            self.estado = .sepasaJugador
        } else if self.sumaJugador>self.sumaMaquina {
            self.estado = .ganaJugador
        } else  if self.sumaJugador==self.sumaMaquina{
            self.estado = .empate
        }
        else {
            self.estado = .pierdeJugador
        }
    }

    private func sumarManoJugador() -> Double {
        var suma = 0.0
        for carta in self.manoJugador.cartas {
            switch carta.valor {
                case 1...7:
                    suma += Double(carta.valor)
                case 10,11,12:
                    suma += 0.5
                default:
                    break
            }
        }
        return suma
    }
    
}