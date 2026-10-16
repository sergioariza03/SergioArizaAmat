//
//  ViewController.swift
//  SieteyMedia
//
//  Created by Máster Móviles on 16/10/26.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        let mano = Mano()

            if let carta = Carta(valor: 1, palo: .oros) {
                mano.addCarta(carta)
            }

            if let carta = Carta(valor: 5, palo: .copas) {
                mano.addCarta(carta)
            }
        
        for carta in mano.cartas {
            print(carta.descripcion())
        }
    }


}

