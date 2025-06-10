package com.marioalonso.enclave.dao

import androidx.room.*
import com.marioalonso.enclave.entities.*

@Dao
interface EnclaveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSecret(secret: SecretEntity)

    @Update
    suspend fun updateSecret(secret: SecretEntity)

    @Query("DELETE FROM secrets WHERE id = :id")
    suspend fun deleteSecretById(id: String)

    @Query("SELECT * FROM secrets WHERE id = :id")
    suspend fun getSecretById(id: String): SecretEntity?

    @Query("SELECT * FROM secrets")
    suspend fun getAllSecrets(): List<SecretEntity>
}