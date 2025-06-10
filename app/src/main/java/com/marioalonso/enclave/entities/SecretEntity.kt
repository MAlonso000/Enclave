package com.marioalonso.enclave.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "secrets")
data class SecretEntity(
    @PrimaryKey val id: String,
    val title: String,
    val folderId: String,
    val type: String, // "Credential", "Note", "Card"
    val username: String? = null,
    val encryptedPassword: String? = null,
    val encryptedNote: String? = null,
    val ownerName: String? = null,
    val encryptedCardNumber: String? = null,
    val brand: String? = null,
    val expirationDate: String? = null,
    val encryptedCVV: String? = null
)