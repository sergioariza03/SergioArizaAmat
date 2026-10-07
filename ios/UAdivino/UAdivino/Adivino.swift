//
//  Adivino.swift
//  UAdivino
//
//  Created by Máster Móviles on 7/10/26.
//
class Adivino{
    var nombre : String
    let respuestas = ["si", "no", "¡claro!", "Ni de coña"]
    
    init(nombre: String){
        self.nombre = nombre
    }
    
    func obtenerRespuesta() -> String{
        let pos = Int.random(in: 0..<respuestas.count)
        return respuestas[pos]
    }
}

import Foundation
