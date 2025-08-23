package com.marioalonso.enclave.utils

import com.marioalonso.enclave.R

object PasswordUtils{

    fun isPasswordSecure(password: String): Triple<Boolean, Int, Int> {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val isLongEnough = password.length >= 6

//        return Triple(true, 0, 0)

        return Triple(
            hasUpperCase && hasLowerCase && hasDigit && isLongEnough,
            R.string.password_not_secure,
            R.string.password_not_secure_extended
        )
    }
}

