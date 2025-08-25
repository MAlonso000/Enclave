package com.marioalonso.enclave.classes

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Clase que representa una carpeta para organizar secretos.
 *
 * @property id Identificador único de la carpeta.
 * @property name Nombre de la carpeta.
 */
@Entity(tableName = "folders")
open class Folder(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var name: String,
) {
}