package com.marioalonso.enclave.classes

import com.marioalonso.enclave.utils.AESCipherGCM
import java.util.*
import javax.crypto.SecretKey

class NoteSecret(
    id: String = UUID.randomUUID().toString(),
    title: String,
    folderId : String? = null,
    var encryptedNote: String,
) : Secret(id, title, folderId) {

    override fun getContentRaw(): String {
        return "Contenido: $encryptedNote"
    }

    override fun getContent(password: SecretKey): String {
        val decryptedNote = AESCipherGCM.decrypt(encryptedNote, password)
        return "Contenido: $decryptedNote"
    }

    override fun toString(): String {
        return "Nota | $title | $encryptedNote"
    }
}