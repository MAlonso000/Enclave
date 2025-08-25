package com.marioalonso.enclave.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.entities.SecretEntity

/**
 * Interfaz DAO para acceder a la base de datos de Enclave.
 *
 */
@Dao
interface EnclaveDao {

    // region Insert
    /**
     * Inserta un nuevo secreto en la base de datos.
     *
     * @param secret Secreto a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecret(secret: SecretEntity)

    /**
     * Inserta una nueva carpeta en la base de datos.
     *
     * @param secrets Carpeta a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(secrets: Folder)
    // endregion

    // region Delete

    /**
     * Elimina todos los secretos y carpetas de la base de datos.
     *
     */
    @Query("DELETE FROM secrets")
    suspend fun deleteAllSecrets()

    /**
     * Elimina todas las carpetas de la base de datos.
     *
     */
    @Query("DELETE FROM folders")
    suspend fun deleteAllFolders()

    /**
     * Elimina un secreto de la base de datos por su ID.
     *
     * @param id ID del secreto a eliminar.
     */
    @Query("DELETE FROM secrets WHERE id = :id")
    suspend fun deleteSecretById(id: String)

    /**
     * Elimina una carpeta de la base de datos por su ID.
     *
     * @param id ID de la carpeta a eliminar.
     */
    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolderById(id: String)
    // endregion

    // region Update

    /**
     * Actualiza un secreto en la base de datos.
     *
     * @param secret Secreto a actualizar.
     */
    @Update
    suspend fun updateSecret(secret: SecretEntity)
    // endregion

    // region Query

    /**
     * Obtiene todos los secretos de la base de datos de forma síncrona.
     *
     * @return Lista de secretos.
     */
    @Query("SELECT * FROM secrets")
    suspend fun getAllSecretsSync(): List<SecretEntity>

    /**
     * Obtiene todas las carpetas de la base de datos de forma síncrona.
     *
     * @return Lista de carpetas.
     */
    @Query("SELECT * FROM secrets")
    fun getAllSecrets(): LiveData<List<SecretEntity>>

    /**
     * Obtiene todas las carpetas de la base de datos de forma síncrona.
     *
     * @return Lista de carpetas.
     */
    @Query("SELECT * FROM folders")
    fun getAllFolders(): LiveData<List<Folder>>

    /**
     * Obtiene un secreto por su ID.
     *
     * @param id ID del secreto.
     * @return Secreto o null si no existe.
     */
    @Query("SELECT * FROM secrets WHERE id = :id")
    fun getSecretById(id: String): LiveData<SecretEntity?>

    /**
     * Obtiene todos los secretos que pertenecen a una carpeta específica.
     *
     * @param folderId ID de la carpeta.
     * @return Lista de secretos.
     */
    @Query("SELECT * FROM secrets WHERE folderId = :folderId")
    fun getSecretsByFolderId(folderId: String): LiveData<List<SecretEntity>>

    /**
     * Obtiene una carpeta por su ID.
     *
     * @param folderId ID de la carpeta.
     * @return Carpeta o null si no existe.
     */
    @Query("SELECT * FROM folders WHERE id = :folderId")
    fun getFolderById(folderId: String): LiveData<Folder?>
    // endregion
}