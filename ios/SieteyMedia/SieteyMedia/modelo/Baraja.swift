//
//  Baraja.swift
//  SieteyMedia
//
//  Created by Máster Móviles on 16/10/26.
//

import Foundation

class Baraja {
    var cartas: [Carta] = []
    
    init() {
        for palo in [Palo.bastos, Palo.copas, Palo.espadas, Palo.oros]{
            for valor in 1...12{
                if valor != 8 && valor != 9{
                    if let carta = Carta(valor: valor, palo: palo){
                        self.cartas.append(carta)
                    }
                }
            }
        }
    }
    
    func repartirCarta() -> Carta?{
        return self.cartas.popLast()
    }
    
    func barajar(){
        self.cartas.shuffle()
    }
}
