package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.FiltroPuntoDeVenta
import ar.edu.unsam.algo3.dto.MarketCardDTO
import ar.edu.unsam.algo3.dto.SalesPointCardDTO
import ar.edu.unsam.algo3.service.PuntosDeVentaService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
class PuntosDeVentaController(val puntosDeVentaService: PuntosDeVentaService) {
  @GetMapping("/puntosDeVenta/{id}")
  @Operation(summary = "Obtiene todos los puntos de venta")
  fun getAll(
    @PathVariable id: Int,
    @RequestParam(name= "palabraClave", required = false, defaultValue = "") palabraClave: String,
    @RequestParam(name= "opcionElegida", required = false, defaultValue = "") opcionElegida: String
  ): List<MarketCardDTO> {
    val filtro = FiltroPuntoDeVenta(
      palabraClave = palabraClave,
      opcionElegida = opcionElegida,
    )
    println(filtro)
    return this.puntosDeVentaService.obtenerPuntosDeVentaFiltrados(id,filtro)
  }

  @GetMapping("/puntosDeVenta/index")
  @Operation(summary = "Obtiene toda la info necesaria para mostrar todos los puntos de venta en su respectivo dashboard")
  fun getAllSalesPoint(): List<SalesPointCardDTO>{
    return this.puntosDeVentaService.getAllSalesPoint()
  }
}