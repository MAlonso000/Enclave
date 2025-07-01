package com.marioalonso.enclave.utils

import android.content.Context
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import androidx.core.content.edit
import kotlin.apply

object AESCipherGCM {

    private var cryptoKey: SecretKey? = null

    private const val PREFS_NAME = "encrypted_prefs"
    private const val VERIFICATION_KEY = "verification_text"

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256 // bits
    private const val IV_SIZE = 12 // bytes (96 bits, recomendado para GCM)
    private const val TAG_SIZE = 128 // bits
    private const val SALT_SIZE = 16 // bytes
    private const val ITERATIONS = 100_000

    fun initializeKey(context: Context, password: String) {
        if (cryptoKey != null) {
            return // La clave ya está inicializada
        }
        val existingSalt = getSalt(context)
        val salt = existingSalt ?: generateSalt().also { saveSalt(context, it) }
        val key = deriveKey(password, salt)
        cryptoKey = key
    }

    fun initializeVerificationText(context: Context) {
        val verificationText = "verification_text"
        val encryptedText = encrypt(verificationText)
        storeVerificationText(context, encryptedText)
    }

    fun storeVerificationText(context: Context, encryptedText: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit() { putString(VERIFICATION_KEY, encryptedText) }
    }

    fun getVerificationText(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(VERIFICATION_KEY, null)
    }

    fun verifyPassword(context: Context, password: String): Boolean {
        // Cogemos el salt y si es null devolvemos false
        val salt = getSalt(context) ?: return false
        // Derivamos la clave de cifrado a partir de la contraseña y el salt
        val derivedKey = deriveKey(password, salt)

        val encryptedText = getVerificationText(context) ?: return false
        return try {
            decrypt(encryptedText, verifyingKey = derivedKey)
            // Si la desencriptación es exitosa, inicializamos la contraseña y devolvemos true
            initializeKey(context, password)
            true
        } catch (e: Exception) {
            false // Si ocurre un error, la contraseña es incorrecta
        }
    }

    fun getCryptoKey(): SecretKey? {
        return cryptoKey
    }

    fun clearCryptoKey() {
        cryptoKey = null // Borra la clave de memoria
    }

    fun generateSalt(): ByteArray {
        return ByteArray(SALT_SIZE).apply {
            SecureRandom().nextBytes(this)
        }
    }

    fun getSalt(context: Context): ByteArray? {
        val sharedPreferences = context.getSharedPreferences("enclave_prefs", Context.MODE_PRIVATE)
        val saltBase64 = sharedPreferences.getString("crypto_salt", null)
        return saltBase64?.let { android.util.Base64.decode(it, android.util.Base64.DEFAULT) }
    }

    fun saveSalt(context: Context, salt: ByteArray) {
        val sharedPreferences = context.getSharedPreferences("enclave_prefs", Context.MODE_PRIVATE)
        val saltBase64 = android.util.Base64.encodeToString(salt, android.util.Base64.DEFAULT)
        sharedPreferences.edit() { putString("crypto_salt", saltBase64) }
    }

    fun deriveKey(password: String, salt: ByteArray): SecretKey {
        val passwordCharArray = password.toCharArray()
        try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(passwordCharArray, salt, ITERATIONS, KEY_SIZE)
            val secret = factory.generateSecret(spec)
            return SecretKeySpec(secret.encoded, "AES")
        } finally {
            // Clear the password from memory
            passwordCharArray.fill('\u0000')
        }
    }

    fun encrypt(plaintext: String, useSalt: Boolean = false): String {
        val currentCryptoKey = cryptoKey ?: throw IllegalStateException("Clave de cifrado no inicializada")

        val salt = if (useSalt) generateSalt() else ByteArray(0)
        val key = if (useSalt) {
            val masterKeyChars = currentCryptoKey.encoded.toString(Charsets.UTF_8)
            deriveKey(masterKeyChars, salt)
        } else {
            currentCryptoKey
        }

        val iv = ByteArray(IV_SIZE).apply {
            SecureRandom().nextBytes(this)
        }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))
        val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        val combined = salt + iv + encrypted
        return Base64.getEncoder().encodeToString(combined)
    }

    fun decrypt(ciphertextBase64: String, useSalt: Boolean = false, verifyingKey: SecretKey? = null): String {
        val cryptoKey = verifyingKey ?: getCryptoKey() ?:
            throw IllegalStateException("Clave de cifrado no inicializada")

        val combined = Base64.getDecoder().decode(ciphertextBase64)
        val salt = if (useSalt) combined.copyOfRange(0, SALT_SIZE) else ByteArray(0)
        val iv = combined.copyOfRange(if (useSalt) SALT_SIZE else 0, (if (useSalt) SALT_SIZE else 0) + IV_SIZE)
        val encrypted = combined.copyOfRange((if (useSalt) SALT_SIZE else 0) + IV_SIZE, combined.size)

        val key = if (useSalt) {
            val masterKeyChars = cryptoKey.encoded.toString(Charsets.UTF_8)
            deriveKey(masterKeyChars, salt)
        } else {
            cryptoKey
        }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }
}