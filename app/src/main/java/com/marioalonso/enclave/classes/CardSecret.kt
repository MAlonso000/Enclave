package com.marioalonso.enclave.classes

import com.marioalonso.enclave.utils.AESCipherGCM
import java.util.*

class CardSecret (
    title: String,
    var ownerName: String,
    var encryptedCardNumber: String,
    var brand: String = "Unknown",
    var expirationDate: String,
    var encryptedCVV: String,
    id: String = UUID.randomUUID().toString(),
): Secret(title, id) {
    override fun getContentRaw(): String {
        return "Owner: $ownerName, Card Number: $encryptedCardNumber, Brand: $brand, Expiration Date: $expirationDate, CVV: $encryptedCVV"
    }

    override fun getContent(password: String): String {
        val decryptedCardNumber = AESCipherGCM.decrypt(encryptedCardNumber, password)
        val decryptedCVV = AESCipherGCM.decrypt(encryptedCVV, password)
        return "Owner: $ownerName, Card Number: $decryptedCardNumber, Brand: $brand, Expiration Date: $expirationDate, CVV: $decryptedCVV"
    }

    override fun toString(): String {
        return "Tarjeta | $title | $ownerName | $encryptedCardNumber | $brand | $expirationDate | $encryptedCVV"
    }

    companion object {
        fun createCard(password: String): CardSecret {
            println("Añadiendo tarjeta a la carpeta ")
            print("  Teclea el título: ")
            val title = readln()
            print("  Teclea el nombre del propietario: ")
            val ownerName = readln()
            print("  Teclea el número de tarjeta: ")
            val cardNumber = readln()
            print("  Teclea la marca de la tarjeta (opcional): ")
            val brand = readln()
            print("  Teclea la fecha de expiración (MM/AA): ")
            val expirationDate = readln()
            print("  Teclea el CVV: ")
            val cvv = readln()

            val encryptedCardNumber = AESCipherGCM.encrypt(cardNumber, password)
            val encryptedCVV = AESCipherGCM.encrypt(cvv, password)

            val card = CardSecret(title, ownerName, encryptedCardNumber, brand, expirationDate, encryptedCVV)

            return card
        }

        fun fromString(data: String): CardSecret {
            val parts = data.split(" | ")
            if (parts.size != 7 || parts[0] != "Tarjeta") {
                throw IllegalArgumentException("Invalid CardSecret format")
            }
            val title = parts[1]
            val ownerName = parts[2]
            val encryptedCardNumber = parts[3]
            val brand = parts[4]
            val expirationDate = parts[5]
            val encryptedCVV = parts[6]
            return CardSecret(title, ownerName, encryptedCardNumber, brand, expirationDate, encryptedCVV)
        }
    }
}