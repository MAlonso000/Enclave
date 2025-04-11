package com.marioalonso.enclave.classes

import java.util.*

// Abstract class for secrets
abstract class Secret(
    var title: String,
    var id: String = UUID.randomUUID().toString()

) {

    abstract fun getContent(password: String): String
    abstract fun getContentRaw(): String
}