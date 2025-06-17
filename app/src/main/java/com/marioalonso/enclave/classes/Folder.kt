package com.marioalonso.enclave.classes

import java.util.UUID
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
open class Folder(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var name: String,
) {
}