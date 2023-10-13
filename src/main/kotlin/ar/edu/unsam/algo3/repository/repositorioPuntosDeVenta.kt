package ar.edu.unsam.algo3.repository

import ar.edu.unsam.algo3.domain.Pedido
import ar.edu.unsam.algo3.domain.PuntoDeVenta
import ar.edu.unsam.algo3.domain.Repositorio
import org.springframework.stereotype.Repository

@Repository
class RepositorioPuntosDeVenta: Repositorio<PuntoDeVenta>() {
    fun inactivos() = elementos.filter{(!it.value.disponibilidad() && it.value.pedidosPendientes.isEmpty()) || (!it.value.disponibilidad() && !it.value.tienePedidoConEntregaProxima())}

    fun updateStock(recibidos: MutableList<Pedido>){
        elementos.values.forEach{ it.procesarPedidos(recibidos) }
    }
}