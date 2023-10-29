package ar.edu.unsam.algo3.domain

data class FiltroFigurita(
  var idUsuario: Int,
  var palabraClave: String = "",
  var onFire: Boolean = false,
  var esPromesa: Boolean = false,
  var rangoValoracion: ClosedRange<Double> = (0.0..0.0),
)

data class FiltroPuntoDeVenta(
  var idUsuario: Int,
  var palabraClave: String = "",
  var opcionElegida: String = "",
)