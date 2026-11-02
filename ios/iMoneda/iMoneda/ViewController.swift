//
//  ViewController.swift
//  iMoneda
//
//  Created by Master Moviles on 9/10/26.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet weak var imagenMoneda: UIImageView!
    @IBOutlet weak var resultadoLabel: UILabel!
    let moneda = Moneda()
    
    
    @IBAction func lanzarMoneda(_ sender: UIButton) {
        let tirada = moneda.lanzar()
        
        switch tirada {
        case .cara:
            imagenMoneda.image = UIImage(named: "cara")
            resultadoLabel.text = "Ha salido cara"
        case .cruz:
            imagenMoneda.image = UIImage(named: "cruz")
            resultadoLabel.text = "Ha salido cruz"
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.

    }


}

