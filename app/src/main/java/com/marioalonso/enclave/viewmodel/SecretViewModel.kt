package com.marioalonso.enclave.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.marioalonso.enclave.dao.EnclaveDao
import androidx.lifecycle.LiveData
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

        deleteSecrets()
        deleteFolders()

        val folder1 = Folder(name = "Folder 1")
        insertFolder(folder1)

        val cardSecret = SecretFactory.createSecret(
            SecretType.CARD,
            "Mi Tarjeta",
            mapOf(
                "ownerName" to "Mario Alonso",
                "encryptedCardNumber" to "encryptedNumber",
                "brand" to "Visa",
                "expirationDate" to "12/25",
                "encryptedCVV" to "encryptedCVV",
                "folderId" to folder1.id
            )
        )
        val noteSecret = SecretFactory.createSecret(
            SecretType.NOTE,
            "Mi Nota",
            mapOf(
                "encryptedNote" to "encryptedNoteContent",
                "folderId" to folder1.id
            )
        )

        val credentialSecret = SecretFactory.createSecret(
            SecretType.CREDENTIAL,
            "Credencial de Usuario",
            mapOf(
                "username" to "usuario123",
                "encryptedPassword" to "encryptedPassword",
                "folderId" to folder1.id
            )
        )

        val secret = CredentialSecret(
            id = "secret1",
            title = "Secreto General",
//            folderId = null,
            username = "generalUser",
            encryptedPassword = "generalEncryptedPassword",
            email = "james.k.polk@examplepetstore.com",
            url = "https://examplepetstore.com"
        )

        insertSecret(cardSecret)
        insertSecret(noteSecret)
        insertSecret(credentialSecret)
        insertSecret(secret)
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
                    encryptedCVV = secret.encryptedCVV
                )
                else -> throw IllegalArgumentException("Tipo de secreto desconocido")
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
    fun getSecretsByFolderName(folderName: String): LiveData<List<Secret>> {
        return enclaveDao.getSecretsByFolderName(folderName).map { secretEntities ->
            secretEntities.map { entity -> mapToSecret(entity) }
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
            else -> throw IllegalArgumentException("Unknown type: ${entity.type}")
        }
    }
}

class SecretViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SecretViewModel(application) as T
    }
}