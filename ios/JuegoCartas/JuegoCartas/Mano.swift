//
//  Mano.swift
//  JuegoCartas
//
//  Created by Master Moviles on 9/10/26.
//
class Mano {
    var cartas: [Carta] = []
    
    var tamaño: Int{
        return cartas.count
    }
    
    init(){
        cartas = []
    }
    
    func addCarta(_ carta: Carta){
        cartas.append(carta)
    }
    
    func getCarta(posicion: Int) -> Carta?{
        if posicion < 0 || posicion >= tamaño{
            return nil
        }
        
        return cartas[posicion]
    }
    
}
import Foundation
