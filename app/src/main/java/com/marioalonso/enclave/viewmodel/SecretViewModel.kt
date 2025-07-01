package com.marioalonso.enclave.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marioalonso.enclave.dao.EnclaveDao
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.marioalonso.enclave.classes.CardSecret
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.classes.SecretFactory
import com.marioalonso.enclave.classes.SecretType
import com.marioalonso.enclave.database.EnclaveDatabase
import com.marioalonso.enclave.entities.SecretEntity
import kotlinx.coroutines.launch

class SecretViewModel(application: Application) : ViewModel() {

    private val enclaveDao: EnclaveDao
    var secrets: LiveData<List<Secret>>
    var folders: LiveData<List<Folder>>

    init {
        enclaveDao = EnclaveDatabase.getInstance(application.applicationContext).enclaveDao
        secrets = enclaveDao.getAllSecrets().map { secretEntities ->
            secretEntities.map { entity -> mapToSecret(entity) }
        }
        folders = enclaveDao.getAllFolders()
    }


    // region Insert
    fun insertSecret(secret: Secret) {
        viewModelScope.launch {
            val secretEntity = when (secret) {
                is CredentialSecret -> SecretEntity(
                    id = secret.id,
                    title = secret.title,
                    folderId = secret.folderId,
                    type = "Credential",
                    username = secret.username,
                    encryptedPassword = secret.encryptedPassword,
                    email = secret.email,
                    url = secret.url
                )
                is NoteSecret -> SecretEntity(
                    id = secret.id,
                    title = secret.title,
                    folderId = secret.folderId,
                    type = "Note",
                    encryptedNote = secret.encryptedNote
                )
                is CardSecret -> SecretEntity(
                    id = secret.id,
                    title = secret.title,
                    folderId = secret.folderId,
                    type = "Card",
                    ownerName = secret.ownerName,
                    encryptedCardNumber = secret.encryptedCardNumber,
                    brand = secret.brand,
                    expirationDate = secret.expirationDate,
                    encryptedCVV = secret.encryptedCVV,
                    encryptedPin = secret.encryptedPin
                )
                else -> throw IllegalArgumentException("Unknown secret type")
            }
            enclaveDao.insertSecret(secretEntity)
        }
    }

    fun insertFolder(folder: Folder) = viewModelScope.launch {
        enclaveDao.insertFolder(folder)
    }
    // endregion

    // region Delete
    fun deleteSecrets() {
        viewModelScope.launch {
            enclaveDao.deleteAllSecrets()
        }
    }

    fun deleteFolders() {
        viewModelScope.launch {
            enclaveDao.deleteAllFolders()
        }
    }

    fun deleteSecret(secretId: String) {
        viewModelScope.launch {
            enclaveDao.deleteSecretById(secretId)
        }
    }

    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            enclaveDao.deleteFolderById(folderId)
        }
    }
    // endregion

    // region Update
    // endregion

    // region Query
    fun getSecretsByFolderId(folderId: String): LiveData<List<Secret>> {
        return enclaveDao.getSecretsByFolderId(folderId).map { secretEntities ->
            secretEntities.map { entity -> mapToSecret(entity) }
        }
    }

    fun getFolderById(folderId: String) = enclaveDao.getFolderById(folderId)

//    fun getSecretById(secretId: String): LiveData<Secret?> {
//        Log.d("SecretViewModel", "Buscando secreto con ID: -$secretId-")
//        val secret = enclaveDao.getSecretById(secretId)
//        secret.observeForever { secretEntity ->
//            Log.d("SecretViewModel", "Resultado: ${secretEntity?.id ?: "null"}")
//        }
//        secret.removeObserver { }
//        return secret.map { entity ->
//            entity?.let { mapToSecret(it) }
//        }
//    }
    fun getSecretById(secretId: String): LiveData<Secret?> {
        return enclaveDao.getSecretById(secretId).map { entity ->
            entity?.let {
                mapToSecret(it)
            }
        }
    }
    // endregion

    private fun mapToSecret(entity: SecretEntity): Secret {
        return when (entity.type) {
            "Credential" -> CredentialSecret(
                id = entity.id,
                title = entity.title,
                folderId = entity.folderId,
                username = entity.username ?: "",
                encryptedPassword = entity.encryptedPassword ?: "",
                email = entity.email ?: "",
                url = entity.url ?: ""
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
                encryptedPin = entity.encryptedPin ?: "",
                brand = entity.brand ?: "",
                expirationDate = entity.expirationDate ?: "",
                encryptedCVV = entity.encryptedCVV ?: ""
            )
            else -> throw IllegalArgumentException("Tipo desconocido: ${entity.type}")
        }
    }
}

class SecretViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SecretViewModel(application) as T
    }
}