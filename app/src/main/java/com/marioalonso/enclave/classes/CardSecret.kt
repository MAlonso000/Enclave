package com.marioalonso.enclave.classes

import com.marioalonso.enclave.utils.AESCipherGCM
import java.util.UUID
import javax.crypto.SecretKey

/**
 * Clase que representa un secreto de tipo tarjeta.
 *
 * @property ownerName Nombre del propietario de la tarjeta.
 * @property encryptedCardNumber Número de tarjeta cifrado.
 * @property encryptedPin PIN cifrado de la tarjeta.
 * @property brand Marca de la tarjeta.
 * @property expirationDate Fecha de expiración de la tarjeta.
 * @property encryptedCVV CVV cifrado de la tarjeta.
 * @constructor
 * Crea una nueva instancia de CardSecret.
 *
 * @param id Identificador único del secreto.
 * @param title Título del secreto.
 * @param folderId Identificador de la carpeta a la que pertenece el secreto (opcional).
 */
class CardSecret (
    id: String = UUID.randomUUID().toString(),
    title: String,
    folderId : String? = null,
    var ownerName: String,
    var encryptedCardNumber: String,
    var encryptedPin: String,
    var brand: String = "Unknown",
    var expirationDate: String,
    var encryptedCVV: String,
): Secret(id, title, folderId) {
    override fun getContentRaw(): String {
        return "Owner: $ownerName, Card Number: $encryptedCardNumber, PIN: $encryptedPin, Brand: $brand, Expiration Date: $expirationDate, CVV: $encryptedCVV"
    }

    override fun getContent(password: SecretKey): String {
        val decryptedCardNumber = AESCipherGCM.decrypt(encryptedCardNumber)
        val decryptedCVV = AESCipherGCM.decrypt(encryptedCVV)
        return "Owner: $ownerName, Card Number: $decryptedCardNumber, PIN: $encryptedPin, Brand: $brand, Expiration Date: $expirationDate, CVV: $decryptedCVV"
    }

    override fun toString(): String {
        return "Tarjeta | $title | $ownerName | $encryptedCardNumber | $encryptedPin | $brand | $expirationDate | $encryptedCVV"
    }
}