//
//  Carta.swift
//  JuegoCartas
//
//  Created by Master Moviles on 9/10/26.
//

class Carta{
    let valor: Int
    let palo: Palo
    
    init?(valor: Int, palo: Palo) {
        if(valor < 1 || valor > 12 || valor == 8 || valor == 9){
            return nil
        }
        
        self.valor = valor
        self.palo = palo
    }
    
    func descripcion() -> String{
        return "El \(self.valor) de \(self.palo.rawValue)"
    }
    
    
}

import Foundation
