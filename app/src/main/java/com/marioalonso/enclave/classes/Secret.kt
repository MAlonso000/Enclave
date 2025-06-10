package com.marioalonso.enclave.classes

import java.util.*
import javax.crypto.SecretKey

abstract class Secret(
    var title: String,
    val id: String = UUID.randomUUID().toString(),
    var deckId: String = UUID.randomUUID().toString()
) {
    abstract fun getContent(password: SecretKey): String
    abstract fun getContentRaw(): String
}