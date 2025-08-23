package com.marioalonso.enclave.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.entities.*

@Dao
interface EnclaveDao {

    // region Insert

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecret(secret: SecretEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(secrets: Folder)
    // endregion

    // region Delete

    @Query("DELETE FROM secrets")
    suspend fun deleteAllSecrets()

    @Query("DELETE FROM folders")
    suspend fun deleteAllFolders()

    @Query("DELETE FROM secrets WHERE id = :id")
    suspend fun deleteSecretById(id: String)

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteFolderById(id: String)
    // enregion

    // region Update

    @Update
    suspend fun updateSecret(secret: SecretEntity)
    // endregion

    // region Query

    @Query("SELECT * FROM secrets")
    suspend fun getAllSecretsSync(): List<SecretEntity>

    @Query("SELECT * FROM secrets")
    fun getAllSecrets(): LiveData<List<SecretEntity>>

    @Query("SELECT * FROM folders")
    fun getAllFolders(): LiveData<List<Folder>>

    @Query("SELECT * FROM secrets WHERE id = :id")
    fun getSecretById(id: String): LiveData<SecretEntity?>

    @Query("SELECT * FROM secrets WHERE folderId = :folderId")
    fun getSecretsByFolderId(folderId: String): LiveData<List<SecretEntity>>

    @Query("SELECT * FROM folders WHERE id = :folderId")
    fun getFolderById(folderId: String): LiveData<Folder?>
    // endregion
}