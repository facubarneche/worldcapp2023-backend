package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.controller.FiguritaFilterParams
import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.*
import ar.edu.unsam.algo3.error.*
import ar.edu.unsam.algo3.repository.FiguritasRepository
import ar.edu.unsam.algo3.repository.JugadorRepository
import ar.edu.unsam.algo3.repository.UsuariosRepository
import org.springframework.stereotype.Service

const val ERROR_MSG_FIND_JUGADOR = "El jugador a buscar es inexitente"
const val ERROR_MSG_DATA_INCOMPLETA = "Los campos se encuentran incompletos"
const val ERROR_MSG_PARAMETRO_INVALIDO = "El nivel de impresion es invalido"

@Service
class FiguritaService(
  val figuritaRepository: FiguritasRepository,
  val usuariosRepository: UsuariosRepository,
  val jugadorRepository: JugadorRepository)
{
  fun getAll(params: FiguritaFilterParams): List<FiguritaBaseDTO>{
    val figuritas = figuritaRepository.getAll()
    return filtrar(figuritas, params).map { it.toBaseDTO() }
  }

  fun paraIntercambiar(logedUserid:Int, params:FiguritaFilterParams): List<FiguritaFullDTO> {
    try {
      usuariosRepository.getById(logedUserid)
    } catch (ex: Exception) {
      throw NotFoundException(ErrorMessages.ID_INEXISTENTE)
    }
    val otros = this.otrosUsuarios(logedUserid)
    return otros.flatMap { filtrar(it.listaFiguritasARegalar(), params).map{ figu -> figu.toDTO(it)} }
  }

  fun obtenerFigusFaltantesAgregables(userID: Int, params: FiguritaFilterParams): List<FiguritaFullDTO> {
    val userFaltentesList = usuariosRepository.getById(userID).figuritasFaltantes.toList()
    val figusFaltantesAUsuario = figuritaRepository.getAll().filter { figu -> !userFaltentesList.contains(figu)  }
    return filtrar(figusFaltantesAUsuario, params).map{ it.toDTO(null)}
  }

  fun otrosUsuarios(miID: Int) = usuariosRepository.getAll().filter { it.id != miID }

  fun getAllPlayers():List<JugadorCreateDTO> = jugadorRepository.getAll().map {jugador -> jugador.toJugadorCreateDTO()}

  fun crearFiltroFigurita(params: FiguritaFilterParams):Filtro<Figurita>{
    val rango = (params.cotizacionInicial)..(params.cotizacionFinal)
    return Filtro<Figurita>().apply {
      addCondiconFiltrado(FiltroPalabraClaveFigurita(params.palabraClave, figuritaRepository))
      addCondiconFiltrado(FiltroOnfire(params.onFire))
      addCondiconFiltrado(FiltroEspromesa(params.esPromesa))
      addCondiconFiltrado(FiltroValoracion(rango))
    }
  }

  fun filtrar(figus: List<Figurita>, params: FiguritaFilterParams): List<Figurita>{
    val filtro = crearFiltroFigurita(params)
    return figus.filter { figu -> filtro.cumpleCondiciones(figu) }
  }
  fun getById(id:Int): FiguritaBaseDTO {
      return figuritaRepository.getById(id).toBaseDTO()
  }
  fun delete(id: Int) {
    val figurita = figuritaRepository.getById(id)
    validarFiguInutilizada(figurita)
    figuritaRepository.delete(figurita)
  }
  fun crearFigurita(infoFigurita : FiguritaCreateModifyDTO) {
    val nuevaFigurita = Figurita (
      numero = infoFigurita.numero,
      onFire = infoFigurita.onFire,
      cantidadImpresa = obtenerNivelImpresionDesdeString(infoFigurita.nivelImpresion),
      jugador = buscarJugadorPorNombre(infoFigurita.nombreApellido),
      urlImage = infoFigurita.urlImage
    )
    figuritaRepository.create(nuevaFigurita)
  }
  fun modificarFigurita(infoFigurita: FiguritaCreateModifyDTO, idFigurita: Int){
    val figurita = figuritaRepository.getById(idFigurita)

    with(figurita) {
      cambiarNumero(infoFigurita.numero)
      onFire = infoFigurita.onFire
      cantidadImpresa = obtenerNivelImpresionDesdeString(infoFigurita.nivelImpresion)
      jugador = buscarJugadorPorNombre(infoFigurita.nombreApellido)
      urlImage = infoFigurita.urlImage
    }
  }
  fun buscarJugadorPorNombre ( jugadorNombre: String ) : Jugador{
    val nombreApellido = jugadorNombre.split(" ")
    val nombre = nombreApellido[0]

    return jugadorRepository.getAll().find { jugador ->
      jugador.nombre.lowercase()  == nombre.lowercase()}
      ?: throw NotFoundException(ERROR_MSG_FIND_JUGADOR)
  }
  fun obtenerNivelImpresionDesdeString(nivelImpresionString: String): NivelImpresion {
    val mapNivelesImpresion = mapOf(
      "baja" to impresionBaja,
      "media" to impresionMedia,
      "alta" to impresionAlta
    )

    return mapNivelesImpresion[nivelImpresionString.lowercase()]
      ?: throw IllegalArgumentException(ERROR_MSG_PARAMETRO_INVALIDO)
  }

  fun validarFiguInutilizada(figurita: Figurita){
    val usuarios: List<Usuario> = usuariosRepository.getAll()

    usuarios.forEach { user -> user.validarFiguritaAEliminar(figurita) }
  }
}