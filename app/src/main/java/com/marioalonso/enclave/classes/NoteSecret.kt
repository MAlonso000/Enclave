package com.marioalonso.enclave.classes

import java.util.UUID

/**
 * Clase que representa una nota secreta.
 *
 * @property encryptedNote El contenido cifrado de la nota.
 * @constructor
 * Crea una nueva instancia de NoteSecret.
 *
 * @param id El ID único de la nota.
 * @param title El título de la nota.
 * @param folderId El ID de la carpeta a la que pertenece la nota (opcional).
 */
class NoteSecret(
    id: String = UUID.randomUUID().toString(),
    title: String,
    folderId : String? = null,
    var encryptedNote: String,
) : Secret(id, title, folderId) {

    override fun toString(): String {
        return "Nota | $title | $encryptedNote"
    }
}