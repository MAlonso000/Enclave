package com.marioalonso.enclave.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.marioalonso.enclave.classes.Folder


@Entity(
    tableName = "secrets",
    indices = [Index(value = ["folderId"])],
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SecretEntity(
    @PrimaryKey
    var id: String,
    var title: String,
    var folderId: String? = null,
    var type: String, // "Credential", "Note", "Card"
    var username: String? = null,
    var encryptedPassword: String? = null,
    var email: String? = null,
    var url: String? = null,
    var encryptedNote: String? = null,
    var ownerName: String? = null,
    var encryptedCardNumber: String? = null,
    var encryptedPin: String? = null,
    var brand: String? = null,
    var expirationDate: String? = null,
    var encryptedCVV: String? = null
)