import Foundation


enum ErrorPassword: Error {
    case demasiadoCorta
    case faltaMayusculaOMinuscula
}


func chequearPassword(_ password: String) throws -> Bool {
    // Condición 1: longitud mínima de 8 caracteres
    if password.count < 8 {
        throw ErrorPassword.demasiadoCorta
    }

    // Condición 2: al menos una mayúscula y una minúscula
    var hayMayuscula = false
    var hayMinuscula = false

    for caracter in password {
        if caracter.isUppercase { hayMayuscula = true }
        if caracter.isLowercase { hayMinuscula = true }
    }

    if !hayMayuscula || !hayMinuscula {
        throw ErrorPassword.faltaMayusculaOMinuscula
    }

    return true
}

// Pruebas
let pruebas = ["abc", "abcdefgh", "ABCDEFGH", "Abcdefgh"]

for password in pruebas {
    do {
        let valida = try chequearPassword(password)
        print("\(password): válida (\(valida))")
    } catch ErrorPassword.demasiadoCorta {
        print("\(password): es demasiado corta, mínimo 8 caracteres")
    } catch ErrorPassword.faltaMayusculaOMinuscula {
        print("\(password): necesita al menos una mayúscula y una minúscula")
    } catch {
        print("Error desconocido")
    }
}
