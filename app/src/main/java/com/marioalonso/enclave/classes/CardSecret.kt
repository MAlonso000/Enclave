package com.marioalonso.enclave.classes

import com.marioalonso.enclave.utils.AESCipherGCM
import java.util.*
import javax.crypto.SecretKey

class CardSecret (
    id: String = UUID.randomUUID().toString(),
    title: String,
    folderId : String = UUID.randomUUID().toString(),
    var ownerName: String,
    var encryptedCardNumber: String,
    var brand: String = "Unknown",
    var expirationDate: String,
    var encryptedCVV: String,


): Secret(id, title, folderId) {
    override fun getContentRaw(): String {
        return "Owner: $ownerName, Card Number: $encryptedCardNumber, Brand: $brand, Expiration Date: $expirationDate, CVV: $encryptedCVV"
    }

    override fun getContent(password: SecretKey): String {
        val decryptedCardNumber = AESCipherGCM.decrypt(encryptedCardNumber, password)
        val decryptedCVV = AESCipherGCM.decrypt(encryptedCVV, password)
        return "Owner: $ownerName, Card Number: $decryptedCardNumber, Brand: $brand, Expiration Date: $expirationDate, CVV: $decryptedCVV"
    }

    override fun toString(): String {
        return "Tarjeta | $title | $ownerName | $encryptedCardNumber | $brand | $expirationDate | $encryptedCVV"
    }
}