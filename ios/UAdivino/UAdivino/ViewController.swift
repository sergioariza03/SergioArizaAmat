//
//  ViewController.swift
//  UAdivino
//
//  Created by Máster Móviles on 7/10/26.
//

import UIKit

class ViewController: UIViewController {
    let adivino = Adivino(nombre: "Akinator")
    
    @IBOutlet weak var resLabel: UILabel!
    
    @IBAction func botonPulsado(_ sender: Any) {
        resLabel.text = adivino.obtenerRespuesta()
        
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        /*let adivino = Adivino(nombre: "Akinator")
        for _ in 1...5 {
            print(adivino.obtenerRespuesta())
        }*/
    }


}


