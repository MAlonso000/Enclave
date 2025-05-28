package com.marioalonso.enclave.classes

object SecretFactory {
    fun createSecret(
        type: SecretType,
        title: String,
        data: Map<String, String>
    ): Secret {
        return when (type) {
            SecretType.CARD -> CardSecret(
                title = title,
                ownerName = data["ownerName"] ?: "",
                encryptedCardNumber = data["encryptedCardNumber"] ?: "",
                brand = data["brand"] ?: "Unknown",
                expirationDate = data["expirationDate"] ?: "",
                encryptedCVV = data["encryptedCVV"] ?: ""
            )
            SecretType.CREDENTIAL -> CredentialSecret(
                title = title,
                username = data["username"] ?: "",
                encryptedPassword = data["encryptedPassword"] ?: ""
            )
            SecretType.NOTE -> NoteSecret(
                title = title,
                encryptedNote = data["encryptedNote"] ?: ""
            )
        }
    }
}

/* Ejemplos de uso:
val cardSecret = SecretFactory.createSecret(
    SecretType.CARD,
    "Mi Tarjeta",
    mapOf(
        "ownerName" to "Mario Alonso",
        "encryptedCardNumber" to "encryptedNumber",
        "brand" to "Visa",
        "expirationDate" to "12/25",
        "encryptedCVV" to "encryptedCVV"
    )
)

val credentialSecret = SecretFactory.createSecret(
    SecretType.CREDENTIAL,
    "Mi Credencial",
    mapOf(
        "username" to "mario",
        "encryptedPassword" to "encryptedPassword"
    )
)

val noteSecret = SecretFactory.createSecret(
    SecretType.NOTE,
    "Mi Nota",
    mapOf(
        "encryptedNote" to "encryptedNote"
    )
)
 */