package com.marioalonso.enclave.utils

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object AESCipherGCM {

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256 // bits
    private const val IV_SIZE = 12 // bytes (96 bits, recomendado para GCM)
    private const val TAG_SIZE = 128 // bits
    private const val SALT_SIZE = 16 // bytes
    private const val ITERATIONS = 100_000

    fun generateSalt(): ByteArray {
        return ByteArray(SALT_SIZE).apply {
            SecureRandom().nextBytes(this)
        }
    }

    fun deriveKey(password: CharArray, salt: ByteArray): SecretKey {
        try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_SIZE)
            val secret = factory.generateSecret(spec)
            return SecretKeySpec(secret.encoded, "AES")
        } finally {
            // Clear the password from memory
            password.fill('\u0000')
        }
    }

//    fun encrypt(plaintext: String, masterKey: SecretKey, debug: Boolean = false): String {
//        val salt = AESCipherGCM.generateSalt()
//        var mutableMasterKey = masterKey
//
//        // Convertir el ByteArray de masterKey.encoded a CharArray
//        val masterKeyChars = mutableMasterKey.encoded.toString(Charsets.UTF_8).toCharArray()
//
//        // Derivar una nueva clave a partir de la masterKey y el salt
//        val derivedKey = AESCipherGCM.deriveKey(masterKeyChars, salt)
//
//        // Generar un IV único
//        val iv = ByteArray(AESCipherGCM.IV_SIZE).apply {
//            SecureRandom().nextBytes(this)
//        }
//
//        // Cifrar el texto
//        val cipher = Cipher.getInstance(AESCipherGCM.TRANSFORMATION)
//        cipher.init(Cipher.ENCRYPT_MODE, derivedKey, GCMParameterSpec(AESCipherGCM.TAG_SIZE, iv))
//        val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
//
//        // Combinar salt + IV + texto cifrado y codificar en Base64
//        val combined = salt + iv + encrypted
//        return Base64.getEncoder().encodeToString(combined)
//    }
//
//    fun decrypt(ciphertextBase64: String, masterKey: SecretKey, debug: Boolean = false): String {
//        // Decodificar el texto cifrado
//        val combined = Base64.getDecoder().decode(ciphertextBase64)
//        var mutableMasterKey = masterKey
//
//        // Extraer el salt, IV y texto cifrado
//        val salt = combined.copyOfRange(0, AESCipherGCM.SALT_SIZE)
//        val iv = combined.copyOfRange(AESCipherGCM.SALT_SIZE, AESCipherGCM.SALT_SIZE + AESCipherGCM.IV_SIZE)
//        val encrypted = combined.copyOfRange(AESCipherGCM.SALT_SIZE + AESCipherGCM.IV_SIZE, combined.size)
//
//        // Convertir el ByteArray de masterKey.encoded a CharArray
//        val masterKeyChars = mutableMasterKey.encoded.toString(Charsets.UTF_8).toCharArray()
//
//        // Derivar la clave usando la masterKey y el salt
//        val derivedKey = AESCipherGCM.deriveKey(masterKeyChars, salt)
//
//        // Descifrar el texto
//        val cipher = Cipher.getInstance(AESCipherGCM.TRANSFORMATION)
//        cipher.init(Cipher.DECRYPT_MODE, derivedKey, GCMParameterSpec(AESCipherGCM.TAG_SIZE, iv))
//        val decrypted = cipher.doFinal(encrypted)
//        return String(decrypted, Charsets.UTF_8)
//    }

    fun encrypt(plaintext: String, masterKey: SecretKey, useSalt: Boolean = false): String {
        val salt = if (useSalt) generateSalt() else ByteArray(0)
        val key = if (useSalt) {
            val masterKeyChars = masterKey.encoded.toString(Charsets.UTF_8).toCharArray()
            deriveKey(masterKeyChars, salt)
        } else {
            masterKey
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

    fun decrypt(ciphertextBase64: String, masterKey: SecretKey, useSalt: Boolean = false): String {
        val combined = Base64.getDecoder().decode(ciphertextBase64)
        val salt = if (useSalt) combined.copyOfRange(0, SALT_SIZE) else ByteArray(0)
        val iv = combined.copyOfRange(if (useSalt) SALT_SIZE else 0, (if (useSalt) SALT_SIZE else 0) + IV_SIZE)
        val encrypted = combined.copyOfRange((if (useSalt) SALT_SIZE else 0) + IV_SIZE, combined.size)

        val key = if (useSalt) {
            val masterKeyChars = masterKey.encoded.toString(Charsets.UTF_8).toCharArray()
            deriveKey(masterKeyChars, salt)
        } else {
            masterKey
        }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }
}