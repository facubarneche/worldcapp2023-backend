package ar.edu.unsam.algo3.domain

import ar.edu.unsam.algo3.error.BussinesExpetion
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class AccionUsuarioSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Test Acciones de Usuarios") {
        val usuarioGeneral = juan.copy()
        val usuarioCercano = pablo.copy()

        describe("Test validaciones generales acciones de usuario") {
            val usuarioValidador = sol.copy()

            it("Un usuario no puede activar la misma accion mas de una vez") {
                usuarioValidador.activarAccion(ConvertirUsuarioEnDesprendido())
                usuarioValidador.activarAccion(ConvertirUsuarioEnDesprendido())
                usuarioValidador.acciones.size shouldBe 1
            }

            it("Un usuario puede activar una accion si esta no esta activada") {
                usuarioValidador.activarAccion(ConvertirUsuarioEnNacionalista())
                usuarioValidador.acciones.size shouldBe 1
            }
            it("Debe dar error si un usuario intenta desactivar una accion que nunca fue activada") {
                shouldThrow<BussinesExpetion> {
                    usuarioValidador.desactivarAccion(ConvertirUsuarioEnNacionalista())
                }.message shouldBe MENSAJE_ERROR_DESACTIVAR_ACCION
            }
            it("Un usuario debe poder desactivar una accion que este activada") {
                usuarioValidador.activarAccion(ConvertirUsuarioEnNacionalista())
                usuarioValidador.desactivarAccion(ConvertirUsuarioEnNacionalista())
                usuarioValidador.acciones.size shouldBe 0
            }
        }

        describe("Test solicitud de figuritas con accion ConvertirUsuarioEnNacionalista") {
            it("Un usuario se convierte a nacionalista de detemrianda seleccion si solicito de forma seguida N figuritas de la dicha seleccion y no lleno el album") {
                //arrange
                val figuritaArgenta = figuritaComun
                val usuarioSolicitante = usuarioGeneral.apply {
                    addFiguritaFaltante(figuritaBase)
                    addFiguritaFaltante(figuritaArgenta)
                }
                usuarioCercano.apply {
                    repeat(3) { addFiguritaRepetida(figuritaChilena) }
                }
                //activate
                usuarioSolicitante.activarAccion(ConvertirUsuarioEnNacionalista())
                usuarioSolicitante.pedirFigurita(figuritaArgenta, usuarioCercano)
                repeat(3) {
                    usuarioSolicitante.addFiguritaFaltante(figuritaChilena)
                    usuarioSolicitante.pedirFigurita(figuritaChilena, usuarioCercano)
                }

                //assert
                usuarioSolicitante.seleccionesFavoritas.contains(seleccionChile).shouldBeTrue()
                (usuarioSolicitante.condicionParaDar is Nacionalista).shouldBeTrue()
            }
            it("Un usuario no se convierte en nacionalista por que no realizo los N pedidos de figuirtas de la misma seleccion") {
                //arrange
                val usuarioSolicitante = usuarioGeneral.apply {
                    addFiguritaFaltante(figuritaBase)
                }
                usuarioCercano.apply {
                    repeat(2) { addFiguritaRepetida(figuritaChilena) }
                }
                //activate
                usuarioSolicitante.activarAccion(ConvertirUsuarioEnNacionalista())
                repeat(2) {
                    usuarioSolicitante.addFiguritaFaltante(figuritaChilena)
                    usuarioSolicitante.pedirFigurita(figuritaChilena, usuarioCercano)
                }
                //assert
                (usuarioSolicitante.condicionParaDar is Nacionalista).shouldBeFalse()
            }
            it("Un usuario no se convierte en nacionalista por que ya lleno el album") {
                //arrange
                val figuritaArgenta = figuritaComun
                val usuarioSolicitante = usuarioGeneral.apply { figuritasFaltantes.clear() }
                usuarioCercano.apply {
                    addFiguritaRepetida(figuritaArgenta)
                    repeat(3) {
                        addFiguritaRepetida(figuritaChilena)
                    }
                }
                //activate
                usuarioSolicitante.activarAccion(ConvertirUsuarioEnNacionalista())
                repeat(3) {
                    usuarioSolicitante.addFiguritaFaltante(figuritaChilena)
                    usuarioSolicitante.pedirFigurita(figuritaChilena, usuarioCercano)
                }
                //assert
                (usuarioSolicitante.condicionParaDar is Nacionalista).shouldBeFalse()
            }
            it("Un usuario no se convierte en nacionalista por que los N pedidos que realizó hay por lo menos una figurita de una distinta seleccion que las demas") {
                //arrange
                val figuritaArgenta = figuritaComun
                val usuarioSolicitante = usuarioGeneral.apply {
                    addFiguritaFaltante(figuritaArgenta)
                }
                usuarioCercano.apply {
                    repeat(2) { addFiguritaRepetida(figuritaChilena) }
                    addFiguritaRepetida(figuritaArgenta)
                }
                //activate
                usuarioSolicitante.activarAccion(ConvertirUsuarioEnNacionalista())
                repeat(2) {
                    usuarioSolicitante.addFiguritaFaltante(figuritaChilena)
                    usuarioSolicitante.pedirFigurita(figuritaChilena, usuarioCercano)
                }
                usuarioSolicitante.addFiguritaFaltante(figuritaChilena)
                usuarioSolicitante.pedirFigurita(figuritaArgenta, usuarioCercano)
                usuarioSolicitante.pedirFigurita(figuritaChilena, usuarioCercano)
                //assert
                (usuarioSolicitante.condicionParaDar is Nacionalista).shouldBeFalse()
            }
        }

        describe("Test solicitud de figuritas con accion ConvertirUsuarioEnDesprendido ") {
            usuarioGeneral.apply {
                //Cambio la condición para dar del usuario ya que por defecto es Desprendido
                modificarComportamientoIntercambio(Nacionalista(usuarioGeneral))
                //Agrego 2 figuritas repetidas al usuario general
                addFiguritaRepetida(figuritaBase)
                addFiguritaRepetida(figuritaChilena)
            }
            it("Usuario con album lleno pero con menos de un determinado número de figuritas a regalar, NO se convierte en Desprendido") {
                //Verificamos sus figuritas repetidas
                usuarioGeneral.listaFiguritasARegalar().size shouldBe 2

                usuarioGeneral.condicionParaDar::class.simpleName shouldBe "Nacionalista"
            }

            it("Usuario solicitante con más de un determinado número de figuritas a regalar y al pedir la figurita faltante para llenar el album se convierte en Desprendido") {
                //arrange
                usuarioCercano.addFiguritaRepetida(figuritaComun)

                usuarioGeneral.apply {
                    addFiguritaFaltante(figuritaComun)
                    addFiguritaRepetida(figuritaDevaluada)
                }

                //active
                usuarioGeneral.activarAccion(ConvertirUsuarioEnDesprendido())
                usuarioGeneral.pedirFigurita(figuritaComun, usuarioCercano)

                //Assert
                usuarioGeneral.figuritasFaltantes.size shouldBe 0
                usuarioGeneral.condicionParaDar::class.simpleName shouldBe "Desprendido"
            }

            it("Usuario al pedir figurita con las condiciones requeridas para convertir usuario en desprendido con la acción desactivada, no se convierte en Desprendido") {
                //arrange
                usuarioCercano.addFiguritaRepetida(figuritaComun)

                usuarioGeneral.apply {
                    addFiguritaFaltante(figuritaComun)
                    addFiguritaRepetida(figuritaDevaluada)
                }

                //active
                usuarioGeneral.activarAccion(ConvertirUsuarioEnDesprendido())
                usuarioGeneral.desactivarAccion(ConvertirUsuarioEnDesprendido())
                usuarioGeneral.pedirFigurita(figuritaComun, usuarioCercano)

                //Assert
                usuarioGeneral.figuritasFaltantes.size shouldBe 0
                usuarioGeneral.condicionParaDar::class.simpleName shouldBe "Nacionalista"
            }
        }

        describe("Test solicitud de figuritas con accion UsuarioTriplicaKM") {
            //Arrange
            usuarioGeneral.apply {
                addFiguritaFaltante(figuritaComun)
                addFiguritaFaltante(figuritaBase)
                activarAccion(UsuarioTriplicaKM())
            }
            usuarioCercano.addFiguritaRepetida(figuritaComun)
            usuarioCercano.addFiguritaRepetida(figuritaBase)

            it("Usuario con menos de TOPE_FIGURITAS_PARA_TRIPLICAR se multiplica su cercania con el otro usuario, y asi buscar figuritas") {
                //Active
                usuarioGeneral.pedirFigurita(figuritaComun, usuarioCercano)
                //Assert
                usuarioGeneral.distanciaMaximaCercania shouldBe 15
            }

            it("Usuario con menos de TOPE_FIGURITAS_PARA_TRIPLICAR NO multiplica 2 veces seguidas su cercania con el otro usuario") {
                //Active
                usuarioGeneral.pedirFigurita(figuritaComun, usuarioCercano)
                usuarioGeneral.pedirFigurita(figuritaBase, usuarioCercano)

                //Assert
                usuarioGeneral.distanciaMaximaCercania shouldBe 15
            }

            it("Usuario con menos de TOPE_FIGURITAS_PARA_TRIPLICAR multiplica 2 veces seguidas al volver a activar la acción UsuarioTriplicaKM su cercania con el otro usuario") {
                //Active
                usuarioGeneral.pedirFigurita(figuritaComun, usuarioCercano)
                usuarioGeneral.activarAccion(UsuarioTriplicaKM())
                usuarioGeneral.pedirFigurita(figuritaBase, usuarioCercano)

                //Assert
                usuarioGeneral.distanciaMaximaCercania shouldBe 45
            }
        }

        describe("test solicitud de figuirtias con accion FelicitarUsuario") {
            val stubMailSender = StubMailSender()
            it("Usuario completa el album y se le envia un correo felicitandolo") {
                //arrange
                val usuarioAlbumFull = usuarioGeneral.apply {
                    addFiguritaFaltante(figuritaComun)
                }
                val usuarioCercanoConFiguRep = usuarioCercano.apply {
                    condicionParaDar = Desprendido(this)
                    addFiguritaRepetida(figuritaComun)
                }
                //active
                usuarioAlbumFull.activarAccion(FelicitarUsuario(stubMailSender))
                usuarioAlbumFull.pedirFigurita(figuritaComun, usuarioCercanoConFiguRep)
                //assert
                stubMailSender.mailsEnviados.size.shouldBe(1)
            }
            it("Usuario si no completa el album no recibe el mail de felcitaciones") {
                //arrange
                val usuarioAlbumFull = usuarioGeneral.apply {
                    addFiguritaFaltante(figuritaComun)
                    addFiguritaFaltante(figuritaBase)
                }
                val usuarioCercanoConFiguRep = usuarioCercano.apply {
                    condicionParaDar = Desprendido(this)
                    addFiguritaRepetida(figuritaComun)
                }
                //active
                usuarioAlbumFull.activarAccion(FelicitarUsuario(stubMailSender))
                usuarioAlbumFull.pedirFigurita(figuritaComun, usuarioCercanoConFiguRep)
                //assert
                stubMailSender.mailsEnviados.size.shouldBe(0)
            }
        }
        describe("Test solicitud de figuritas con accion IncorporarFiguritaReservada") {
            it("Si usuario Solicitante si posee figurita repetida, se registra como repetida en lista RepetidaReservada") {
                val usuarioConFiguritaRepetida = usuarioGeneral.apply {
                    addFiguritaRepetida(figuritaComun)
                    addFiguritaFaltante(figuritaBase)
                }
                usuarioCercano.apply { addFiguritaRepetida(figuritaBase) }
                val incorporarFiguritaARepetidasReservadas =
                    IncorporarFiguritaARepetidasReservadas(usuarioConFiguritaRepetida, figuritaComun)

                usuarioConFiguritaRepetida.activarAccion(incorporarFiguritaARepetidasReservadas)
                usuarioConFiguritaRepetida.pedirFigurita(figuritaBase, usuarioCercano)

                incorporarFiguritaARepetidasReservadas.figuritasReservadas.isEmpty().shouldBeFalse()

            }
        }
    }
})