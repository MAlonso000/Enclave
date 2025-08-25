package com.marioalonso.enclave.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.marioalonso.enclave.classes.Folder
import com.marioalonso.enclave.dao.EnclaveDao
import com.marioalonso.enclave.entities.*

/**
 * Base de datos Room para la aplicación Enclave.
 *
 */
@Database(
    entities = [
        SecretEntity::class,
        Folder::class
    ],
    version = 10, // Incrementa la versión de la base de datos
    exportSchema = true
)
abstract class EnclaveDatabase : RoomDatabase() {
    abstract val enclaveDao: EnclaveDao

    companion object {
        @Volatile
        private var INSTANCE: EnclaveDatabase? = null

        fun getInstance(context: Context): EnclaveDatabase {
            var instance = INSTANCE

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    EnclaveDatabase::class.java,
                    "enclave_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }
            return instance
        }
    }
}