//ejercicio 1
let valor = "true"
var b = Bool()

b = Bool(valor)!

print(b)

//ejercicio 2
var mensaje : String? = nil

if let mensaje = mensaje {
  print("El valor es: \(mensaje)")
}
else{
  print("El valor es nulo")
}

//ejercicio 3
var bizcocho = ["huevos", "leche", "harina"]

for i in 0..<bizcocho.count{
  print(bizcocho[i])
}


//ejercicio 4
func filtrar (_ lista:Array<Int>, max: Int)->Array<Int>{
  var resultado = [Int]()
  for numero in lista {
    if numero <= max{
      resultado.append(numero)
    } 
  }

  return resultado
}

var lista = [10, 4, 5, 7]
var f = filtrar(lista, max:5) 
print(f) //[4,5]

//ejercicio 5
class Persona {
  var nombre : String
  var edad : Int

  init(nombre: String, edad: Int){
    self.nombre = nombre
    self.edad = edad
  }

   var adulto: Bool {
        get {
            return edad >= 18
        }
    }
}

var p = Persona(nombre:"Pepe", edad:20)
//debería imprimir ADULTO! ya que la edad de la persona es >= 18
if p.adulto {
  print("ADULTO!")
} 

//ejercicio 6
enum DiaSemana: Int{
  case lunes, martes, miercoles, jueves, viernes, sabado, domingo

  func cuantoFalta() -> Int {
    if self.rawValue >= 5 {
      return 0
    }
    else{
      return 5 - self.rawValue
    }

  }
}

var dia = DiaSemana.lunes
print(dia.rawValue)       // 0

dia = .viernes
print(dia.cuantoFalta())  // 1
