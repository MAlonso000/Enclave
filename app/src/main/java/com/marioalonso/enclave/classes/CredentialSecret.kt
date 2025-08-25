package com.marioalonso.enclave.classes

import com.marioalonso.enclave.utils.AESCipherGCM
import java.util.UUID
import javax.crypto.SecretKey

/**
 * Clase que representa un secreto de tipo credencial.
 *
 * @property username Nombre de usuario asociado a la credencial.
 * @property encryptedPassword Contraseña cifrada asociada a la credencial.
 * @property email Correo electrónico asociado a la credencial.
 * @property url URL asociada a la credencial.
 * @constructor
 * Crea una nueva instancia de CredentialSecret.
 *
 * @param id Identificador único del secreto.
 * @param title Título del secreto.
 * @param folderId Identificador de la carpeta donde se almacena el secreto (opcional).
 */
class CredentialSecret(
    id: String = UUID.randomUUID().toString(),
    title: String,
    folderId : String? = null,
    var username: String,
    var encryptedPassword: String,
    var email: String,
    var url: String
) : Secret(id, title, folderId) {

    override fun getContentRaw(): String {
        return "User: $username, Pass: $encryptedPassword"
    }

    override fun getContent(password: SecretKey): String {
        val decryptedPassword = AESCipherGCM.decrypt(encryptedPassword)
        return "User: $username, Pass: $decryptedPassword"
    }

    override fun toString(): String {
        return "Credencial | $title | $username | $encryptedPassword"
    }
}