package com.marioalonso.enclave.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.marioalonso.enclave.classes.Folder

/**
 * Clase que representa una entidad de secreto en la base de datos.
 *
 * @property id El identificador único del secreto.
 * @property title El título del secreto.
 * @property folderId El identificador de la carpeta a la que pertenece el secreto (puede ser nulo).
 * @property type El tipo de secreto (por ejemplo, "Credential", "Note", "Card").
 * @property username El nombre de usuario asociado al secreto (puede ser nulo).
 * @property encryptedPassword La contraseña encriptada asociada al secreto (puede ser nulo).
 * @property email El correo electrónico asociado al secreto (puede ser nulo).
 * @property url La URL asociada al secreto (puede ser nulo).
 * @property encryptedNote La nota encriptada asociada al secreto (puede ser nulo).
 * @property ownerName El nombre del propietario de la tarjeta (puede ser nulo).
 * @property encryptedCardNumber El número de tarjeta encriptado (puede ser nulo).
 * @property encryptedPin El PIN encriptado de la tarjeta (puede ser nulo).
 * @property brand La marca de la tarjeta (puede ser nulo).
 * @property expirationDate La fecha de expiración de la tarjeta (puede ser nulo).
 * @property encryptedCVV El CVV encriptado de la tarjeta (puede ser nulo).
 */
@Entity(
    tableName = "secrets",
    indices = [Index(value = ["folderId"])],
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SecretEntity(
    @PrimaryKey
    var id: String,
    var title: String,
    var folderId: String? = null,
    var type: String, // "Credential", "Note", "Card"
    var username: String? = null,
    var encryptedPassword: String? = null,
    var email: String? = null,
    var url: String? = null,
    var encryptedNote: String? = null,
    var ownerName: String? = null,
    var encryptedCardNumber: String? = null,
    var encryptedPin: String? = null,
    var brand: String? = null,
    var expirationDate: String? = null,
    var encryptedCVV: String? = null
)