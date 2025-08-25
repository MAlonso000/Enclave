package com.marioalonso.enclave.utils

import java.security.SecureRandom

/**
 * Generador de contraseñas seguras con opciones personalizables.
 */
object PasswordGenerator {

    private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
    private const val DIGITS = "0123456789"
    private const val SYMBOLS = "!@#$%^&*"
    private const val AMBIGUOUS = "O0lI1"

    private val secureRandom = SecureRandom()

    /**
     * Genera una contraseña segura.
     *
     * @param length Longitud de la contraseña (mínimo 6).
     * @param useUpper Incluir letras mayúsculas.
     * @param useLower Incluir letras minúsculas.
     * @param useDigits Incluir dígitos.
     * @param useSymbols Incluir símbolos especiales.
     * @param avoidAmbiguous Evitar caracteres ambiguos como 'O', '0', 'l', '1', 'I'.
     * @return Contraseña generada.
     * @throws IllegalArgumentException Si la longitud es menor a 6 o si no se selecciona ningún tipo de carácter.
     */
    fun generate(
        length: Int = 16,
        useUpper: Boolean = true,
        useLower: Boolean = true,
        useDigits: Boolean = true,
        useSymbols: Boolean = true,
        avoidAmbiguous: Boolean = false
    ): String {
        if (length < 6) throw IllegalArgumentException("Longitud muy corta")
        if (!useUpper && !useLower && !useDigits && !useSymbols) {
            throw IllegalArgumentException("Debes activar al menos un tipo de carácter")
        }
        val pool = buildString {
            if (useUpper) append(UPPER)
            if (useLower) append(LOWER)
            if (useDigits) append(DIGITS)
            if (useSymbols) append(SYMBOLS)
        }.let { pool ->
            if (avoidAmbiguous) pool.filterNot { it in AMBIGUOUS } else pool
        }

        if (pool.isEmpty()) throw IllegalArgumentException("Debes activar al menos un tipo de carácter")

        return (1..length)
            .map { pool[secureRandom.nextInt(pool.length)] }
            .joinToString("")
    }
}