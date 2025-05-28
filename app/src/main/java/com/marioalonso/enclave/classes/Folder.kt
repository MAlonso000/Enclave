package com.marioalonso.enclave.classes

import java.util.UUID

class Folder(
    val folderId: String = UUID.randomUUID().toString(),
    val name: String,
) {
}