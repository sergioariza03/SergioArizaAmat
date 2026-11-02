import UIKit

class ViewController: UIViewController {


override func viewDidLoad() {
    super.viewDidLoad()

    let mano = Mano()

    mano.addCarta(Carta(valor: 1, palo: .oros)!)
    mano.addCarta(Carta(valor: 10, palo: .espadas)!)
    mano.addCarta(Carta(valor: 7, palo: .copas)!)

    print("Hay \(mano.tamaño) cartas")

    for num in 0..<mano.tamaño {
        if let carta = mano.getCarta(posicion: num) {
            print(carta.descripcion())
        }
    }
}

}

