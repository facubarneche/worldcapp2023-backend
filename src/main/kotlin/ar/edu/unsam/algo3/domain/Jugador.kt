package ar.edu.unsam.algo3.domain

import ar.edu.unsam.algo3.repository.RepositorioProps
import java.time.LocalDate
import ar.edu.unsam.algo3.error.IllegalArgumentException

const val MENSAJE_ERROR_NUM_CAMISETA = "No se puede ingresar un numero de camiseta menor a 1 o mayor a 99"
const val COTIZACIONBASE = 2000000
const val LIMITEANIOSVETERANO = 2
const val EDADLIMITEPROMESAFUTBOL = 22

//TODO: cambiar props a un data class para poder realizar la validacionde campos al modificar un jugador ya instanciado
data class Jugador (
    var nombre:String,
    var apellido:String,
    var fechaNacimiento:LocalDate,
    var nroCamiseta: Int,
    var seleccionPerteneciente: Seleccion,
    var posicion: Posicion,
    var anioDeDebut: Int,
    var altura: Double,
    var peso: Double,
    var esLider: Boolean,
    var cotizacion: Double
): RepositorioProps(){
    init {
        validarTodosLosCampos()
    }
    //Obtiene las primeras 3 letras de la Selección
    fun pais(): String = seleccionPerteneciente.pais.slice(0..2)

    //Retorna si su peso esta entre los valores (range check)
    fun esLigero():Boolean = peso in 65.0..70.0
    fun esAlto():Boolean = altura >= 1.80
    fun aniosJugados():Int = LocalDate.now().year - anioDeDebut
    fun esLeyenda(): Boolean = (aniosJugados()>10) && ((cotizacion>20000000) || ((nroCamiseta in 5..10) && (esLider)))
    fun edad():Int = calculadoraEdad.calcularEdad(fechaNacimiento)
    fun valoracionJugador() = posicion.valor(jugador = this)
    private fun soyCaro(): Boolean = cotizacion <= COTIZACIONBASE
    private fun soyVeterano(): Boolean = aniosJugados() > LIMITEANIOSVETERANO
    private fun soyJoven(): Boolean = edad() < EDADLIMITEPROMESAFUTBOL
    fun promesaDelFutbol(): Boolean = !soyCaro() && !soyVeterano() && soyJoven()
    fun esDeSeleccionCampeonaDelMundo(): Boolean = seleccionPerteneciente.esCampeonaDelMundo()
    override fun validSearchCondition(value: String) =  Comparar.parcial(value, listOf(nombre, apellido))

    //------------------------- VALIDACIONES -------------------------- //
    fun validarNumeroCamiseta(){
        if (nroCamiseta !in 1..99){
            throw IllegalArgumentException(MENSAJE_ERROR_NUM_CAMISETA)
        }
    }
    fun validarCamposNumericosPositivos(){
        if( listOf(anioDeDebut.toDouble(), altura, peso, cotizacion).any{ !HelperNumerosEnteros.esPositivo( it ) }){
            throw IllegalArgumentException(MENSAJE_ERROR_NUM_NEGATIVO)
        }
    }

    fun validarTodosLosCampos(){
        validadorStrings.errorStringVacio(nombre, errorMessage =  MENSAJE_ERROR_INGRESAR_NOMBRE)
        validadorStrings.errorStringVacio(apellido, errorMessage = MENSAJE_ERROR_INGRESAR_APELLIDO)
        validarNumeroCamiseta()
        validarCamposNumericosPositivos()
    }
}
