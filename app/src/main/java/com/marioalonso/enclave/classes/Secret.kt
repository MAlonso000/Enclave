package com.marioalonso.enclave.classes

import java.util.*
import javax.crypto.SecretKey

abstract class Secret(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var folderId: String = UUID.randomUUID().toString()
) {
    abstract fun getContent(password: SecretKey): String
    abstract fun getContentRaw(): String
}