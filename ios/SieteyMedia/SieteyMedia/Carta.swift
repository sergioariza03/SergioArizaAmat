//
//  Carta.swift
//  SieteyMedia
//
//  Created by Máster Móviles on 16/10/26.
//

import Foundation

class Carta {
    let valor: Int
    let palo: Palo

    init?(valor: Int, palo: Palo) {
        if valor < 1 || valor > 12 || valor == 8 || valor == 9 {
            return nil
        }

        self.valor = valor
        self.palo = palo
    }

    func descripcion() -> String {
        return "el \(valor) de \(palo.rawValue)"
    }
}
