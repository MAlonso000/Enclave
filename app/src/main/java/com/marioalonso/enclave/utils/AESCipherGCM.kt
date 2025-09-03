package com.marioalonso.enclave.utils

import android.content.Context
import androidx.core.content.edit
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Objeto para manejar el cifrado y descifrado AES-GCM con derivación de clave PBKDF2.
 * Incluye funciones para inicializar la clave, verificar la contraseña,
 * y almacenar/recuperar texto de verificación cifrado.
 *
 */
object AESCipherGCM {

    private var cryptoKey: SecretKey? = null
    private var isAuthenticated = false

    private const val PREFS_NAME = "encrypted_prefs"
    private const val VERIFICATION_KEY = "verification_text"
    private const val AUTH_STATE_KEY = "auth_state"

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256 // bits
    private const val IV_SIZE = 12 // bytes
    private const val TAG_SIZE = 128 // bits
    private const val SALT_SIZE = 16 // bytes
    private const val ITERATIONS = 100_000

    /**
     * Inicializa la clave de cifrado derivada de la contraseña del usuario.
     * Si ya existe una sal almacenada, la utiliza; de lo contrario, genera una nueva.
     * Guarda el estado de autenticación en SharedPreferences.
     *
     * @param context Contexto de la aplicación
     * @param password Contraseña del usuario
     */
    fun initializeKey(context: Context, password: String) {
        // Si el salt ya existe, usarla; si no, generar una nueva y guardarla
        val existingSalt = getSalt(context)
        val salt = existingSalt ?: generateSalt().also { saveSalt(context, it) }
        // Derivar la clave y guardarla en memoria
        val key = deriveKey(password, salt)
        cryptoKey = key
        isAuthenticated = true
        saveAuthState(context, true)
    }

    /**
     * Verifica si la clave de cifrado está inicializada y si el usuario está autenticado.
     * Si la clave no está en memoria pero el usuario estaba autenticado, intenta recuperar la sesión.
     *
     * @param context Contexto de la aplicación
     * @return true si la clave está inicializada y el usuario autenticado, false en caso contrario
     */
    fun isKeyInitialized(context: Context): Boolean {
        // Si la clave está en memoria, todo bien
        if (cryptoKey != null) return true

        // Si no está en memoria pero estaba autenticado, intentar recuperar la sesión
        if (getAuthState(context)) {
            return false // La clave se perdió pero estaba autenticado
        }

        // No estaba autenticado
        return false
    }

    /**
     * Guarda el estado de autenticación en SharedPreferences.
     *
     * @param context Contexto de la aplicación
     * @param state Estado de autenticación (true si autenticado, false si no)
     */
    private fun saveAuthState(context: Context, state: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit { putBoolean(AUTH_STATE_KEY, state) }
    }

    /**
     * Recupera el estado de autenticación desde SharedPreferences.
     *
     * @param context Contexto de la aplicación
     * @return true si el usuario estaba autenticado, false en caso contrario
     */
    private fun getAuthState(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(AUTH_STATE_KEY, false)
    }

    /**
     * Inicializa el texto de verificación cifrado y lo almacena en SharedPreferences.
     * Este texto se utiliza para verificar la contraseña del usuario.
     *
     * @param context Contexto de la aplicación
     */
    fun initializeVerificationText(context: Context) {
        val verificationText = "verification_text"
        val encryptedText = encrypt(verificationText)
        storeVerificationText(context, encryptedText)
    }

    /**
     * Almacena el texto de verificación cifrado en SharedPreferences.
     *
     * @param context Contexto de la aplicación
     * @param encryptedText Texto de verificación cifrado
     */
    fun storeVerificationText(context: Context, encryptedText: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit { putString(VERIFICATION_KEY, encryptedText) }
    }

    /**
     * Recupera el texto de verificación cifrado desde SharedPreferences.
     *
     * @param context Contexto de la aplicación
     * @return Texto de verificación cifrado o null si no existe
     */
    fun getVerificationText(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(VERIFICATION_KEY, null)
    }

    /**
     * Verifica la contraseña del usuario intentando descifrar el texto de verificación.
     * Si la verificación es exitosa, inicializa la clave de cifrado en memoria.
     *
     * @param context Contexto de la aplicación
     * @param password Contraseña del usuario
     * @return true si la contraseña es correcta, false en caso contrario
     */
    fun verifyPassword(context: Context, password: String): Boolean {
        val salt = getSalt(context) ?: return false
        val derivedKey = deriveKey(password, salt)

        val encryptedText = getVerificationText(context) ?: return false
        return try {
            decrypt(encryptedText, verifyingKey = derivedKey)
            initializeKey(context, password)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Obtiene la clave de cifrado actual.
     *
     * @return Clave de cifrado o null si no está inicializada
     */
    fun getCryptoKey(): SecretKey? {
        return cryptoKey
    }

    /**
     * Limpia la clave de cifrado de la memoria y marca al usuario como no autenticado.
     */
    fun clearCryptoKey() {
        cryptoKey = null
        isAuthenticated = false
    }

    /**
     * Cierra la sesión del usuario, limpiando la clave de cifrado y actualizando el estado de autenticación.
     *
     * @param context Contexto de la aplicación
     */
    fun logout(context: Context) {
        clearCryptoKey()
        saveAuthState(context, false)
    }

    /**
     * Genera una nueva sal aleatoria.
     *
     * @return Array de bytes que representa la sal
     */
    fun generateSalt(): ByteArray {
        return ByteArray(SALT_SIZE).apply {
            SecureRandom().nextBytes(this)
        }
    }

    /**
     * Recupera la sal almacenada en SharedPreferences.
     *
     * @param context Contexto de la aplicación
     * @return Array de bytes que representa la sal o null si no existe
     */
    fun getSalt(context: Context): ByteArray? {
        val sharedPreferences = context.getSharedPreferences("enclave_prefs", Context.MODE_PRIVATE)
        val saltBase64 = sharedPreferences.getString("crypto_salt", null)
        return saltBase64?.let { android.util.Base64.decode(it, android.util.Base64.DEFAULT) }
    }

    /**
     * Guarda la sal en SharedPreferences.
     *
     * @param context Contexto de la aplicación
     * @param salt Array de bytes que representa la sal
     */
    fun saveSalt(context: Context, salt: ByteArray) {
        val sharedPreferences = context.getSharedPreferences("enclave_prefs", Context.MODE_PRIVATE)
        val saltBase64 = android.util.Base64.encodeToString(salt, android.util.Base64.DEFAULT)
        sharedPreferences.edit() { putString("crypto_salt", saltBase64) }
    }

    /**
     * Deriva una clave AES a partir de una contraseña y una sal utilizando PBKDF2.
     *
     * @param password Contraseña del usuario
     * @param salt Sal utilizada para la derivación
     * @return Clave secreta derivada
     */
    fun deriveKey(password: String, salt: ByteArray): SecretKey {
        val passwordCharArray = password.toCharArray()
        try {
            // Derivar la clave usando PBKDF2 con HMAC-SHA256
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(passwordCharArray, salt, ITERATIONS, KEY_SIZE)
            val secret = factory.generateSecret(spec)
            return SecretKeySpec(secret.encoded, "AES")
        } finally {
            passwordCharArray.fill('\u0000')
        }
    }

    /**
     * Cifra un texto plano utilizando AES-GCM.
     * Si se especifica, utiliza una sal para derivar la clave.
     *
     * @param plaintext Texto plano a cifrar
     * @return Texto cifrado en Base64
     */
    fun encrypt(plaintext: String): String {
        // Asegurarse de que la clave esté inicializada
        val currentCryptoKey = cryptoKey ?: throw IllegalStateException("Clave de cifrado no inicializada")

        val key = currentCryptoKey

        // Generar un IV aleatorio
        val iv = ByteArray(IV_SIZE).apply {
            SecureRandom().nextBytes(this)
        }

        // Cifrar el texto
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))
        val encrypted = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        // Combinar IV y texto cifrado
        val combined = iv + encrypted
        return Base64.getEncoder().encodeToString(combined)
    }

    /**
     * Descifra un texto cifrado utilizando AES-GCM.
     * Si se especifica, utiliza una sal para derivar la clave.
     *
     * @param ciphertextBase64 Texto cifrado en Base64
     * @param verifyingKey Clave secreta para verificar (opcional)
     * @return Texto plano descifrado
     */
    fun decrypt(ciphertextBase64: String, verifyingKey: SecretKey? = null): String {
        // Asegurarse de que la clave esté inicializada
        val cryptoKey = verifyingKey ?: getCryptoKey() ?:
            throw IllegalStateException("Clave de cifrado no inicializada")

        val combined = Base64.getDecoder().decode(ciphertextBase64)

        // Extraer IV y texto cifrado
        val iv = combined.copyOfRange(0, IV_SIZE)
        val encrypted = combined.copyOfRange(IV_SIZE, combined.size)

        val key = cryptoKey

        // Descifrar el texto
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))
        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }
}