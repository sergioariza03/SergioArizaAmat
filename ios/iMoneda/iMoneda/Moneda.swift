//
//  Moneda.swift
//  iMoneda
//
//  Created by Master Moviles on 9/10/26.
//
enum Tirada{
    case cara, cruz
}

class Moneda {
    func lanzar() -> Tirada {
        if Bool.random() {
            return Tirada.cara
        } else {
            return Tirada.cruz
        }
    }
}
import Foundation
