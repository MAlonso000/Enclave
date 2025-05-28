package com.marioalonso.enclave.classes

import java.util.*
import javax.crypto.SecretKey

// Abstract class for secrets
abstract class Secret(
    var title: String,
    var id: String = UUID.randomUUID().toString(),
    var deckId: String = UUID.randomUUID().toString()
) {
    abstract fun getContent(password: SecretKey): String
    abstract fun getContentRaw(): String
}