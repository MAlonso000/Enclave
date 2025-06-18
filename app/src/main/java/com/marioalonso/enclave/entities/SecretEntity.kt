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
    val id: String,
    val title: String,
    val folderId: String? = null,
    val type: String, // "Credential", "Note", "Card"
    val username: String? = null,
    val encryptedPassword: String? = null,
    val email: String? = null,
    val url: String? = null,
    val encryptedNote: String? = null,
    val ownerName: String? = null,
    val encryptedCardNumber: String? = null,
    val encryptedPin: String? = null,
    val brand: String? = null,
    val expirationDate: String? = null,
    val encryptedCVV: String? = null
)