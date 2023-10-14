package ar.edu.unsam.algo3.repository

import org.springframework.stereotype.Repository

const val MENSAJE_ERROR_ID_INEXISTENTE= "El ID no corresponde con ningun elementod del repositorio"

@Repository
class Repositorio<T : RepositorioProps> {
    var elementos = mutableMapOf<Int, T>()
    private var idCounter = 0

    fun getAll():List<T> = elementos.map{ e -> e.value }

    fun create(elemento: T):T{
        elemento.id(idCounter)
        elementos[elemento.id] = elemento
        idCounter += 1
        return elemento
    }

    fun delete(elemento: T){
        elementos.remove(elemento.id)
    }

    fun massiveDelete(lista: Map<Int, T>){
        lista.forEach{this.delete(it.value)}
    }

    fun update(updatedElement: T){
        validarIDElemento(updatedElement.id)
        elementos[updatedElement.id] = updatedElement
    }

    fun getById(id: Int): T? {
        validarIDElemento(id)
        return elementos[id]
    }

    fun search(value: String) = elementos.values.filter { it.validSearchCondition(value) }

    private fun validarIDElemento(id: Int) {
        if (!elementos.keys.contains(id)){
            throw IllegalArgumentException(MENSAJE_ERROR_ID_INEXISTENTE)
        }
    }
}



