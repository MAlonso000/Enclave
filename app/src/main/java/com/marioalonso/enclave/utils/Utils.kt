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
}

