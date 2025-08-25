package com.marioalonso.enclave.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.marioalonso.enclave.classes.CardSecret
import com.marioalonso.enclave.classes.CredentialSecret
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.classes.NoteSecret
import com.marioalonso.enclave.classes.Secret
import com.marioalonso.enclave.dao.EnclaveDao
import com.marioalonso.enclave.database.EnclaveDatabase
import com.marioalonso.enclave.entities.SecretEntity
import com.marioalonso.enclave.utils.AESCipherGCM
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * ViewModel para gestionar los secretos y carpetas en la aplicación.
 * Proporciona métodos para insertar, eliminar, actualizar y consultar secretos y carpetas.
 * Además, maneja el cifrado y descifrado de los datos sensibles.
 */
class SecretViewModel(application: Application) : ViewModel() {

    private val enclaveDao: EnclaveDao
    var secrets: LiveData<List<Secret>>
    var folders: LiveData<List<Folder>>

    /**
     * Inicializa el ViewModel obteniendo la instancia del DAO y configurando los LiveData para secretos y carpetas.
     */
    init {
        enclaveDao = EnclaveDatabase.getInstance(application.applicationContext).enclaveDao
        secrets = enclaveDao.getAllSecrets().map { secretEntities ->
            secretEntities.map { entity -> mapToSecret(entity) }
        }
        folders = enclaveDao.getAllFolders()
    }


    // region Insert
    /**
     * Inserta un secreto en la base de datos.
     * Convierte el objeto Secret en SecretEntity antes de la inserción.
     *
     * @param secret El secreto a insertar (puede ser CredentialSecret, NoteSecret o CardSecret).
     * @throws IllegalArgumentException Si el tipo de secreto es desconocido.
     */
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

    /**
     * Inserta una carpeta en la base de datos.
     *
     * @param folder La carpeta a insertar.
     */
    fun insertFolder(folder: Folder) = viewModelScope.launch {
        enclaveDao.insertFolder(folder)
    }
    // endregion

    // region Delete
    /**
     * Elimina todos los secretos de la base de datos.
     */
    fun deleteSecrets() {
        viewModelScope.launch {
            enclaveDao.deleteAllSecrets()
        }
    }

    /**
     * Elimina todas las carpetas de la base de datos.
     */
    fun deleteFolders() {
        viewModelScope.launch {
            enclaveDao.deleteAllFolders()
        }
    }

    /**
     * Elimina un secreto específico por su ID.
     *
     * @param secretId El ID del secreto a eliminar.
     */
    fun deleteSecret(secretId: String) {
        viewModelScope.launch {
            enclaveDao.deleteSecretById(secretId)
        }
    }

    /**
     * Elimina una carpeta específica por su ID.
     *
     * @param folderId El ID de la carpeta a eliminar.
     */
    fun deleteFolder(folderId: String) {
        viewModelScope.launch {
            enclaveDao.deleteFolderById(folderId)
        }
    }
    // endregion

    // region Update
    // endregion

    // region Query
    /**
     * Obtiene todos los secretos que pertenecen a una carpeta específica.
     *
     * @param folderId El ID de la carpeta cuyos secretos se desean obtener.
     * @return LiveData con la lista de secretos en la carpeta especificada.
     */
    fun getSecretsByFolderId(folderId: String): LiveData<List<Secret>> {
        return enclaveDao.getSecretsByFolderId(folderId).map { secretEntities ->
            secretEntities.map { entity -> mapToSecret(entity) }
        }
    }

    /**
     * Obtiene una carpeta específica por su ID.
     *
     * @param folderId El ID de la carpeta a obtener.
     * @return LiveData con la carpeta especificada, o null si no existe.
     */
    fun getFolderById(folderId: String) = enclaveDao.getFolderById(folderId)

    /**
     * Obtiene un secreto específico por su ID.
     *
     * @param secretId El ID del secreto a obtener.
     * @return LiveData con el secreto especificado, o null si no existe.
     */
    fun getSecretById(secretId: String): LiveData<Secret?> {
        return enclaveDao.getSecretById(secretId).map { entity ->
            entity?.let {
                mapToSecret(it)
            }
        }
    }
    // endregion
    /**
     * Mapea una entidad de secreto (SecretEntity) a su correspondiente clase de dominio (Secret).
     *
     * @param entity La entidad de secreto a mapear.
     * @return El objeto Secret correspondiente.
     * @throws IllegalArgumentException Si el tipo de entidad es desconocido.
     */
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

    // Almacenamiento temporal para los SecretEntity descifrados
    private var temporaryDecryptedSecrets = mutableListOf<SecretEntity>()

    /**
     * Descifra todos los secretos de forma síncrona y los almacena temporalmente en memoria.
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
     * Cifra todos los secretos que fueron descifrados previamente y almacenados temporalmente.
     * Actualiza la base de datos con los datos cifrados y limpia los datos temporales por seguridad.
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

/**
 * Factory para crear instancias de SecretViewModel con el parámetro Application.
 *
 * @param application La instancia de Application necesaria para el ViewModel.
 */
class SecretViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SecretViewModel(application) as T
    }
}