package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.FiltroPuntoDeVenta
import ar.edu.unsam.algo3.dto.MarketCardDTO
import ar.edu.unsam.algo3.service.PuntosDeVentaService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class PuntosDeVentaController(val puntosDeVentaService: PuntosDeVentaService) {
  @GetMapping("/puntosDeVenta/")
  @Operation(summary = "Obtiene todos los puntos de venta")
  fun getAll(
    @RequestParam(name= "id", required = true, defaultValue = "0") id: Int,
    @RequestParam(name= "palabraClave", required = false, defaultValue = "") palabraClave: String,
    @RequestParam(name= "opcionElegida", required = false, defaultValue = "") opcionElegida: String
  ): List<MarketCardDTO> {
    val filtro = FiltroPuntoDeVenta(
      idUsuario = id,
      palabraClave = palabraClave,
      opcionElegida = opcionElegida,
    )
    println(filtro)
    return this.puntosDeVentaService.obtenerPuntosDeVentaFiltrados(filtro)
  }
}