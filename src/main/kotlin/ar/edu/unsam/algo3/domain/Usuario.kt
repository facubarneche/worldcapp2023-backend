package ar.edu.unsam.algo3.domain

import ar.edu.unsam.algo3.error.BussinesExpetion
import ar.edu.unsam.algo3.error.FiguritaErrorMessages
import ar.edu.unsam.algo3.repository.RepositorioProps
import java.time.LocalDate

const val MENSAJE_ERROR_INGRESAR_NOMBRE_USUARIO = "Debe ingresar un nombre de usuario"
const val MENSAJE_ERROR_INGRESAR_EMAIL = "Debe ingresar un email"
const val MENSAJE_ERROR_DESACTIVAR_ACCION = "No puede desactivar una accion que nunca fue activada"
const val MENSAJE_ERROR_FIGURITA_INACCESIBLE = "El usuario no puede otrogar la figurita solicitada"
const val MENSAJE_ERROR_USUARIO_LEJANO = "El usuario al que le intenta solicitar la figurita esta demasiado lejos"
const val MENSAJE_ERROR_FIGURITA_ENFALTANTES = "La figurita que intenta agregar a repetidas esta en faltantes"
const val MENSAJE_ERROR_FIGURITA_ENREPETIDAS = "La figurita que intenta agregar a faltantes esta en repetidas"
const val MENSJAE_ERROR_FALTANTE_YA_AGREGADA = "La figurita que intenta agregar a faltantes ya esta agregada"
const val MENSAJE_ERROR_FIGU_NO_FALTANTE = "La figurita que esta solicitando no se encuentra dentro de la lista de faltantes"
data class Usuario(
    var nombre: String,
    var apellido: String,
    var nombreUsuario: String,
    val contrasenia: String = "",
    var fechaNacimiento: LocalDate,
    var email: String,
    var direccion: Direccion,
    val imagenPath: String
) : RepositorioProps() {
    //Lista de acciones que el usuario puede activar o desactivar segun sus necesidades de negocio
    val acciones = mutableSetOf<AccionesUsuarios>()
    var condicionParaDar: CondicionesParaDar = Desprendido(this)
    val seleccionesFavoritas = mutableSetOf<Seleccion>()
    val jugadoresFavoritos = mutableSetOf<Jugador>()
    val figuritasFaltantes = mutableSetOf<Figurita>()
    val figuritasRepetidas = mutableListOf<Figurita>()
    var distanciaMaximaCercania:Int = 5

    init {
        validadorStrings.errorStringVacio(nombre, errorMessage = MENSAJE_ERROR_INGRESAR_NOMBRE)
        validadorStrings.errorStringVacio(apellido, errorMessage = MENSAJE_ERROR_INGRESAR_APELLIDO)
        validadorStrings.errorStringVacio(nombreUsuario, errorMessage = MENSAJE_ERROR_INGRESAR_NOMBRE_USUARIO)
        validadorStrings.errorStringVacio(email, errorMessage = MENSAJE_ERROR_INGRESAR_EMAIL)
    }

    fun addJugadorFavorito(jugador: Jugador) { jugadoresFavoritos.add(jugador) }

    fun addSeleccionFavoritas(seleccion: Seleccion) { seleccionesFavoritas.add(seleccion) }

    fun nuevaDistanciaMaximaCercania(nuevaDistancia: Int) { distanciaMaximaCercania = nuevaDistancia }

    fun edad(): Int = calculadoraEdad.calcularEdad(fechaNacimiento)
    fun puedoDar(figurita: Figurita): Boolean = estaRepetida(figurita) && condicionParaDar.puedeDar(figurita)

    fun listaFiguritasARegalar(): List<Figurita> = figuritasRepetidas.filter{ puedoDar(it) }
    fun darFigurita(figurita: Figurita, solicitante: Usuario){
        if(puedoDar(figurita)){
            removeFiguritaRepetida(figurita)
            solicitante.recibirFigurita(figurita)
        }
    }

    // Proceso habitual de solicitud de una figurita a otro usuario
    fun pedirFigurita(figurita: Figurita, usuario: Usuario) {
        validarFiguritaSeaFaltante(figurita)
        validarDistanciaPedidoDeFigu(usuario)
        usuario.darFigurita(figurita, this)
        //Ejecuta la acción ConvertirUsuarioEnDesprendido
        acciones.forEach { accion -> accion.ejecutarAccion(this, figurita) }
    }

    //TODO: Preguntar si la implementación es correcta, hay alguna forma más eficiente?
    fun activarAccion(accion: AccionesUsuarios) {
        //TODO: Conviene que sea un error para el usuario?
        if (acciones.none { it.CODIGO_ACCION == accion.CODIGO_ACCION }) {
            acciones.add(accion)
        }
    }

    //TODO: Preguntar si la implementación es correcta, hay alguna forma más eficiente?
    fun desactivarAccion(accion: AccionesUsuarios) {
        this.validarDesactivarAccion(accion)
        //Si las instancias tienen el mismo codigo de acción, la remueve de la lista
        acciones.removeIf { it.CODIGO_ACCION == accion.CODIGO_ACCION }
    }

    fun modificarComportamientoIntercambio(comportamiento: CondicionesParaDar) {
        condicionParaDar = comportamiento
    }

    fun topCincoFiguritasRepetidas() = figuritasRepetidas.sortedBy { it.valoracion() }.takeLast(5).toSet()
    fun estaRepetida(figurita: Figurita) = figuritasRepetidas.contains(figurita)
    override fun validSearchCondition(value: String) =  Comparar.parcial(value, listOf(nombre, apellido)) ||
                                                        Comparar.total(value, listOf(nombreUsuario))
    fun addFiguritaFaltante(figurita: Figurita) {
        validarFaltanteExistente(figurita,MENSJAE_ERROR_FALTANTE_YA_AGREGADA)
        validarRepetidaExistente(figurita)
        figuritasFaltantes.add(figurita)
    }

    fun addFiguritaRepetida(figurita:Figurita){
        validarFaltanteExistente(figurita, MENSAJE_ERROR_FIGURITA_ENFALTANTES)
        figuritasRepetidas.add(figurita)
    }

    fun recibirFigurita(figurita:Figurita){
        removeFiguritaFaltante(figurita)
    }

    fun estaCerca(otroUsuario:Usuario):Boolean {
        return direccion.distanciaConPoint(point = otroUsuario.direccion.ubiGeografica) <= distanciaMaximaCercania
    }

    private fun removeFiguritaFaltante(figurita: Figurita) {
        if (buscadorFaltanteExistente(figurita)) {
            figuritasFaltantes.remove(figurita)
        }
    }
    private fun removeFiguritaRepetida(figurita: Figurita) {
        figuritasRepetidas.remove(figurita)
    }
    private fun buscadorFaltanteExistente(figurita: Figurita): Boolean =
        figuritasFaltantes.map { it.numero }.contains(figurita.numero)

    //---------------------- VALIDACIONES -------------------------//
    fun validarFiguritaAEliminar(figurita: Figurita){
        if(figuritasRepetidas.contains(figurita) || figuritasFaltantes.contains(figurita)) {
            throw BussinesExpetion(FiguritaErrorMessages.FIGURITA_UTILIZADA)
        }
    }
    private fun validarRepetidaExistente(figurita: Figurita) {
        if (figuritasRepetidas.contains(figurita)){
            throw BussinesExpetion(MENSAJE_ERROR_FIGURITA_ENREPETIDAS)
        }
    }

    private fun validarFaltanteExistente(figurita: Figurita, msg: String) {
        if(figuritasFaltantes.contains(figurita)){
            throw BussinesExpetion(msg)
        }
    }

    private fun validarDesactivarAccion(accion: AccionesUsuarios) {
        if (acciones.none { it.CODIGO_ACCION == accion.CODIGO_ACCION }) {
            throw BussinesExpetion(MENSAJE_ERROR_DESACTIVAR_ACCION)
        }
    }

    private fun validarFiguritaSeaFaltante(figurita: Figurita) {
        if(!figuritasFaltantes.contains(figurita)){
            throw BussinesExpetion(MENSAJE_ERROR_FIGU_NO_FALTANTE)
        }
    }

    private fun validarEntregaDeFigu(figurita: Figurita) {
        if (!puedoDar(figurita)) throw BussinesExpetion(MENSAJE_ERROR_FIGURITA_INACCESIBLE)
    }

    private fun validarDistanciaPedidoDeFigu(usuario: Usuario) {
        if (!this.estaCerca(usuario)) throw BussinesExpetion(MENSAJE_ERROR_USUARIO_LEJANO)
    }
}
