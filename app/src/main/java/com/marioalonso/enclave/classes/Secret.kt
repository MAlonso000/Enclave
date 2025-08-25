package com.marioalonso.enclave.classes

import java.util.UUID
import javax.crypto.SecretKey

/**
 * Clase abstracta que representa un secreto genérico.
 *
 * @property id Identificador único del secreto.
 * @property title Título del secreto.
 * @property folderId Identificador de la carpeta que contiene el secreto (opcional).
 */
abstract class Secret(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var folderId: String? = null
) {
    abstract fun getContent(password: SecretKey): String
    abstract fun getContentRaw(): String
}