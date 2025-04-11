package com.marioalonso.enclave.classes

import com.marioalonso.enclave.utils.AESCipherGCM
import java.util.*

class NoteSecret(
    title: String,
    var encryptedNote: String,
    id: String = UUID.randomUUID().toString(),
) : Secret(title, id) {

    override fun getContentRaw(): String {
        return "Contenido: $encryptedNote"
    }

    override fun getContent(password: String): String {
        val decryptedNote = AESCipherGCM.decrypt(encryptedNote, password)
        return "Contenido: $decryptedNote"
    }

    override fun toString(): String {
        return "Nota | $title | $encryptedNote"
    }

    companion object {
        fun createNote(password: String): NoteSecret {
            println("Añadiendo nota a la carpeta ")
            print("  Teclea el título: ")
            val title = readln()
            print("  Teclea el contenido: ")
            val content = readln()

            val encryptedNote = AESCipherGCM.encrypt(content, password)
            val note = NoteSecret(title, encryptedNote)

            return note
        }

        fun fromString(data: String): NoteSecret {
            val parts = data.split(" | ")
            if (parts.size != 3 || parts[0] != "Nota") {
                throw IllegalArgumentException("Invalid NoteSecret format")
            }
            val title = parts[1]
            val encryptedNote = parts[2]
            return NoteSecret(title, encryptedNote)
        }
    }
}