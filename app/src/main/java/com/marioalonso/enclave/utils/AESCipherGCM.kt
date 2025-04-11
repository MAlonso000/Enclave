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

    fun deriveKey(password: String, salt: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE)
        val secret = factory.generateSecret(spec)
        return SecretKeySpec(secret.encoded, "AES")
    }

    fun hashPassword(password: String, salt: ByteArray): String {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE)
        val hash = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(salt + hash) // Combine salt + hash
    }

    fun verifyPassword(inputPassword: String, storedHash: String): Boolean {
        val decoded = Base64.getDecoder().decode(storedHash)
        val salt = decoded.copyOfRange(0, SALT_SIZE)
        val storedHashBytes = decoded.copyOfRange(SALT_SIZE, decoded.size)

        val inputHash = hashPassword(inputPassword, salt)
        val inputHashBytes = Base64.getDecoder().decode(inputHash).copyOfRange(SALT_SIZE, decoded.size)

        return storedHashBytes.contentEquals(inputHashBytes)
    }

    fun encrypt(plaintext: String, password: String): String {
        val salt = generateSalt()
        val key = deriveKey(password, salt)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val iv = ByteArray(IV_SIZE).apply {
            SecureRandom().nextBytes(this)
        }
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))
        val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        // Combinar salt + IV + texto cifrado y codificar en Base64
        val combined = salt + iv + encrypted
        return Base64.getEncoder().encodeToString(combined)
    }

    fun decrypt(ciphertextBase64: String, password: String): String {
        val combined = Base64.getDecoder().decode(ciphertextBase64)
        val salt = combined.copyOfRange(0, SALT_SIZE)
        val iv = combined.copyOfRange(SALT_SIZE, SALT_SIZE + IV_SIZE)
        val encrypted = combined.copyOfRange(SALT_SIZE + IV_SIZE, combined.size)

        val key = deriveKey(password, salt)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }
}

fun main3() {
    val masterPassword = "miClaveMaestra123"
    val plaintext = "Este es un mensaje secreto"

    // Cifrar
    val encrypted = AESCipherGCM.encrypt(plaintext, masterPassword)
    println("Encrypted: $encrypted")

    // Descifrar
    val decrypted = AESCipherGCM.decrypt(encrypted, masterPassword)
    println("Decrypted: $decrypted")
}

fun main() {
    while (true) {
        println("\nChoose an option:")
        println("1. Encrypt")
        println("2. Decrypt")
        println("3. Exit")
        print("Option: ")

        when (readln().toIntOrNull()) {
            1 -> {
                print("Enter the password for encryption: ")
                val password = readln()
                val salt = AESCipherGCM.generateSalt()
                val key = AESCipherGCM.deriveKey(password, salt)

                print("Enter the text to encrypt: ")
                val plaintext = readln()
                val result = AESCipherGCM.encrypt(plaintext, password)

                println("Encrypted text (store this): $result")
            }

            2 -> {
                print("Enter the password for decryption: ")
                val password = readln()

                print("Enter the encrypted text (including salt): ")
                val encryptedInput = readln()
                println("Decrypted text: ${AESCipherGCM.decrypt(encryptedInput, password)}")
            }

            3 -> {
                println("Exiting the application. Goodbye!")
                break
            }

            else -> println("Invalid option. Please try again.")
        }
    }
}