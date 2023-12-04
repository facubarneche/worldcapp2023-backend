package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.controller.BaseFilterParams
import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.JugadorDTO
import ar.edu.unsam.algo3.dto.toDTO
import ar.edu.unsam.algo3.repository.SeleccionesRepository
import org.springframework.stereotype.Service

@Service
class SeleccionesService(
    val seleccionesRepo: SeleccionesRepository
) {
    fun crearFiltroSeleccion(params: BaseFilterParams): Filtro<Seleccion> {
        return Filtro<Seleccion>().apply {
            addCondiconFiltrado(FiltroPalabraClaveSeleccion(params.palabraClave, seleccionesRepo))
        }
    }

    fun filtrar(selecciones: List<Seleccion>, params: BaseFilterParams): List<Seleccion>{
        val filtro = crearFiltroSeleccion(params)
        return selecciones.filter { seleccion -> filtro.cumpleCondiciones(seleccion) }
    }

    fun getAllNames(): List<String>{
        val allNationalTeams = seleccionesRepo.getAll()
        return allNationalTeams.map { nationalTeam -> nationalTeam.pais }
    }

    fun getAll(params: BaseFilterParams): List<Seleccion> {
        val selecciones = seleccionesRepo.getAll()
        return filtrar(selecciones, params)
    }
}