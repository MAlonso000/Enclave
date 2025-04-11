package com.marioalonso.enclave.classes

import com.marioalonso.enclave.utils.AESCipherGCM
import java.util.*

// Concrete implementations
class CredentialSecret(
    title: String,
    var username: String,
    var encryptedPassword: String,
    id: String = UUID.randomUUID().toString(),
) : Secret(title, id) {

    override fun getContentRaw(): String {
        return "User: $username, Pass: $encryptedPassword"
    }

    override fun getContent(password: String): String {
        val decryptedPassword = AESCipherGCM.decrypt(encryptedPassword, password)
        return "User: $username, Pass: $decryptedPassword"
    }

    override fun toString(): String {
        return "Credencial | $title | $username | $encryptedPassword"
    }

    companion object {
        fun createCredential(password: String): CredentialSecret {
            println("Añadiendo credencial a la carpeta ")
            print("  Teclea el título: ")
            val title = readln()
            print("  Teclea el usuario: ")
            val user = readln()
            print("  Teclea la contraseña: ")
            val pass = readln()

            val encryptedPass = AESCipherGCM.encrypt(pass, password)
            val credential = CredentialSecret(title, user, encryptedPass)

            return credential
        }

        fun fromString(data: String): CredentialSecret {
            val parts = data.split(" | ")
            if (parts.size != 4 || parts[0] != "Credencial") {
                throw IllegalArgumentException("Invalid CredentialSecret format")
            }
            val title = parts[1]
            val username = parts[2]
            val encryptedPassword = parts[3]
            return CredentialSecret(title, username, encryptedPassword)
        }
    }
}