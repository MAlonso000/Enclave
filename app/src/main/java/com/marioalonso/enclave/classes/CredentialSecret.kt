package com.marioalonso.enclave.classes

import com.marioalonso.enclave.utils.AESCipherGCM
import java.util.*
import javax.crypto.SecretKey

class CredentialSecret(
    id: String = UUID.randomUUID().toString(),
    title: String,
    folderId : String = UUID.randomUUID().toString(),
    var username: String,
    var encryptedPassword: String,
    var email: String,
    var url: String
) : Secret(id, title, folderId) {

    override fun getContentRaw(): String {
        return "User: $username, Pass: $encryptedPassword"
    }

    override fun getContent(password: SecretKey): String {
        val decryptedPassword = AESCipherGCM.decrypt(encryptedPassword, password)
        return "User: $username, Pass: $decryptedPassword"
    }

    override fun toString(): String {
        return "Credencial | $title | $username | $encryptedPassword"
    }
}