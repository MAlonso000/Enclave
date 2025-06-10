package com.marioalonso.enclave.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marioalonso.enclave.dao.EnclaveDao
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.marioalonso.enclave.classes.CardSecret
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.classes.SecretFactory
import com.marioalonso.enclave.classes.SecretType
import com.marioalonso.enclave.database.EnclaveDatabase
import com.marioalonso.enclave.entities.SecretEntity
import kotlinx.coroutines.launch

class SecretViewModel(application: Application) : ViewModel() {

    private val enclaveDao: EnclaveDao
    private val _secrets = MutableLiveData<List<Secret>>()
    val secrets: LiveData<List<Secret>> get() = _secrets

    init {
        enclaveDao = EnclaveDatabase.getInstance(application.applicationContext).enclaveDao

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

        insertSecret(cardSecret)

        loadSecrets()
    }

    private fun loadSecrets() {
        viewModelScope.launch {
            val rawSecrets = enclaveDao.getAllSecrets()
            _secrets.postValue(rawSecrets.map { mapToSecret(it) })
        }
    }

    private fun mapToSecret(entity: SecretEntity): Secret {
        return when (entity.type) {
            "Credential" -> CredentialSecret(
                id = entity.id,
                title = entity.title,
                folderId = entity.folderId,
                username = entity.username ?: "",
                encryptedPassword = entity.encryptedPassword ?: ""
            )
            "Note" -> NoteSecret(
                id = entity.id,
                title = entity.title,
                folderId = entity.folderId,
                encryptedNote = entity.encryptedNote ?: ""
            )
            "Card" -> CardSecret(
                id = entity.id,
                title = entity.title,
                folderId = entity.folderId,
                ownerName = entity.ownerName ?: "",
                encryptedCardNumber = entity.encryptedCardNumber ?: "",
                brand = entity.brand ?: "",
                expirationDate = entity.expirationDate ?: "",
                encryptedCVV = entity.encryptedCVV ?: ""
            )
            else -> throw IllegalArgumentException("Tipo desconocido: ${entity.type}")
        }
    }

    fun insertSecret(secret: Secret) {
        viewModelScope.launch {
            val secretEntity = when (secret) {
                is CredentialSecret -> SecretEntity(
                    id = secret.id,
                    title = secret.title,
                    folderId = secret.deckId,
                    type = "Credential",
                    username = secret.username,
                    encryptedPassword = secret.encryptedPassword
                )
                is NoteSecret -> SecretEntity(
                    id = secret.id,
                    title = secret.title,
                    folderId = secret.deckId,
                    type = "Note",
                    encryptedNote = secret.encryptedNote
                )
                is CardSecret -> SecretEntity(
                    id = secret.id,
                    title = secret.title,
                    folderId = secret.deckId,
                    type = "Card",
                    ownerName = secret.ownerName,
                    encryptedCardNumber = secret.encryptedCardNumber,
                    brand = secret.brand,
                    expirationDate = secret.expirationDate,
                    encryptedCVV = secret.encryptedCVV
                )
                else -> throw IllegalArgumentException("Tipo de secreto desconocido")
            }
            enclaveDao.insertSecret(secretEntity)
        }
    }
}

class SecretViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SecretViewModel(application) as T
    }
}