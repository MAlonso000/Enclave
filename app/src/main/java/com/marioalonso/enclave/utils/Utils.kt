package com.marioalonso.enclave.utils

import com.marioalonso.enclave.R

/**
 * Utilidades para la gestión y validación de contraseñas.
 */
object PasswordUtils{

    /**
     * Verifica si una contraseña es segura.
     *
     * Una contraseña se considera segura si:
     * - Tiene al menos 6 caracteres.
     * - Contiene al menos una letra mayúscula.
     * - Contiene al menos una letra minúscula.
     * - Contiene al menos un dígito.
     *
     * @param password La contraseña a verificar.
     * @return Triple donde el primer valor indica si la contraseña es segura,
     *         el segundo es el ID del recurso de cadena para mensaje corto,
     *         y el tercero es el ID del recurso de cadena para mensaje extendido.
     */
    fun isPasswordSecure(password: String): Triple<Boolean, Int, Int> {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val isLongEnough = password.length >= 6

        return Triple(
            hasUpperCase && hasLowerCase && hasDigit && isLongEnough,
            R.string.password_not_secure,
            R.string.password_not_secure_extended
        )
    }

    /**
     * Calcula la fortaleza de una contraseña y devuelve una puntuación del 1 al 5.
     *
     * La puntuación se basa en los siguientes criterios (especificados por el NIST):
     * - Longitud de la contraseña.
     * - Presencia de letras minúsculas.
     * - Presencia de letras mayúsculas.
     * - Presencia de números.
     * - Presencia de símbolos.
     *
     * @param password La contraseña a evaluar.
     * @return Un entero entre 1 (muy débil) y 5 (muy fuerte).
     */
    fun calculatePasswordStrength(password: String): Int {
        if (password.isBlank()) {
            return 1
        }

        var score = 0

        // Criterio 1: Longitud
        if (password.length >= 8) {
            score++
        }
        if (password.length >= 12) {
            score++
        }

        // Criterio 2: Variedad de caracteres
        val hasLowercase = password.any { it.isLowerCase() }
        val hasUppercase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }

        val varietyCount = listOf(hasLowercase, hasUppercase, hasDigit, hasSymbol).count { it }

        score += when (varietyCount) {
            1 -> 0 // Solo un tipo de caracter no añade fortaleza
            2 -> 1
            3 -> 2
            4 -> 3
            else -> 0
        }

        // Mapear la puntuación total (0-5) a una escala final de 1 a 5
        return (score + 1).coerceAtMost(5)
    }
}

