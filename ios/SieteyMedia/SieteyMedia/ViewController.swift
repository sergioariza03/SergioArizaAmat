//
//  ViewController.swift
//  SieteyMedia
//
//  Created by Máster Móviles on 16/10/26.
//

import UIKit

class ViewController: UIViewController {
    var juego: Juego!
    var viewsCartas: [UIImageView] = []
    
    @IBOutlet weak var botonPedir: UIButton!
    @IBOutlet weak var botonPlantarse: UIButton!
    @IBOutlet weak var botonNueva: UIButton!
    
    
    @IBAction func nuevaPartida(_ sender: Any) {
        quitarCartas()
        juego = Juego()
        juego.turnoMaquina()
        
        botonPedir.isEnabled = true
        botonPlantarse.isEnabled = true
        botonNueva.isEnabled = false
    }
    
    @IBAction func pedirCarta(_ sender: Any) {
        let estado = juego.jugadorPideCarta()
        if let carta = juego.manoJugador.cartas.last {
            dibujarCarta(carta)
        }
        if estado != .turnoJugador {
            mostrarResultado(estado)
        }
    }
    
    @IBAction func plantarse(_ sender: Any) {
        let estado = juego.jugadorSePlanta()
        if estado != .turnoJugador{
            mostrarResultado(estado)
        }
    }
    
    func mostrarResultado(_ estado: EstadoJuego) {
        var mensaje = ""

        switch estado {
        case .sepasaJugador:  mensaje = "Te has pasado, pierdes"
        case .ganaJugador:    mensaje = "¡Has ganado!"
        case .pierdeJugador:  mensaje = "Gana la máquina"
        case .empate:         mensaje = "Empate"
        default: break
        }

        botonPedir.isEnabled = false
        botonPlantarse.isEnabled = false
        botonNueva.isEnabled = true

        let alert = UIAlertController(title: "Fin del juego",
                                      message: mensaje,
                                      preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        present(alert, animated: true)
    }
    
    func dibujarCarta(_ carta: Carta){
        let imagen = UIImage(named:"\(carta.valor)\(carta.palo.rawValue)")
        let vista = UIImageView(image: imagen)

        let separacion: CGFloat = 40
        let x = 20 + CGFloat(viewsCartas.count) * separacion
        let y: CGFloat = 200
        
        vista.frame = CGRect(x:-100, y: y, width: 60, height: 100)
        view.addSubview(vista)
        viewsCartas.append(vista)
        
        UIView.animate(withDuration: 0.4) {
            vista.frame = CGRect(x:x, y: y, width: 60, height: 100)
        }
    }
    
    func quitarCartas() {
        for v in viewsCartas {
            v.removeFromSuperview()
        }
        viewsCartas = []
    }
    
    


}

