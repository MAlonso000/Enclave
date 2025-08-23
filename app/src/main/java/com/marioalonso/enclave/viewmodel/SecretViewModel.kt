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
import com.marioalonso.enclave.utils.AESCipherGCM
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
            else -> throw IllegalArgumentException("Unknown type: ${entity.type}")
        }
    }



//    // Almacenamiento temporal para los SecretEntity descifrados
//    private var temporaryDecryptedSecrets = mutableListOf<SecretEntity>()
//
//    /**
//     * Descifra todos los secretos y guarda los objetos SecretEntity temporalmente en memoria
//     * para su posterior recifrado con nueva clave
//     */
//    fun decryptAllSecrets() = viewModelScope.launch {
//        temporaryDecryptedSecrets.clear()
//
//        val allSecrets = enclaveDao.getAllSecretsSync()
//
//        for (secretEntity in allSecrets) {
//            // Creamos una copia del SecretEntity para modificarla
//            val decryptedEntity = when (secretEntity.type) {
//                "Credential" -> {
//                    val newEntity = secretEntity.copy()
//                    secretEntity.encryptedPassword?.let {
//                        // Guardamos la contraseña descifrada en el mismo campo
//                        newEntity.encryptedPassword = AESCipherGCM.decrypt(it)
//                    }
//                    newEntity
//                }
//                "Note" -> {
//                    val newEntity = secretEntity.copy()
//                    secretEntity.encryptedNote?.let {
//                        newEntity.encryptedNote = AESCipherGCM.decrypt(it)
//                    }
//                    newEntity
//                }
//                "Card" -> {
//                    val newEntity = secretEntity.copy()
//                    secretEntity.encryptedCardNumber?.let {
//                        newEntity.encryptedCardNumber = AESCipherGCM.decrypt(it)
//                    }
//                    secretEntity.encryptedCVV?.let {
//                        newEntity.encryptedCVV = AESCipherGCM.decrypt(it)
//                    }
//                    secretEntity.encryptedPin?.let {
//                        newEntity.encryptedPin = AESCipherGCM.decrypt(it)
//                    }
//                    newEntity
//                }
//                else -> secretEntity
//            }
//
//            temporaryDecryptedSecrets.add(decryptedEntity)
//        }
//    }
//
//    /**
//     * Recifra todos los secretos utilizando la nueva clave maestra
//     */
//    fun encryptAllSecrets() = viewModelScope.launch {
//        for (decryptedEntity in temporaryDecryptedSecrets) {
//            // Creamos una nueva entidad con los valores recifrados
//            val encryptedEntity = when (decryptedEntity.type) {
//                "Credential" -> {
//                    val newEntity = decryptedEntity.copy()
//                    decryptedEntity.encryptedPassword?.let {
//                        newEntity.encryptedPassword = AESCipherGCM.encrypt(it)
//                    }
//                    newEntity
//                }
//                "Note" -> {
//                    val newEntity = decryptedEntity.copy()
//                    decryptedEntity.encryptedNote?.let {
//                        newEntity.encryptedNote = AESCipherGCM.encrypt(it)
//                    }
//                    newEntity
//                }
//                "Card" -> {
//                    val newEntity = decryptedEntity.copy()
//                    decryptedEntity.encryptedCardNumber?.let {
//                        newEntity.encryptedCardNumber = AESCipherGCM.encrypt(it)
//                    }
//                    decryptedEntity.encryptedCVV?.let {
//                        newEntity.encryptedCVV = AESCipherGCM.encrypt(it)
//                    }
//                    decryptedEntity.encryptedPin?.let {
//                        newEntity.encryptedPin = AESCipherGCM.encrypt(it)
//                    }
//                    newEntity
//                }
//                else -> decryptedEntity
//            }
//
//            enclaveDao.updateSecret(encryptedEntity)
//        }
//
//        // Limpiar datos temporales por seguridad
//        temporaryDecryptedSecrets.clear()
//    }

    // Almacenamiento temporal para los SecretEntity descifrados
    private var temporaryDecryptedSecrets = mutableListOf<SecretEntity>()

    /**
     * Descifra todos los secretos de forma síncrona
     */
    fun decryptAllSecrets() {
        temporaryDecryptedSecrets.clear()

        // Obtenemos todos los secretos de forma síncrona bloqueante
        val allSecrets = runBlocking { enclaveDao.getAllSecretsSync() }

        for (secretEntity in allSecrets) {
            val decryptedEntity = when (secretEntity.type) {
                "Credential" -> {
                    val newEntity = secretEntity.copy()
                    secretEntity.encryptedPassword?.let {
                        newEntity.encryptedPassword = AESCipherGCM.decrypt(it)
                    }
                    newEntity
                }
                "Note" -> {
                    val newEntity = secretEntity.copy()
                    secretEntity.encryptedNote?.let {
                        newEntity.encryptedNote = AESCipherGCM.decrypt(it)
                    }
                    newEntity
                }
                "Card" -> {
                    val newEntity = secretEntity.copy()
                    secretEntity.encryptedCardNumber?.let {
                        newEntity.encryptedCardNumber = AESCipherGCM.decrypt(it)
                    }
                    secretEntity.encryptedCVV?.let {
                        newEntity.encryptedCVV = AESCipherGCM.decrypt(it)
                    }
                    secretEntity.encryptedPin?.let {
                        newEntity.encryptedPin = AESCipherGCM.decrypt(it)
                    }
                    newEntity
                }
                else -> secretEntity
            }

            temporaryDecryptedSecrets.add(decryptedEntity)
        }
    }

    /**
     * Recifra todos los secretos de forma síncrona
     */
    fun encryptAllSecrets() {
        for (decryptedEntity in temporaryDecryptedSecrets) {
            val encryptedEntity = when (decryptedEntity.type) {
                "Credential" -> {
                    val newEntity = decryptedEntity.copy()
                    decryptedEntity.encryptedPassword?.let {
                        newEntity.encryptedPassword = AESCipherGCM.encrypt(it)
                    }
                    newEntity
                }
                "Note" -> {
                    val newEntity = decryptedEntity.copy()
                    decryptedEntity.encryptedNote?.let {
                        newEntity.encryptedNote = AESCipherGCM.encrypt(it)
                    }
                    newEntity
                }
                "Card" -> {
                    val newEntity = decryptedEntity.copy()
                    decryptedEntity.encryptedCardNumber?.let {
                        newEntity.encryptedCardNumber = AESCipherGCM.encrypt(it)
                    }
                    decryptedEntity.encryptedCVV?.let {
                        newEntity.encryptedCVV = AESCipherGCM.encrypt(it)
                    }
                    decryptedEntity.encryptedPin?.let {
                        newEntity.encryptedPin = AESCipherGCM.encrypt(it)
                    }
                    newEntity
                }
                else -> decryptedEntity
            }

            // Actualización síncrona bloqueante de la BD
            runBlocking { enclaveDao.updateSecret(encryptedEntity) }
        }

        // Limpiar datos temporales por seguridad
        temporaryDecryptedSecrets.clear()
    }
}



class SecretViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SecretViewModel(application) as T
    }
}